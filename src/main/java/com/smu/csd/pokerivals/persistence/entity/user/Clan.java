package com.smu.csd.pokerivals.persistence.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Clan {

    @Id
    @Column(length = 100)
    private String name;

    @OneToMany(mappedBy = "clan")
    @JsonIgnore
    private Set<Player> members = new HashSet<>();

    public Clan(String name){
        this.name = name;
    }

}
