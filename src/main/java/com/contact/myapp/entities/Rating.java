package com.contact.myapp.entities;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name="RATING")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rId;

    @NotBlank(message = "rating field is required")
    private String rate;

    @NotBlank(message = "review is required")
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

    public int getrId() {
        return rId;
    }

    public void setrId(int rId) {
        this.rId = rId;
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

    public Rating(int rId, @NotBlank(message = "rating field is required") String rate,
            @NotBlank(message = "review is required") String review) {
        this.rId = rId;
        this.rate = rate;
        this.review = review;
    }
}
