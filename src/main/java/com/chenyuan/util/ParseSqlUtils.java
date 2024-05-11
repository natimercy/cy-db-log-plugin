package com.chenyuan.util;

import com.chenyuan.ParamsEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ParseSqlUtils
 *
 * @author <a href="mailto:natimercy93@gmail.com">natimercy</a>
 * @version 1.0.0
 */
public class ParseSqlUtils {

    private static final Pattern PLACEHOLDERS_PATTERN = Pattern.compile("\\:(\\w+)");

    Pattern pattern = Pattern.compile("(\\\\w+)\\\\(name=')([^']+)\\\\2\\\\s*,\\\\s*(putType=\\\\w+),\\\\s*dataType=([^,]+),\\\\s*value=('[^']+)");

    private static final List<String> NEED_QUOTATION_MARKS = new ArrayList<>();

    public ParseSqlUtils() {
    }

    public static String generateSql(String str) {
        String resultSql = "";
        String precompiledSql = getPrecompiledSql(str);
        String parameters = getParams(str);
        List<ParamsEntity> paramsList = paramsToEntity(parameters);
        precompiledSql = handleProceduresWithoutCall(precompiledSql, paramsList);
        if (CollectionUtils.isNotEmpty(paramsList)) {
            return replacePlaceholdersInSql(precompiledSql, paramsList);
        }

        return resultSql;
    }

    public static String replacePlaceholdersInSql(String sqlTemplate, List<ParamsEntity> paramsList) {
        // 正则表达式匹配形如":identifier"的模式
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PLACEHOLDERS_PATTERN.matcher(sqlTemplate);
        while (matcher.find()) {
            // 获取匹配到的占位符名称（不包括冒号）
            String placeholder = matcher.group(1);
            ParamsEntity paramsEntity = findParamByName(paramsList, placeholder);
            if (Objects.nonNull(paramsEntity)) {
                String replacement = paramsEntity.getValue();
                matcher.appendReplacement(sb, replacement);
            }
        }

        // 将剩余的部分添加到StringBuilder中
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static ParamsEntity findParamByName(List<ParamsEntity> paramsList, String name) {
        for (ParamsEntity param : paramsList) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }

    private static String getPrecompiledSql(String str) {
        String sql = str.substring(0, str.indexOf("parameter"));
        Assert.assertNotNull("未获取到参数", sql);
        sql = sql.trim();
        return sql;
    }

    private static String getParams(String str) {
        return str.substring(str.indexOf("parameter"));
    }

    private static List<ParamsEntity> paramsToEntity(String params) {
        Assert.assertNotNull("解析错误", params);
        List<ParamsEntity> resultList = new ArrayList<>();
        String[] paramsArr = params.split("\n");
        List<String> list = Arrays.stream(paramsArr)
                .toList();

        for (String item : list) {
            item = item.replace("parameter(", "");
            item = item.replace(")", "");
            item = item.replace("'", "");
            String[] split = item.split(", ");
            String name = split[0].substring(split[0].indexOf("=") + 1);
            String dataType = split[2].substring(split[2].indexOf("=") + 1);
            String value = split[3].substring(split[3].indexOf("=") + 1);
            if (!"null".equals(value) && NEED_QUOTATION_MARKS.contains(dataType)) {
                value = "'" + value + "'";
            }

            ParamsEntity entity = new ParamsEntity();
            entity.setName(name);
            entity.setDataType(dataType);
            entity.setValue(value);
            resultList.add(entity);
        }

        return resultList;
    }

    public static boolean checkStart(String str) {
        return StringUtils.startsWithIgnoreCase(str, "call")
                || StringUtils.startsWithIgnoreCase(str, "select")
                || StringUtils.startsWithIgnoreCase(str, "update")
                || StringUtils.startsWithIgnoreCase(str, "insert")
                || StringUtils.startsWithIgnoreCase(str, "delete")
                || StringUtils.startsWithIgnoreCase(str, "p_");
    }

    private static String handleProceduresWithoutCall(String sql, List<ParamsEntity> paramsList) {
        if (sql.startsWith("p_") && CollectionUtils.isNotEmpty(paramsList)) {
            sql = "call " + sql + "(";
            String paramStr = "";
            for (ParamsEntity item : paramsList) {
                String paramName = ":" + item.getName();
                if (StringUtils.isBlank(paramStr)) {
                    paramStr = paramName;
                } else {
                    paramStr = String.join(", ", paramStr, paramName);
                }
            }

            sql = sql + paramStr + ");";
        }

        return sql;
    }

    static {
        NEED_QUOTATION_MARKS.add("CHAR");
        NEED_QUOTATION_MARKS.add("UUID");
        NEED_QUOTATION_MARKS.add("VARCHAR");
        NEED_QUOTATION_MARKS.add("LONGVARCHAR");
        NEED_QUOTATION_MARKS.add("DATE");
        NEED_QUOTATION_MARKS.add("TIME");
        NEED_QUOTATION_MARKS.add("TIMESTAMP");
        NEED_QUOTATION_MARKS.add("VARBINARY");
    }
}
