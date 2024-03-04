package com.albert.springbootessentials2.util;

import com.albert.springbootessentials2.request.AnimePOSTBody;

import java.util.List;

import static com.albert.springbootessentials2.util.AnimeCreator.createValidAnime;

public interface AnimePOSTBodyCreator {

    static AnimePOSTBody createAnimePOSTBodyToSave() {
        return AnimePOSTBody.builder().name(createValidAnime().getName()).build();
    }

    /**
     * Returns a list of AnimePOSTBody with name only. The returned list is static generated.
     */
    static List<AnimePOSTBody> createAnimePOSTBodyListToSave() {
        return List.of(
                AnimePOSTBody.builder().name("Serialization Experimentation").build(),
                AnimePOSTBody.builder().name("Jujutsu Kaisen").build(),
                AnimePOSTBody.builder().name("Witch from mercury").build(),
                AnimePOSTBody.builder().name("One Punch Man").build(),
                AnimePOSTBody.builder().name("One Piece").build());
    }
}
