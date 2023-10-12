package com.contact.myapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.myapp.entities.Likes;

public interface LikesRepository extends JpaRepository<Likes, Integer> {
    @Query("select a from Likes a where a.username= :username and a.rating.rId= :rId")
    public Likes getLikesByUsername(@Param("rId") int rId, @Param("username") String username);
}
