package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

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

    private LocalDateTime finishDate;

    private double score;

    public Score(){

    }

    public Score(double score, Game game, Player player, LocalDateTime finishDate) {
        this.score = score;
        this.game = game;
        this.player = player;
        this.finishDate = finishDate;
    }

    public double getScore() {
        return score;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public void setPlayer(Player player) { this.player = player;
    }

    public Map <String, Object> toDTO() {
        Map <String, Object> dto = new LinkedHashMap <> ();
        dto.put ( "score", this.score );
        dto.put("username", this.getPlayer ().getUserName ());
        return dto;
    }
}
