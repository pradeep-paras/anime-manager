package com.contact.myapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.myapp.entities.Anime;


public interface AnimeRepository extends JpaRepository<Anime, Integer> {
    
    // Finds single anime by name @Query("Select c from Registration c where c.place like %:place%")
    @Query("select a from Anime a where a.anime_name like %:anime%")
    public List<Anime> getAnimeByAnimeName(@Param("anime") String anime);

    // fetch animes
    // public List<Anime> findByAnime_NameContaining(String anime_name);

    // update anime details ", a.imageUrl = :imageUrl, a.status = :status, a.description = :description where a.aId :aId"
    // @Modifying
    // @Query("update Anime a set a.anime_name = :anime_name where a.aId :aId")
    // public int updateAnime(@Param("aId") int aId, @Param("anime_name") String anime_name);
    // , @Param("imageUrl") String imageUrl, @Param("status") String status, @Param("description") String description 
}
