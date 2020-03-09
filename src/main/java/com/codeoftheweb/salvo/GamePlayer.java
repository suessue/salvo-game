package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set <Ship> ships = new HashSet <> ();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set <Salvo> salvoes = new HashSet <> ();

    private LocalDateTime joinDate;

    public GamePlayer() {
    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now ();
    }


    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put ( "id", this.id );
        dto.put ( "player", this.player.toDTO () );
        Score score = getScore();
        dto.put ( "scores", score != null ? score.toDTO () : null );
        return dto;
    }


    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public List <Salvo> getSalvoes() {
        return salvoes.stream ().collect ( toList () );
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer ( this );
        ships.add ( ship );
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer ( this );
        salvoes.add ( salvo );
    }

    public Score getScore() {
        return this.player.getScore ( this.game );
    }

    public Map <String, Object> toGameViewDTO() {
        Map <String, Object> dto = new LinkedHashMap <> ();
        dto.put ( "id", this.id );
        dto.put ( "created", this.game.getCreationDate () );
        dto.put ( "gamePlayers", this.game.getGamePlayers ().stream ().map ( GamePlayer::toDTO ).collect ( toList () ) );
        dto.put ( "ships", this.ships.stream ().map ( Ship::toDTO ).collect ( toList () ) );
        dto.put ( "salvoes", this.game.getGamePlayers ().stream ().flatMap ( gamePlayer -> gamePlayer.getSalvoes ().stream ().map ( Salvo::toDTO ) ).collect ( toList () ) );
        return dto;
    }


}
