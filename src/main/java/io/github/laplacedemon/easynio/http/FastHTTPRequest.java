package io.github.laplacedemon.easynio.http;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FastHTTPRequest {
    private static final String CRLF = "\r\n";
    private static ThreadLocal<StringBuilder> LOCAL_STRING_BUILDER = ThreadLocal.withInitial(()-> new StringBuilder(1024));
    private StringBuilder httpRequestStringBuilder;

    private FastHTTPRequest() {
    }

    public static FastHTTPRequest method(HTTPMethod httpMethod, String uri) {
        FastHTTPRequest fastHTTPRequest = new FastHTTPRequest();
        StringBuilder stringBuilder = LOCAL_STRING_BUILDER.get();
        stringBuilder.setLength(0);
        fastHTTPRequest.httpRequestStringBuilder = stringBuilder;
        fastHTTPRequest.httpRequestStringBuilder.append(httpMethod.name());
        fastHTTPRequest.httpRequestStringBuilder.append(" ");
        fastHTTPRequest.httpRequestStringBuilder.append(uri);
        fastHTTPRequest.httpRequestStringBuilder.append(" ");
        fastHTTPRequest.httpRequestStringBuilder.append(HTTPVersion.HTTP_1_1);
        fastHTTPRequest.httpRequestStringBuilder.append(CRLF);
        return fastHTTPRequest;
    }

    public FastHTTPRequest addHTTPHeader(String name, String value) {
        httpRequestStringBuilder.append(name);
        httpRequestStringBuilder.append(": ");
        httpRequestStringBuilder.append(value);
        httpRequestStringBuilder.append(CRLF);
        return this;
    }

    public FastHTTPRequest httpContent(String content) {
        addHTTPHeader("Content-Length", String.valueOf(content.length()));
        httpRequestStringBuilder.append(CRLF);
        httpRequestStringBuilder.append(content);
        return this;
    }

    public ByteBuffer build() {
        return ByteBuffer.wrap(httpRequestStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
