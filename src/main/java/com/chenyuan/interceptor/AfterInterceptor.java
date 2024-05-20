package com.chenyuan.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * AfterInterceptor
 *
 * @author <a href="mailto:natimercy93@gmail.com">natimercy</a>
 * @version 1.0.0
 */
public class AfterInterceptor implements Interceptor {

    public final Map<String, String> specialCharacters;

    public AfterInterceptor() {
        specialCharacters = new HashMap<>();
        specialCharacters.put("<", "&lt;");
        specialCharacters.put(">", "&gt;");
    }

    @Override

    public String invoke(String sql) {
        // 将特殊字符串转义
        String[] wrapper = new String[]{sql};
        specialCharacters.forEach((source, target) -> wrapper[0] = wrapper[0].replaceAll(source, target));
        return wrapper[0];
    }
}
