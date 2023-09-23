package com.contact.myapp.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.myapp.entities.Rating;

public interface RatingRepository extends JpaRepository<Rating, Integer>{
        @Query("SELECT tr " +
           "FROM Rating tr "+
           "LEFT JOIN Anime ta ON tr.anime.aId = ta.aId "+
           "LEFT JOIN User tu ON tr.user.id = tu.id " +
           "WHERE ta.aId = :animeId")
        public Page<Rating> findRatingsByAnimeName(@Param("animeId") int animeId, Pageable pageable);
    // public List<Rating> findRatingsByAnimeName(@Param("animeId") int animeId);

    @Query("SELECT r FROM Rating r WHERE r.user.id = :userId")
    public Page<Rating> findRatingsByUser(@Param("userId") int userId, Pageable pageable);
}