package com.albert.springbootessentials2.client;

import com.albert.springbootessentials2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class RestTemplateTestPOST {
    public static void main(String[] args) {
        // bear in mind that the attributes of Anime and AnimePOSTBody have the same name,
        // and thus they are automatically mapped by Jackson

        Anime newAnime = Anime.builder().name("Boogiepop").build();
        final Anime postForObject =
                new RestTemplate().postForObject("http://localhost:8080/animes", newAnime, Anime.class);
        log.info("Saved anime: {}", postForObject);

        Anime newAnime2 = Anime.builder().name("Mushoku Tensei").build();
        final ResponseEntity<Anime> postForEntity =
                new RestTemplate().postForEntity("http://localhost:8080/animes", newAnime2, Anime.class);
        log.info("Saved anime2: {}", postForEntity);

    }
}
