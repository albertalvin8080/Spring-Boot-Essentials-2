package com.albert.springbootessentials2.util;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.request.AnimePUTBody;

import static com.albert.springbootessentials2.util.AnimeCreator.createValidAnime;

public class AnimePUTBodyCreator {
    public static AnimePUTBody createAnimePUTBodyToUpdate() {
        final Anime validAnime = createValidAnime();
        return AnimePUTBody.builder()
                .id(validAnime.getId())
                .name(validAnime.getName())
                .build();
    }
}
