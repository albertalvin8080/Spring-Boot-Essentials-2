package com.albert.springbootessentials2.client;

import com.albert.springbootessentials2.request.EntityID;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Log4j2
public class RestTemplateTestDELETE {
    public static void main(String[] args) {

        // returns void
        new RestTemplate().delete("http://localhost:8080/animes/{id}", 2);

        final ResponseEntity<Void> exchange =
                new RestTemplate().exchange(
                        "http://localhost:8080/animes/{id}",
                        HttpMethod.DELETE,
//                      new HttpEntity<>(createJOSONHeaders()), // can just be null
                        null,
                        Void.class,
                        7);
        log.info(exchange);

        final List<EntityID> entityIDS = List.of(
                new EntityID(40L), new EntityID(41L), new EntityID(42L), new EntityID(43L)
        );
        final ResponseEntity<Void> exchange2 = new RestTemplate().exchange(
                "http://localhost:8080/animes/deleteMany",
                HttpMethod.DELETE,
                new HttpEntity<>(entityIDS, createJOSONHeaders()),
                Void.class);
        log.info(exchange2);
    }

    private static HttpHeaders createJOSONHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
