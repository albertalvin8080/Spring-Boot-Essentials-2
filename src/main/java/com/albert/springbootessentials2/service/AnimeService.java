package com.albert.springbootessentials2.service;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.exception.NotFoundException;
import com.albert.springbootessentials2.mapper.AnimeMapper;
import com.albert.springbootessentials2.repository.AnimeRepository;
import com.albert.springbootessentials2.request.EntityID;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import com.albert.springbootessentials2.request.AnimePUTBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimeService {
    private final AnimeRepository animeRepository;
    private final AnimeMapper animeMapper;

    @Autowired
    public AnimeService(AnimeRepository animeRepository, AnimeMapper animeMapper) {
        this.animeRepository = animeRepository;
        this.animeMapper = animeMapper;
    }

    public Page<Anime> listAll(Pageable pageable) {
        return animeRepository.findAll(pageable);
    }

    public List<Anime> listAllNonPageable() {
        return animeRepository.findAll();
    }

    public Anime findById(long id) throws NotFoundException {
        return animeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Anime not found"));
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found"));
    }

    public List<Anime> findAllByName(String name) {
        return animeRepository.findAllByName(name);
    }

//    @Transactional(rollbackFor = Exception.class)
    @Transactional
    public Anime save(AnimePOSTBody animePOSTBody) {
        Anime newAnime = animeMapper.toAnime(animePOSTBody);
        return animeRepository.save(newAnime);
    }

    @Transactional
    public List<Anime> saveMany(List<AnimePOSTBody> list) {
        final List<Anime> collect = list.stream().map(animeMapper::toAnime).collect(Collectors.toList());
        return animeRepository.saveAll(collect);
    }

    public void remove(Long id) throws NotFoundException {
        findById(id);
        animeRepository.deleteById(id);
    }

    public void remove(List<EntityID> entityIDS) {
        final List<Long> collect = entityIDS.stream().map(EntityID::getId).collect(Collectors.toList());
        animeRepository.deleteAllById(collect);
    }

    public void replace(AnimePUTBody animePUTBody) throws NotFoundException {
        findById(animePUTBody.getId()); // doesn't throw anything if the @Entity exists
        animeRepository.save(animeMapper.toAnime(animePUTBody));
    }
}
