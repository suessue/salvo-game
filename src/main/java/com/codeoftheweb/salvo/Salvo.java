
package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

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

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setSalvoLocations(List <String> salvoLocations) {
        this.locations = locations;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public List <String> getLocations() {
        return locations;
    }

    public List <String> getHits() {
        List <String> opponentShipLocations = this.gamePlayer.getOpponentShipLocations ();
        return this.locations.stream ().filter ( opponentShipLocations::contains ).collect ( toList () );
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put ( "turn", this.turn );
        dto.put ( "player", this.gamePlayer.getId () );
        dto.put ( "locations", this.locations );


        return dto;
    }


    public Map<String, Object> toHitsDTO() {
        Map<String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put("turn", this.turn);
        dto.put("hits", getHits());
        //why adding this makes the salvoes disappear?
//        dto.put("shipState", toShipHitsDTO ());


        return dto;
    }

    public Map<String, Object> toShipHitsDTO() {
        Map<String, Object> dto = new LinkedHashMap <> ();
        getShipHit().forEach ( shipHit -> {
        String type = shipHit.getType();
        long numberOfHits = shipHit.getLocations ().stream().filter ( loc -> this.locations.contains ( loc )).count ();
        dto.put("shipHit", type);
        dto.put("numberOfHits", numberOfHits);


        }
        return dto;
    }


    public List<Ship> getShipHit(){

        List <Ship> opponentShips = this.gamePlayer.getShipsOpponent ();

        return opponentShips.stream ().filter ( s -> this.locations.retainAll ( s.getLocations () ))
                .collect ( toList () );

    }

    public List<Ship> getShipSink(){

        List <Ship> opponentShips = this.gamePlayer.getShipsOpponent ();
        List <String> salvoUntilThisTurn = this.gamePlayer.getSalvoes ()
                .stream ().filter( sa -> sa.getTurn () <= this.turn )
                .flatMap (s -> s.getLocations ().stream()).collect(toList())
                ;

        return opponentShips.stream ().filter ( s ->
                salvoUntilThisTurn.containsAll (s.getLocations () ))
                .collect ( toList () );

    }

}









