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
