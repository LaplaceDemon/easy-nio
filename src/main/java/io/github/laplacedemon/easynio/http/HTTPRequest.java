package io.github.laplacedemon.easynio.http;

import java.util.ArrayList;

public class HTTPRequest {
    private HTTPMethod httpMethod;
    private HTTPVersion httpVersion;
    private ArrayList<String> httpHeaders;
    private String content;

    private HTTPRequest() {
        httpHeaders = new ArrayList<>();
    }

    public static HTTPRequest method(HTTPMethod httpMethod) {
        HTTPRequest httpRequest = new HTTPRequest();
        httpRequest.httpMethod = httpMethod;
        return httpRequest;
    }

    public HTTPRequest addHTTPHeader(String name, String value) {
        httpHeaders.add(name);
        httpHeaders.add(value);
        return this;
    }

    public HTTPRequest httpContent(String content) {
        this.content = content;
        return this;
    }

}
