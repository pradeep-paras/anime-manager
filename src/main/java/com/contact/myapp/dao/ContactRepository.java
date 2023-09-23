package com.contact.myapp.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.myapp.entities.Contact;
import com.contact.myapp.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
    
    // fetch contacts using name 
    public List<Contact> findByNameContainingAndUser(String name, User user);
}
