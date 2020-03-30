package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set <GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set <Score> scores;


    private String userName;
    private String password;

    public Player() {
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer ( this );
        gamePlayers.add ( gamePlayer );
    }


    public Player(String email, String password) {
        this.userName = email;
        this.password = password;
    }

    public String getPlayer() {return this.userName;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public String getUserName() {
        return userName;
    }

    @JsonIgnore
    public List <Game> getGames() {
        return gamePlayers.stream ().map ( sub -> sub.getGame () ).collect ( toList () );
    }

    public void setGamePlayers(Set <GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    @JsonIgnore
    public Set <GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void addScore(Score score) {
        score.setPlayer ( this );
        scores.add ( score );
    }

    public Score getScore(Game game) {
        return scores.stream ().filter ( p -> p.getGame ().equals ( game ) ).findFirst ().orElse ( null );
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put ( "id", this.id );
        dto.put ( "username", this.userName );


        return dto;
    }

    public Map <String, Object> toLeaderBoardDTO() {
        Map <String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put ("id", this.id);
        dto.put ( "username", this.userName );
        dto.put("totalPoints", getTotalPoints ());
        dto.put("losses", countResults ( 0 ));
        dto.put("wins", countResults ( 1 ));
        dto.put("ties", countResults ( 0.5 ));

        return dto;
    }

    public double getTotalPoints() {
        return this.scores.stream ()
                .mapToDouble ( x -> x.getScore () )
                .sum ();
    }

    public double countResults(double x) {
        return this.scores.stream ()
                .filter ( b -> b.getScore () == x )
                .count ();
    }



}


