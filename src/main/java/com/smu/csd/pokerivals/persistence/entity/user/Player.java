package com.smu.csd.pokerivals.persistence.entity.user;

import com.smu.csd.pokerivals.persistence.entity.Clan;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Player extends User {

    @Setter
    private double points = 800.0;

    public Player(String username, String googleSub) {
        super(username, googleSub);
    }

    @ManyToMany
    private Set<Player> befriendedBy = new HashSet<>();

    @ManyToMany(mappedBy = "befriendedBy")
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

        p.befriendedBy.remove(this);
        this.friendsWith.remove(p);
    }


    @ManyToOne
    @JoinColumn(name = "fk_clan")
    private Clan clan;

    public void addToClan(Clan c){
        if (this.clan != null && !clan.getName().equals(c.getName())) {
            clan.getMembers().remove(this);
        }
        this.clan = c;
        c.getMembers().add(this);
    }

}