
package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public List <String> getLocations() {
        return locations;
    }

    public List <String> getHits() {
        List <String> opponentShipLocations = this.gamePlayer.getOpponentShipLocations ();
        return this.locations.stream ().filter ( opponentShipLocations::contains ).collect ( toList () );
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <> ();
        dto.put ( "turn", this.turn );
        dto.put ( "player", this.gamePlayer.getId () );
        dto.put ( "locations", this.locations );

        return dto;
    }

    public Map <String, Object> toHitsDTO() {
        Map <String, Object> dto = new LinkedHashMap <> ();
        dto.put ( "turn", this.turn );
        dto.put ( "allHits", this.getHits () );
        dto.put ( "shipStatus", this.toShipHitsDTO () );

        return dto;
    }

    public List <Map <String, Object>> toShipHitsDTO() {
        List <Map <String, Object>> dtoList = new LinkedList <> ();


        this.getShipHit ().forEach ( shipHit -> {
            Map <String, Object> dto = new LinkedHashMap <> ();

            List <Salvo> allSalvoesUpToThisTurn = this.gamePlayer.getSalvoes ().stream ()
                    .filter ( s -> s.getTurn () <= this.turn )
                    .collect ( toList () );

            List <String> allSalvoesLocationsUpToThisTurn = allSalvoesUpToThisTurn.stream ()
                    .map ( Salvo::getLocations )
                    .flatMap ( List::stream )
                    .collect ( Collectors.toList () );


            int numberOfHits = Math.toIntExact ( shipHit.getLocations ().stream ().filter ( loc -> this.getLocations ().contains ( loc ) ).count () );
            dto.put ( "type", shipHit.getType () );
            dto.put ( "shipId", shipHit.getId () );
            dto.put ( "numberOfHits", numberOfHits );
            int hitsAccumulated = Math.toIntExact ( shipHit.getLocations ().stream ().filter ( allSalvoesLocationsUpToThisTurn::contains ).count () );
            dto.put ( "hitsLeft", shipHit.getLocations ().size () - hitsAccumulated );

            dtoList.add ( dto );
        } );
        return dtoList;

    }


    public List <Ship> getShipHit() {

        List <Ship> opponentShips = this.gamePlayer.getShipsOpponent ();
        return opponentShips.stream ().filter ( s -> this.getLocations ().stream ().anyMatch ( x -> s.getLocations ().contains ( x ) ) )
                .sorted ( Comparator.comparingLong ( Ship::getId ) )
                .collect ( toList () );

    }

}







