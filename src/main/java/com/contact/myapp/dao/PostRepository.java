package com.contact.myapp.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.contact.myapp.entities.Anime;


@Repository
public interface PostRepository extends PagingAndSortingRepository<Anime, Long> {

    // @Transactional()
    // Page<Anime> findByUserIdPage(@Param("userId") int userId, Pageable pageable);

}
