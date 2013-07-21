package com.jamierf.rcdroid.http.handler;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class JsonPOJOHandler implements HttpHandler {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final Object pojo;

    public JsonPOJOHandler(Object pojo) {
        this.pojo = pojo;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        final String json = JSON.writeValueAsString(pojo);

        // Write the pojo as json
        response.status(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
                .content(json)
                .end();
    }
}
