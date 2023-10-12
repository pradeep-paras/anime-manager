package com.contact.myapp.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contact.myapp.entities.Anime;

import jakarta.transaction.Transactional;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Anime, Long> {

    // @Transactional()
    // Page<Anime> findByUserIdPage(@Param("userId") int userId, Pageable pageable);

}
