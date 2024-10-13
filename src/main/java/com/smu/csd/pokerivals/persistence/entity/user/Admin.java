package com.smu.csd.pokerivals.persistence.entity.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="admins")
public class Admin extends User {
    public Admin(){}

    public Admin(String username, String googleId) {
        super(username, googleId);
    }

    @Setter
    @Getter
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date activeSince;

    @JsonIgnore
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    private Admin invitedBy;

    @JsonIgnore
    @Getter
    @OneToMany(mappedBy = "invitedBy")
    private Set<Admin> invitees = new HashSet<>();

    public void addInvitee(Admin a){
        this.invitees.add(a);
        a.invitedBy = this;
    }

    @JsonGetter("active")
    public boolean isactive(){
        return activeSince != null;
    }
}