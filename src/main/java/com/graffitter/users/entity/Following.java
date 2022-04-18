package com.graffitter.users.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Following implements Serializable {
    public Following() {
        this.follower = null;
        this.followee = null;
    }

    public Following(String follower, String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String follower;

    public String followee;
}
