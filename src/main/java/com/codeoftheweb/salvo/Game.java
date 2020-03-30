package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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
        Map <String, Object> dto = new LinkedHashMap <String, Object> ();
        dto.put ( "id", this.id );
        dto.put ( "created", this.creationDate );
        dto.put ( "finishedDate", this.creationDate.plusMinutes ( 30 ) );
        dto.put ( "gamePlayers", this.gamePlayers.stream ().map ( GamePlayer::toDTO ).collect ( toList () ) );

        return dto;
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
        return gamePlayers.stream ().map ( sub -> sub.getPlayer () ).collect ( toList () );
    }

    public Set <GamePlayer> getGamePlayers() {
        return gamePlayers;
    }


}







