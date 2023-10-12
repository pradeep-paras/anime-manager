package com.contact.myapp.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contact.myapp.dao.ContactRepository;
import com.contact.myapp.dao.LikesRepository;
import com.contact.myapp.dao.RatingRepository;
import com.contact.myapp.dao.UserRepository;
import com.contact.myapp.entities.Contact;
import com.contact.myapp.entities.Likes;
import com.contact.myapp.entities.Rating;
import com.contact.myapp.entities.User;

@RestController
public class SearchController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private RatingRepository ratingRepository;

    // search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
        User user = this.userRepository.getUserByUserName(principal.getName());
        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
        
        System.out.println("Response contacts " + contacts);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/like/{rId}")
    public ResponseEntity<?> like(@PathVariable("rId") Integer rId, Principal principal){
        // for dislike comment
        Rating rating = this.ratingRepository.findById(rId).get();

        // rating.getLikes().getUsername()
        Likes total = this.likesRepository.getLikesByUsername(rId, principal.getName());

        if(total != null){

            int count = rating.getLike_count();
            count--;
            rating.setLike_count(count);

            rating.getLikes().remove(total);

            likesRepository.delete(total);

            ratingRepository.save(rating);

            return ResponseEntity.ok("not liked");
        } else {
            Likes likes = new Likes();
            likes.setUsername(principal.getName());

            int count = rating.getLike_count();

            count++;

            rating.setLike_count(count);

            likes.setRating(rating);

            this.likesRepository.save(likes);

            return ResponseEntity.ok("liked");
        }
    }
}
