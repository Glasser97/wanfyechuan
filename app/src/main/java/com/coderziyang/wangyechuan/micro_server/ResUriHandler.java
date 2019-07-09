package com.coderziyang.wangyechuan.micro_server;

public interface ResUriHandler {
    /**
     * used to match the specify uri
     * @param uri
     * @return
     */
    boolean matches(String uri);

    /**
     * handler the request which matches the uri
     * @param request
     */
    void handler(Request request);

    /**
     * release the resource when finish the handler
     */
    void destroy();
}
