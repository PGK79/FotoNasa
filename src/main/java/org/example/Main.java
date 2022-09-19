package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String URL
            = "https://api.nasa.gov/planetary/apod?api_key=0DL3nl2MkISsG2ONk0XM9GOQok8MbWY25EKc1iB2";

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);

        Photo posts = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
                }
        );

        response.close();

        HttpGet requestDos = new HttpGet(posts.url);
        requestDos.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse responseWithPhoto = httpClient.execute(requestDos);

        byte[] bytes = responseWithPhoto.getEntity().getContent().readAllBytes();

        String[] names = posts.url.split("/");
        String fileName = names[names.length - 1];

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        responseWithPhoto.close();
        httpClient.close();
    }
}