package com.albert.springbootessentials2.service;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.exception.NotFoundException;
import com.albert.springbootessentials2.mapper.AnimeMapper;
import com.albert.springbootessentials2.util.AnimeMapperCustomImpl;
import com.albert.springbootessentials2.repository.AnimeRepository;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.verification.Times;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.albert.springbootessentials2.util.AnimeCreator.*;
import static com.albert.springbootessentials2.util.AnimePOSTBodyCreator.*;
import static com.albert.springbootessentials2.util.AnimePUTBodyCreator.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService animeService;
    @Mock
    private AnimeRepository animeRepository;
    @Spy // Creates a partial mock
    private AnimeMapper animeMapper = new AnimeMapperCustomImpl(); // Dependency inside AnimeService

    @BeforeEach
    void setup() {
        final Anime validAnime = createValidAnime();
        final List<Anime> validAnimeList = createValidAnimeList();
        final PageImpl<Anime> animePage = new PageImpl<>(validAnimeList);

        BDDMockito.when(animeRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(animePage);
        BDDMockito.when(animeRepository.findAll())
                .thenReturn(validAnimeList);
        // findById() is used inside remove() and replace() as well
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(validAnime));
        BDDMockito.when(animeRepository.findAllByName(ArgumentMatchers.anyString()))
                .thenReturn(validAnimeList);

        // save() is used inside replace() as well
        BDDMockito.when(animeRepository.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(validAnime);
        BDDMockito.doNothing().when(animeRepository).deleteById(ArgumentMatchers.anyLong());

//        BDDMockito.when(animeMapper.toAnime(ArgumentMatchers.any(AnimePOSTBody.class)))
//                .thenReturn(validAnime);
//        BDDMockito.when(animeMapper.toAnime(ArgumentMatchers.any(AnimePUTBody.class)))
//                .thenReturn(validAnime);
    }

    @Test
    @DisplayName("replace Throws NotFoundException when anime is not found")
    void replace_ThrowsNotFoundException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> animeService.replace(createAnimePUTBodyToUpdate()));

        BDDMockito.then(animeRepository).should().findById(ArgumentMatchers.anyLong());
        BDDMockito.then(animeRepository)
                .should(new Times(0)).save(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("replace returns void when successful")
    void replace_ReturnsVoid_WhenSuccessful() {
        assertDoesNotThrow(() -> animeService.replace(createAnimePUTBodyToUpdate()));

        BDDMockito.then(animeRepository).should().save(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("remove Throws NotFoundException when anime is not found")
    void remove_ThrowsNotFoundException_WhenAnimeIsNotFound() {
//        BDDMockito.doThrow(NotFoundException.class).when(animeRepository).findById(ArgumentMatchers.anyLong());
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> animeService.remove(77L));

        BDDMockito.then(animeRepository).should().findById(ArgumentMatchers.anyLong());
        BDDMockito.then(animeRepository)
                .should(new Times(0)).deleteById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("remove returns void when successful")
    void remove_ReturnsVoid_WhenSuccessful() {
        assertDoesNotThrow(() -> animeService.remove(77L));

        BDDMockito.then(animeRepository).should().deleteById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("save returns saved anime when successful")
    void save_ReturnsSavedAnime_WhenSuccessful() {
        final AnimePOSTBody animePOSTBodyToSave = createAnimePOSTBodyToSave();
        final Anime savedAnime = animeService.save(animePOSTBodyToSave);

        assertNotNull(savedAnime);
        assertEquals(animePOSTBodyToSave.getName(), savedAnime.getName());

        BDDMockito.then(animeRepository).should().save(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("findAllByName returns empty list of anime when no data is found")
    void findAllByName_ReturnsEmptyPageOfAnime_WhenNoDataIsFound() {
        BDDMockito.when(animeRepository.findAllByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        final List<Anime> animeList = animeService.findAllByName("any name");

        assertNotNull(animeList);
        assertTrue(animeList.isEmpty());

        BDDMockito.then(animeRepository).should().findAllByName(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("findAllByName returns list of anime when successful")
    void findAllByName_ReturnsListOfAnime_WhenSuccessful() {
        final List<Anime> validAnimeList = createValidAnimeList();
        final List<Anime> animeList = animeService.findAllByName("any name");

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(validAnimeList, animeList);

        BDDMockito.then(animeRepository).should().findAllByName(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("findById throws NotFoundException when anime is not found")
    void findById_ThrowsNotFoundException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> animeService.findById(0L));

        BDDMockito.then(animeRepository).should().findById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        final Anime validAnime = createValidAnime();
        final Anime foundAnime = animeService.findById(0L);

        assertNotNull(foundAnime);
        assertEquals(validAnime, foundAnime);

        BDDMockito.then(animeRepository).should().findById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("listAllNonPageable returns empty list of anime when no data is found")
    void listAllNonPageable_ReturnsEmptyPageOfAnime_WhenNoDataIsFound() {
        BDDMockito.when(animeRepository.findAll())
                .thenReturn(Collections.emptyList());

        final List<Anime> animeList = animeService.listAllNonPageable();

        assertNotNull(animeList);
        assertTrue(animeList.isEmpty());

        BDDMockito.then(animeRepository).should().findAll();
    }

    @Test
    @DisplayName("listAllNonPageable returns list of anime when successful")
    void listAllNonPageable_ReturnsListOfAnime_WhenSuccessful() {
        final List<Anime> validAnimeList = createValidAnimeList();
        final List<Anime> animeList = animeService.listAllNonPageable();

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(validAnimeList, animeList);

        BDDMockito.then(animeRepository).should().findAll();
    }

    @Test
    @DisplayName("listAll returns empty page of anime when no data is found")
    void listAll_ReturnsEmptyPageOfAnime_WhenNoDataIsFound() {
        BDDMockito.when(animeRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final Page<Anime> animePage = animeService.listAll(Pageable.unpaged());

        assertNotNull(animePage);
        assertTrue(animePage.isEmpty());
//        assertTrue(animePage.toList().isEmpty());

        BDDMockito.then(animeRepository).should().findAll(ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @DisplayName("listAll returns a page of anime when successful")
    void listAll_ReturnsPageOfAnime_WhenSuccessful() {
        final List<Anime> validAnimeList = createValidAnimeList();
                                                     // or PageRequest.of(0, 1) (it's not the same)
        final Page<Anime> animePage = animeService.listAll(Pageable.unpaged());

        assertNotNull(animePage);
        assertFalse(animePage.isEmpty());
        assertEquals(validAnimeList, animePage.toList());

        BDDMockito.then(animeRepository).should().findAll(ArgumentMatchers.any(Pageable.class));
    }
}