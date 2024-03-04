package com.albert.springbootessentials2.Integration;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.domain.AppUser;
import com.albert.springbootessentials2.repository.AnimeRepository;
import com.albert.springbootessentials2.repository.AppUserRepository;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import com.albert.springbootessentials2.request.AnimePUTBody;
import com.albert.springbootessentials2.request.EntityID;
import com.albert.springbootessentials2.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.albert.springbootessentials2.util.AnimeCreator.createAnimeListToSave;
import static com.albert.springbootessentials2.util.AnimeCreator.createAnimeToSave;
import static com.albert.springbootessentials2.util.AnimePOSTBodyCreator.createAnimePOSTBodyToSave;
import static com.albert.springbootessentials2.util.AnimePOSTBodyCreator.createAnimePOSTBodyListToSave;
import static com.albert.springbootessentials2.util.AnimePUTBodyCreator.createAnimePUTBodyToUpdate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
// deletes and destroys the database for each @Test method, granting isolation
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    @Qualifier(value = "trtRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "trtRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    // used only for picking the ${local.server.port} variable
    @LocalServerPort
    private int port;

    private static final AppUser USER;
    private static final AppUser ADMIN;
    private static final PasswordEncoder passwordEncoder;

    static {
        passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();

        USER = AppUser.builder()
                .name("Yamato Nadeshiko")
                .username("Yamato")
                .password(passwordEncoder.encode("1234"))
                .roles("ROLE_USER")
                .build();

        ADMIN = AppUser.builder()
                .name("Lucas Cucas")
                .username("Lucas")
                .password(passwordEncoder.encode("1234"))
                .roles("ROLE_USER,ROLE_ADMIN")
                .build();
    }

    @Lazy
    @TestConfiguration
    static class Config {
        @Bean(name = "trtRoleUser")
        public TestRestTemplate testRestTemplateRoleUser(@Value("${local.server.port}") String port) {
            String uri = "http://localhost:" + port;
            final RestTemplateBuilder builder = new RestTemplateBuilder()
                    .rootUri(uri)
                    .basicAuthentication(USER.getUsername(), "1234");
            return new TestRestTemplate(builder);
        }

        @Bean(name = "trtRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdmin(@Value("${local.server.port}") String port) {
            String uri = "http://localhost:" + port;
            final RestTemplateBuilder builder = new RestTemplateBuilder()
                    .rootUri(uri)
                    .basicAuthentication(ADMIN.getUsername(), "1234");
            return new TestRestTemplate(builder);
        }
    }

    @Test
    @DisplayName("listAll returns Page of Anime when successful")
    void listAll_ReturnsPageOfAnime_WhenSuccessful() {
        final Anime savedAnime = animeRepository.save(createAnimeToSave());
        appUserRepository.save(USER);

        final ResponseEntity<PageableResponse<Anime>> responseEntity =
                testRestTemplateRoleUser.exchange("/animes",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageableResponse<Anime>>() {
                        });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isNotEmpty()
                .contains(savedAnime);
    }

    @Test
    @DisplayName("listAll returns empty Page when no data is found")
    void listAll_ReturnsEmptyPage_WhenNoDataIsFound() {
        appUserRepository.save(USER);

        final ResponseEntity<PageableResponse<Anime>> responseEntity =
                testRestTemplateRoleUser.exchange("/animes",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageableResponse<Anime>>() {
                        });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("listAllNonPageable returns list of Anime when successful")
    void listAllNonPageable_ReturnsListOfAnime_WhenSuccessful() {
        appUserRepository.save(USER);

        final Anime savedAnime = animeRepository.save(createAnimeToSave());

        final ResponseEntity<List<Anime>> responseEntity = testRestTemplateRoleUser.exchange("/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Anime>>() {
                });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .contains(savedAnime);
    }

    @Test
    @DisplayName("listAllNonPageable returns empty list of Anime when no data is found")
    void listAllNonPageable_ReturnsEmptyList_WhenNoDataIsFound() {
        appUserRepository.save(USER);

        final ResponseEntity<List<Anime>> responseEntity = testRestTemplateRoleUser.exchange("/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Anime>>() {
                });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns Anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        final Anime savedAnime = animeRepository.save(createAnimeToSave());
        appUserRepository.save(USER);

        final ResponseEntity<Anime> forEntity =
                testRestTemplateRoleUser.getForEntity("/animes/{id}", Anime.class, savedAnime.getId());

        assertThat(forEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(forEntity.getBody())
                .isNotNull()
                .isEqualTo(savedAnime);
    }

    @Test
    @DisplayName("findById returns Not Found status when Anime is not found")
    void findById_ReturnsNotFoundStatus_WhenAnimeIsNotFound() {
        appUserRepository.save(USER);

        final ResponseEntity<Anime> forEntity =
                testRestTemplateRoleUser.getForEntity("/animes/{id}", Anime.class, 99L);

        assertThat(forEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("findAllByName returns empty list when anime is not found")
    void findAllByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        appUserRepository.save(USER);

        String url = "/animes/find?name=%s".formatted("any name");
        final ResponseEntity<List<Anime>> responseEntity =
                testRestTemplateRoleUser.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {
                        });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findAllByName returns list of Anime when successful")
    void findAllByName_ReturnsListOfAnime_WhenSuccessful() {
        final Anime savedAnime = animeRepository.save(createAnimeToSave());
        appUserRepository.save(USER);

        String url = "/animes/find?name=%s".formatted(savedAnime.getName());
        final ResponseEntity<List<Anime>> responseEntity =
                testRestTemplateRoleUser.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {
                        });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(savedAnime);
    }

    @Test
    @DisplayName("save returns saved Anime when successful")
    void save_ReturnsSavedAnime_WhenSuccessful() {
        final AnimePOSTBody animePOSTBodyToSave = createAnimePOSTBodyToSave();
        appUserRepository.save(ADMIN);

        final ResponseEntity<Anime> postForEntity =
                testRestTemplateRoleAdmin.postForEntity("/animes/admin", animePOSTBodyToSave, Anime.class);

        assertThat(postForEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        final Anime savedAnime = postForEntity.getBody();

        assertThat(savedAnime)
                .isNotNull()
                .extracting(Anime::getName)
                .isEqualTo(animePOSTBodyToSave.getName());

        assertThat(savedAnime.getId()).isNotNull();
    }

    @Test
    @DisplayName("saveMany returns list of saved animes when successful")
    void saveMany_ReturnsListOfSavedAnimes_WhenSuccessful() {
        final List<AnimePOSTBody> animeListToSave = createAnimePOSTBodyListToSave();
        appUserRepository.save(ADMIN);

        final ResponseEntity<List<Anime>> responseEntity =
                testRestTemplateRoleAdmin.exchange("/animes/admin/save-many",
                HttpMethod.POST,
                new HttpEntity<>(animeListToSave),
                new ParameterizedTypeReference<List<Anime>>() {
                });

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isNotEmpty()
                .extracting(Anime::getName)
                .containsExactlyInAnyOrderElementsOf(
                        animeListToSave.stream().map(AnimePOSTBody::getName).toList()
                );
    }

    @Test
    @DisplayName("removeMany returns void when successful")
    void removeMany_ReturnsVoid_WhenSuccessful() {
        final List<Anime> savedList = animeRepository.saveAll(createAnimeListToSave());
        final List<EntityID> entityIDS =
                savedList.stream().map(anime -> new EntityID(anime.getId())).toList();
        appUserRepository.save(ADMIN);

        final ResponseEntity<Void> responseEntity =
                testRestTemplateRoleAdmin.exchange(
                        "/animes/admin/delete-many",
                        HttpMethod.DELETE,
                        new HttpEntity<>(entityIDS),
                        Void.class);

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("remove returns Not Found status when Anime is not found")
    void remove_ReturnsNotFoundStatus_WhenAnimeIsNotFound() {
        final AnimePUTBody animePUTBodyToUpdate = createAnimePUTBodyToUpdate();
        appUserRepository.save(ADMIN);

        final ResponseEntity<Void> responseEntity =
                testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                        HttpMethod.DELETE,
                        new HttpEntity<>(animePUTBodyToUpdate),
                        Void.class,
                        animePUTBodyToUpdate.getId());

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("remove returns void when successful")
    void remove_ReturnsVoid_WhenSuccessful() {
        final Anime savedAnime = animeRepository.save(createAnimeToSave());
        appUserRepository.save(ADMIN);

        final ResponseEntity<Void> responseEntity =
                testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                        HttpMethod.DELETE,
                        new HttpEntity<>(savedAnime),
                        Void.class,
                        savedAnime.getId());

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns void when successful")
    void replace_ReturnsVoid_WhenSuccessful() {
        final Anime animeToSave = createAnimeToSave();
        final Anime savedAnime = animeRepository.save(animeToSave);
        savedAnime.setName("New Name");
        appUserRepository.save(ADMIN);

        final ResponseEntity<Void> exchange =
                testRestTemplateRoleAdmin.exchange("/animes/admin",
                        HttpMethod.PUT,
                        new HttpEntity<>(savedAnime),
                        Void.class);

        assertThat(exchange)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns Not Found status when Anime is not found")
    void replace_ReturnsNotFoundStatus_WhenAnimeIsNotFound() {
        final AnimePUTBody animePUTBodyToUpdate = createAnimePUTBodyToUpdate();
        appUserRepository.save(ADMIN);

        final ResponseEntity<Void> exchange =
                testRestTemplateRoleAdmin.exchange("/animes/admin",
                        HttpMethod.PUT,
                        new HttpEntity<>(animePUTBodyToUpdate),
                        Void.class);

        assertThat(exchange)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
