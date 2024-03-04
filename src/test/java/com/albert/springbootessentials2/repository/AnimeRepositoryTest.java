package com.albert.springbootessentials2.repository;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.util.AnimeCreator;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for AnimeRepository")
class AnimeRepositoryTest {
    // it's ok to inject directly in tests
    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("save must persist Anime when successful")
    void save_PersistAnime_WhenSuccessful() {
        final Anime newAnime = AnimeCreator.createAnimeToSave();
        final Anime savedAnime = this.animeRepository.save(newAnime);

        Assertions.assertNotNull(savedAnime);
        Assertions.assertNotNull(savedAnime.getId());
        Assertions.assertEquals(newAnime.getName(), savedAnime.getName());
    }

    @Test
    @DisplayName("save must update Anime when successful")
    void save_UpdateAnime_WhenSuccessful() {
        final Anime newAnime = AnimeCreator.createAnimeToSave();
        final Anime savedAnime = this.animeRepository.save(newAnime);

        savedAnime.setName("Jujutsu Kaisen");
        final Anime updatedAnime = this.animeRepository.save(savedAnime);

        Assertions.assertNotNull(updatedAnime);
        Assertions.assertEquals(savedAnime.getId(), updatedAnime.getId());
        Assertions.assertEquals(savedAnime.getName(), updatedAnime.getName());
    }

    @Test
    @DisplayName("save must throw ConstraintViolationException when name is empty or null")
    void save_ThrowsConstraintViolationException_WhenNameIsEmptyOrNull() {
        final Anime nameEmpty = Anime.builder().name("").build();
        final Anime nameNull = Anime.builder().name(null).build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> this.animeRepository.save(nameEmpty));
        Assertions.assertThrows(ConstraintViolationException.class, () -> this.animeRepository.save(nameNull));
    }

    @Test
    @DisplayName("findById must return Optional<Anime> with Anime present when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        final Anime newAnime = AnimeCreator.createAnimeToSave();
        // passa a possuir ID após o save() (por causa do Hibernate)
        this.animeRepository.save(newAnime);

        final Optional<Anime> animeOptional =
                this.animeRepository.findById(newAnime.getId());

        Assertions.assertNotNull(animeOptional);
        Assertions.assertTrue(animeOptional.isPresent());
        Assertions.assertEquals(newAnime.getName(), animeOptional.get().getName());
    }

    @Test
    @DisplayName("findById must return empty Optional<Anime> when Anime is not found")
    void findById_ReturnsEmptyOptional_WhenAnimeIsNotFound() {
        final Anime newAnime = AnimeCreator.createAnimeToSave();
        this.animeRepository.save(newAnime);

        final Optional<Anime> animeOptional =
                this.animeRepository.findById(99L);

        Assertions.assertNotNull(animeOptional);
        Assertions.assertFalse(animeOptional.isPresent());
    }

    @Test
    @DisplayName("findAllByName must return list of Anime when successful")
    void findAllByName_ReturnsAnimeList_WhenSuccessful() {
        final List<Anime> animeList = AnimeCreator.createValidAnimeList();
        this.animeRepository.saveAll(animeList);

        final List<Anime> allList = this.animeRepository.findAllByName("One");
        Assertions.assertNotNull(allList);
        Assertions.assertEquals(2, allList.size());
    }

    @Test
    @DisplayName("findAllByName must return empty list of Anime when Anime is not found")
    void findAllByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        final List<Anime> animeList = AnimeCreator.createValidAnimeList();
        this.animeRepository.saveAll(animeList);

        final List<Anime> allList = this.animeRepository.findAllByName("xaxa");
        Assertions.assertNotNull(allList);
        Assertions.assertEquals(0, allList.size());
    }

    @Test
    @DisplayName("deleteById does not throw anything when successful")
    void deleteById_DoesNotThrow_WhenSuccessful() {
        final var newAnime = AnimeCreator.createAnimeToSave();
        final Anime savedAnime = this.animeRepository.save(newAnime);

        Assertions.assertDoesNotThrow(() -> this.animeRepository.deleteById(savedAnime.getId()));
    }
}