package com.chenyuan.interceptor;

/**
 * Interceptor
 *
 * @author <a href="mailto:natimercy93@gmail.com">natimercy</a>
 * @version 1.0.0
 */
public interface Interceptor {

    String invoke(String sql);

}
