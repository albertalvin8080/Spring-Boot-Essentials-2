package com.albert.springbootessentials2.repository;

import com.albert.springbootessentials2.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    @Query("""
            select a from Anime a where a.name like %:name%
            """)
    List<Anime> findAllByName(String name);

    // this delete method expects a select query which return ONE RESULT AT A TIME
//    @Query("""
//            select a from Anime a where a.id in :longs
//            """)
//    @Override
//    void deleteAllById(@NotNull Iterable<? extends Long> longs);
}
