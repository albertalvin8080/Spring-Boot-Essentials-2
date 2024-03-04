package com.albert.springbootessentials2.controller;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.exception.NotFoundException;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import com.albert.springbootessentials2.request.AnimePUTBody;
import com.albert.springbootessentials2.service.AnimeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.albert.springbootessentials2.util.AnimeCreator.*;
import static com.albert.springbootessentials2.util.AnimePOSTBodyCreator.*;
import static com.albert.springbootessentials2.util.AnimePUTBodyCreator.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
    @InjectMocks
    private AnimeController animeController;
    @Mock
    private AnimeService animeService;

    @BeforeEach
    void setup() {
        final Anime validAnime = createValidAnime();
        final List<Anime> validAnimeList = createValidAnimeList();
        final PageImpl<Anime> animePage = new PageImpl<>(validAnimeList);

        BDDMockito.when(animeService.listAll(ArgumentMatchers.any(Pageable.class))).thenReturn(animePage);
        BDDMockito.when(animeService.listAllNonPageable()).thenReturn(validAnimeList);
        BDDMockito.when(animeService.findById(ArgumentMatchers.anyLong())).thenReturn(validAnime);
        BDDMockito.when(animeService.findAllByName(ArgumentMatchers.anyString())).thenReturn(validAnimeList);
        BDDMockito.when(animeService.save(ArgumentMatchers.any(AnimePOSTBody.class))).thenReturn(validAnime);
        BDDMockito.doNothing().when(animeService).remove(ArgumentMatchers.anyLong());
        BDDMockito.doNothing().when(animeService).replace(ArgumentMatchers.any(AnimePUTBody.class));
    }

    @Test
    @DisplayName("replace throws NotFoundException when Anime is not found")
    void replace_ThrowsNotFoundException_WhenAnimeIsNotFound() {
        BDDMockito.doThrow(NotFoundException.class).when(animeService).replace(ArgumentMatchers.any(AnimePUTBody.class));
        final AnimePUTBody animePUTBodyToUpdate = new AnimePUTBody();

        Assertions.assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> animeController.replace(animePUTBodyToUpdate));

        BDDMockito.then(animeService).should().replace(ArgumentMatchers.any(AnimePUTBody.class));
    }

    @Test
    @DisplayName("replace returns void when successful")
    void replace_ReturnsVoid_WhenSuccessful() {
        final AnimePUTBody animePUTBodyToUpdate = createAnimePUTBodyToUpdate();
        final ResponseEntity<Void> responseEntity = animeController.replace(animePUTBodyToUpdate);

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);

        BDDMockito.then(animeService).should().replace(ArgumentMatchers.any(AnimePUTBody.class));
    }

    @Test
    @DisplayName("remove returns void when successful")
    void remove_ReturnsVoid_WhenSuccessful() {
        final Anime validAnime = createValidAnime();
        final ResponseEntity<Void> responseEntity = animeController.remove(validAnime.getId());

//        Assertions.assertThatCode(() -> animeController.remove(validAnime.getId()))
//                .doesNotThrowAnyException();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);

        BDDMockito.then(animeService).should().remove(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("remove throws NotFoundException when Anime is not found")
    void remove_ThrowsNotFoundException_WhenAnimeIsNotFound() {
        BDDMockito.doThrow(NotFoundException.class).when(animeService).remove(ArgumentMatchers.anyLong());

        Assertions.assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> animeController.remove(77L));

        BDDMockito.then(animeService).should().remove(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("save returns saved Anime when successful")
    void save_ReturnsSavedAnime_WhenSuccessful() {
        final AnimePOSTBody animePOSTBodyToSave = createAnimePOSTBodyToSave();
        final ResponseEntity<Anime> responseEntity = animeController.save(animePOSTBodyToSave);
        final Anime savedAnime = responseEntity.getBody();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        Assertions.assertThat(savedAnime)
                .isNotNull()
                .extracting(Anime::getName)
                .isEqualTo(animePOSTBodyToSave.getName());

        BDDMockito.then(animeService).should().save(ArgumentMatchers.any(AnimePOSTBody.class));
    }

    @Test
    @DisplayName("findAllByName returns list of Anime when successful")
    void findAllByName_ReturnsListOfAnime_WhenSuccessful() {
        final ResponseEntity<List<Anime>> responseEntity = animeController.findAllByName("any String");
        final List<Anime> animeList = responseEntity.getBody();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(createValidAnimeList());

        BDDMockito.then(animeService).should().findAllByName(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("findById Throws NotFoundException when Anime is not found")
    void findById_ThrowsNotFoundException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeService.findById(ArgumentMatchers.anyLong())).thenThrow(NotFoundException.class);

        Assertions.assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> animeController.findById(77L).getBody());

        BDDMockito.then(animeService).should().findById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("findById returns Anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        final Anime expectedAnime = createValidAnime();

        final ResponseEntity<Anime> responseEntity = animeController.findById(expectedAnime.getId());
        final Anime returnedAnime = responseEntity.getBody();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(returnedAnime)
                .isNotNull()
                .isEqualTo(expectedAnime);

        BDDMockito.then(animeService).should().findById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("listAllNonPageable returns empty list of Anime when no data is found")
    void listAllNonPageable_ReturnsEmptyList_WhenNoDataIsFound() {
        BDDMockito.when(animeService.listAllNonPageable()).thenReturn(Collections.emptyList());

        final ResponseEntity<List<Anime>> responseEntity = animeController.listAllNonPageable();
        final List<Anime> animeList = responseEntity.getBody();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(animeList)
                .isNotNull()
                .isEmpty();

        BDDMockito.then(animeService).should().listAllNonPageable();
        // just to know it's possible
//        BDDMockito.then(animeService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("listAllNonPageable return list of Anime when successful")
    void listAllNonPageable_ReturnsListOfAnime_WhenSuccessful() {
        final ResponseEntity<List<Anime>> responseEntity = animeController.listAllNonPageable();
        final List<Anime> animeList = responseEntity.getBody();
//        final Anime expectedAnimeFromService = createValidAnimeList().get(0);

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(animeList).isNotNull().isNotEmpty()
//                .contains(expectedAnimeFromService) // redundancy
                .containsExactlyInAnyOrderElementsOf(createValidAnimeList());

        BDDMockito.then(animeService).should().listAllNonPageable();
    }

    @Test
    @DisplayName("listAll returns empty page when no data is found")
    void listAll_ReturnsEmptyPage_WhenNoDataIsFound() {
        BDDMockito.when(animeService.listAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final ResponseEntity<Page<Anime>> responseEntity = animeController.listAll(Pageable.unpaged());
        final Page<Anime> animePage = responseEntity.getBody();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(animePage)
                .isNotNull()
                .isEmpty();

        BDDMockito.then(animeService).should().listAll(ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @DisplayName("listAll return Page of Anime when successful")
    void listAll_ReturnsPageOfAnime_WhenSuccessful() {
        final int expectedListSize = createValidAnimeList().size();
        final String expectedNameOfFirstAnime = createValidAnimeList().get(0).getName();

        final ResponseEntity<Page<Anime>> responseEntity = animeController.listAll(Pageable.unpaged());
        final Page<Anime> animePage = responseEntity.getBody();

        Assertions.assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(animePage)
                .isNotNull()
                .isNotEmpty()
                // size of the internal list
                .hasSize(expectedListSize);

        Anime actualAnime = animePage.toList().get(0);
        Assertions.assertThat(actualAnime)
                .isNotNull()
                .extracting(Anime::getName)
                .isEqualTo(expectedNameOfFirstAnime);

//      This line of code, using the BDDMockito library,
//      verifies that the animeService.listAll() method was
//      called with any argument during the test execution.
        BDDMockito.then(animeService).should().listAll(ArgumentMatchers.any(Pageable.class));
//        BDDMockito.then(animeService).should().listAll(ArgumentMatchers.eq(new PageImpl<>(List.of(new Anime())).getPageable()));
    }

}