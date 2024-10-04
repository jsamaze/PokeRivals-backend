package com.smu.csd.pokerivals.persistence.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name="admins")
@NoArgsConstructor
public class Admin extends User {

    public Admin(String username, String googleSub) {
        super(username, googleSub);
    }

    @Setter
    private Date activeSince;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Admin invitedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "invitedBy")
    private Set<Admin> invitees = new HashSet<>();

    public void addInvitee(Admin a){
        this.invitees.add(a);
        a.invitedBy = this;
    }

}