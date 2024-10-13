package com.smu.csd.pokerivals.persistence.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Player extends User {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double points = 800.0;

    public Player(String username, String googleId) {
        super(username, googleId);
    }

    @Getter
    @ManyToMany
    @JsonIgnore
    private Set<Player> befriendedBy = new HashSet<>();

    @Getter
    @ManyToMany(mappedBy = "befriendedBy")
    @JsonIgnore
    private Set<Player> friendsWith = new HashSet<>();

    public void addFriend(Player p){
        this.befriendedBy.add(p);
        p.friendsWith.add(this);

        p.befriendedBy.add(this);
        this.friendsWith.add(p);
    }

    public void removeFriend(Player p){
        this.befriendedBy.remove(p);
        p.friendsWith.remove(this);

        this.friendsWith.remove(p);
        p.befriendedBy.remove(this);
    }

    @ManyToOne
    @JoinColumn(name = "fk_clan")
    @Getter
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Clan clan;

    public void addToClan(Clan c){
        if (this.clan != null && !clan.getName().equals(c.getName())) {
            clan.getMembers().remove(this);
        }
        this.clan = c;
        c.getMembers().add(this);
    }

}