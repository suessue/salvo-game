package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "locations")
    private List <String> locations = new ArrayList <> ();

    private Integer turn;

    public Salvo() {
    }

    public Salvo(Integer turn, List <String> locations) {
        this.turn = turn;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Integer getTurn() {
        return turn;
    }

    public List <String> getSalvoLocations() {
        return locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setSalvoLocations(List <String> salvoLocations) {
        this.locations = locations;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put ( "turn", this.turn );
        dto.put ( "player", this.gamePlayer.getId () );
        dto.put ( "locations", this.locations );

        return dto;
    }

}
