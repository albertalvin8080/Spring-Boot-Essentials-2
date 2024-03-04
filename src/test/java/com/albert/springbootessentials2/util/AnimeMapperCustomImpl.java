package com.albert.springbootessentials2.util;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.mapper.AnimeMapper;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import com.albert.springbootessentials2.request.AnimePUTBody;

/**
 * Class used for tests with partial mocks using @Spy
 * */
public class AnimeMapperCustomImpl implements AnimeMapper {
    public Anime toAnime(AnimePOSTBody animePOSTBody) {
        return Anime.builder().name(animePOSTBody.getName()).build();
    }

    public Anime toAnime(AnimePUTBody animePUTBody) {
        return Anime.builder().id(animePUTBody.getId()).name(animePUTBody.getName()).build();
    }
}
