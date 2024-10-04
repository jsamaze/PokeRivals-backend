package com.smu.csd.pokerivals.persistence.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Table(name="users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor
public abstract class User {

    @Id
    @Column(length = 100)
    protected String username;

    protected String description;

    @Column(length = 30, unique=true)
    @JsonIgnore
    private String googleSub;

    @Temporal(TemporalType.TIMESTAMP)
    private Date googleLinkTime;

    public void updateGoogleSub(Date emailCreatedTime, String googleSub){
        if (!emailCreatedTime.equals(googleLinkTime)){
            this.googleSub = googleSub;
            this.googleLinkTime = emailCreatedTime;
        }
    }

    @Setter
    private String email;

    @Version
    private int version; //optimistic locking

    protected User(String username,String googleSub){
        this.username = username;
        this.googleSub = googleSub;
    }

}