package com.contact.myapp.entities;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@Table(name="RATING")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rId;

    private int like_count;

    @NotBlank(message = "rating field is required")
    private String rate;

    @NotBlank(message = "review is required")
    @Size(max = 2000, message = "maximum 2000 characters are allowed")
    @Column(length = 2000)
    private String review;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="id")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="a_id")
    private Anime anime;

    // for likes
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "rating")
    private List<Likes> likes = new ArrayList<>();

    public int getrId() {
        return rId;
    }

    public void setrId(int rId) {
        this.rId = rId;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Rating() {
        super();
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Anime getAnime() {
        return anime;
    }
    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }

    public Rating(int rId,
            int like_count, 
            @NotBlank(message = "rating field is required") String rate,
            @NotBlank(message = "review is required") String review) {
        this.rId = rId;
        this.like_count = like_count;
        this.rate = rate;
        this.review = review;
    }
}
