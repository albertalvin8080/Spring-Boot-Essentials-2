package com.albert.springbootessentials2.client;

import com.albert.springbootessentials2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class RestTemplateTestGET {
    public static void main(String[] args) {
        final ResponseEntity<Anime> forEntity =
                new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 19);
        log.info(forEntity);

        final Anime forObject = new RestTemplate().getForObject("http://localhost:8080/animes/3", Anime.class);
        log.info(forObject);

//        final Anime[] array = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        final Anime[] array = new RestTemplate().getForObject("http://localhost:8080/animes/find?name=On", Anime[].class);
        log.info(Arrays.toString(array));

        final ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {});

        log.info(exchange);
    }
}
