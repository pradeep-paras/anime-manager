package com.contact.myapp.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="ANIME")
public class Anime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int aId;
    
    @NotBlank(message = "Name field is required")
    @Size(max = 50, message = "maximum 20 characters are allowed")
    private String anime_name;

    @NotBlank(message = "image url is required")
    private String imageUrl;

    @NotBlank(message = "status is required")
    private String status;

    private float anime_rating;

    private float total_count;

    @Column(length = 1500)
    private String description;

    // for ratings
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "anime")
    private List<Rating> ratings = new ArrayList<>();

    // for animes and users
    @ManyToMany
    private Set<User> users;

    public int getaId() {
        return aId;
    }

    public void setaId(int aId) {
        this.aId = aId;
    }

    public String getAnime_name() {
        return anime_name;
    }

    public void setAnime_name(String anime_name) {
        this.anime_name = anime_name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getAnime_rating() {
        return anime_rating;
    }

    public void setAnime_rating(float anime_rating) {
        this.anime_rating = anime_rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTotal_count() {
        return total_count;
    }

    public void setTotal_count(float total_count) {
        this.total_count = total_count;
    }  

    public Anime() {
        super();
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    } 

    public Anime(int aId, String anime_name, String imageUrl, String status, float anime_rating, String description, float total_count) {
        this.aId = aId;
        this.anime_name = anime_name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.anime_rating = anime_rating;
        this.description = description;
        this.total_count = total_count;
    }

}
