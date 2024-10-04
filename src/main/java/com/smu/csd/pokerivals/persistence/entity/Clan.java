package com.smu.csd.pokerivals.persistence.entity;

import com.smu.csd.pokerivals.persistence.entity.user.Player;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Clan {

    @Id
    @Column(length = 100)
    private String name;

    @OneToMany(mappedBy = "clan")
    private Set<Player> members = new HashSet<Player>();

    public Clan(String name){
        this.name = name;
    }



}
