package com.smu.csd.pokerivals.persistence.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.Date;


@Entity
@Table(name="users")
@Inheritance(strategy = InheritanceType.JOINED) // experiment with different kinds
@Getter
public abstract class User implements Persistable<String> {
    public User(){}

    @Id
    @Column(length = 100)
    @NotEmpty
    @Size(max =100)
    protected String username;

    @Column(columnDefinition = "TEXT")
    protected String description;

    @Column(length = 30, unique=true)
    @JsonIgnore
    private String googleSub;


    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date googleLinkTime;

    public void updateGoogleSub(Date emailCreatedTime, String googleSub){
        if (!emailCreatedTime.equals(googleLinkTime)){
            this.googleSub = googleSub;
            this.googleLinkTime = emailCreatedTime;
        }
    }

    @Setter
    @Email
    private String email;

    @Version
    @JsonIgnore
    private int version; //optimistic locking

    protected User(String username,String googleSub){
        this.username = username;
        this.googleSub = googleSub;
    }

    // persistable

    @Setter
    @Getter
    @Transient
    private boolean update;

    @Override
    public boolean isNew() {
        return !this.update;
    }

    @PrePersist
    @PostLoad
    void markUpdated() {
        this.update = true;
    }

    @Override
    public String getId(){
        return username;
    }

}