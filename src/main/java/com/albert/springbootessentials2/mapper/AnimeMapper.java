package com.albert.springbootessentials2.mapper;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import com.albert.springbootessentials2.request.AnimePUTBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // used for injection
public interface AnimeMapper {
//    not necessary if using injection (@Autowired)
//    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    //    @Mapping(source = "name", target = "name")
    Anime toAnime(AnimePOSTBody animePOSTBody);
    Anime toAnime(AnimePUTBody animePUTBody);
}
