package io.github.laplacedemon.easynio.http;

public enum HTTPVersion {
    HTTP_1_1("HTTP/1.1");

    private String name;

    HTTPVersion(String name) {
        this.name = name;
    }
}
