/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chenyuan.action;

import com.chenyuan.interceptor.AfterInterceptor;
import com.chenyuan.interceptor.Interceptor;
import com.chenyuan.util.ParseSqlUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import groovy.json.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.Objects;

/**
 * GenerateSqlAction
 *
 * @author <a href="mailto:natimercy93@gmail.com">natimercy</a>
 * @version 1.0.0
 */
public class GenerateSqlAction extends AnAction {

    private final Interceptor afterInterceptor = new AfterInterceptor();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (null != project) {
            Editor editor = event.getData(LangDataKeys.EDITOR);
            CaretModel caretModel = Objects.requireNonNull(editor).getCaretModel();
            Caret currentCaret = caretModel.getCurrentCaret();
            String selectedText = currentCaret.getSelectedText();

            String result;
            try {
                Assert.assertNotNull("请选择SQL和参数", selectedText);
                Assert.assertTrue("格式错误", ParseSqlUtils.checkStart(selectedText));
                result = ParseSqlUtils.generateSql(selectedText);
            } catch (AssertionError e) {
                Messages.showErrorDialog(project, e.getMessage(), "提示");
                return;
            }

            result = afterInterceptor.invoke(result);
            Messages.showMessageDialog(project, result, "Sql", Messages.getInformationIcon());
        }
    }
}
