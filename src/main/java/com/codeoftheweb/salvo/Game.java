package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.stream.Collectors.toList;


@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set <GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set <Score> scores;

    private LocalDateTime creationDate;

    public Game() {
    }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <> ();
        dto.put ( "id", this.id );
        dto.put("state", this.getState());
        dto.put ( "created", this.creationDate );
        dto.put ( "finishedDate", this.creationDate.plusMinutes ( 30 ) );
        dto.put ( "gamePlayers", this.gamePlayers.stream ().sorted ( Comparator.comparingLong(GamePlayer::getId)).map ( GamePlayer::toDTO ).collect ( toList () ) );

        return dto;
    }

    public String getState() {

        if (this.getGamePlayers ().stream ().anyMatch (gp -> gp.getState ().equals ( "WAITING FOR YOUR OPPONENT" ))){
            return "WAITING FOR OPPONENT";
        } else if (this.getGamePlayers ().stream ().anyMatch (gp -> gp.getState ().contains ( "OVER" ))){
            return "GAME OVER";

        } else {return "ONGOING";}

    }

    public void addScore(Score score) {
        score.setGame ( this );
        scores.add ( score );
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @JsonIgnore
    public List <Player> getPlayers() {
        return gamePlayers.stream ().map ( GamePlayer::getPlayer ).collect ( toList () );
    }

    public Set <GamePlayer> getGamePlayers() {
        return gamePlayers;
    }


}







