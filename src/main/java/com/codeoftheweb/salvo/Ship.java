package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ElementCollection
    @Column(name = "shipLocations")
    private List <String> locations = new ArrayList <> ();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private String type;

    public Ship() {
    }

    public Ship(String type, List <String> locations) {
        this.type = type;
        this.locations = locations;

    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setLocations(List <String> locations) {
        this.locations = locations;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List <String> getLocations() {
        return locations;
    }


    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <> ();
        dto.put ( "type", this.type );
        dto.put ( "locations", this.locations );
        return dto;
    }


}
