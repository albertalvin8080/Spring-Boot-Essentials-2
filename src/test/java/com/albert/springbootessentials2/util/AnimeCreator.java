package com.albert.springbootessentials2.util;

import com.albert.springbootessentials2.domain.Anime;

import java.util.List;

public interface AnimeCreator {
    /**
     * Returns an Anime with name only. The returned Anime is static generated.
     */
    static Anime createAnimeToSave() {
        return Anime.builder().name("Serial Experiments Lain").build();
    }

    /**
     * Returns an Anime with all valid fields. The returned Anime is static generated.
     */
    static Anime createValidAnime() {
        return Anime.builder().name("Jigoku Shoujo").id(99L).build();
    }

    /**
     * Returns a list of Animes with name only. The returned list is static generated.
     */
    static List<Anime> createAnimeListToSave() {
        return List.of(
                Anime.builder().name("Serialization Experimentation").build(),
                Anime.builder().name("Jujutsu Kaisen").build(),
                Anime.builder().name("Witch from mercury").build(),
                Anime.builder().name("One Punch Man").build(),
                Anime.builder().name("One Piece").build());
    }

    /**
     * Returns a list of valid Animes. The returned list is static generated.
     */
    static List<Anime> createValidAnimeList() {
        return List.of(
                Anime.builder().id(1L).name("Serialization Experimentation").build(),
                Anime.builder().id(2L).name("Jujutsu Kaisen").build(),
                Anime.builder().id(3L).name("Witch from mercury").build(),
                Anime.builder().id(4L).name("One Punch Man").build(),
                Anime.builder().id(5L).name("One Piece").build());
    }

}
