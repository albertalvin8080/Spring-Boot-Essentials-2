package com.albert.springbootessentials2.client;

import com.albert.springbootessentials2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class RestTemplateTestPUT {
    public static void main(String[] args) {
        // the attributes of Anime and AnimePUTBody have the same name,
        // and thus they are automatically mapped by Jackson

        final Anime build = Anime.builder().name("Mushishi").id(7L).build();
        // returns void
        new RestTemplate().put("http://localhost:8080/animes", build);

        final Anime build2 = Anime.builder().name("Witch from Mercury").id(1L).build();
        // preferable because has a return with http status and other information
        final ResponseEntity<Void> exchange =
                new RestTemplate().exchange(
                        "http://localhost:8080/animes",
                        HttpMethod.PUT,
                        new HttpEntity<>(build2, createJOSONHeaders()),
                        Void.class);

        log.info(exchange);
    }

    private static HttpHeaders createJOSONHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
