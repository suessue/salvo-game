package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;


    @PostMapping("/players")
    public ResponseEntity <Map <String, Object>> register(@RequestParam String email, @RequestParam String password) {

        if (!(email.contains ( "@" )) || !email.contains ( "." ) || email.isEmpty () || password.isEmpty ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NOT_VALID ), HttpStatus.FORBIDDEN );
        }

        Player player = playerRepository.findByUserName ( email );
        if (player != null) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_USERNAME_EXISTS ), HttpStatus.CONFLICT );
        }

        Player newPlayer = playerRepository.save ( new Player ( email, passwordEncoder.encode ( password ) ) );
        return new ResponseEntity <>  (newPlayer.toDTO (), HttpStatus.CREATED );

    }


    @GetMapping("/games")
    public ResponseEntity <Map <String, Object>> getPlayerPlusGames(Authentication authentication) {

        Map <String, Object> dto = new LinkedHashMap <> ();
        if (isGuest ( authentication )) {
            dto.put ( "player", null );

        } else {
            Player player = playerRepository.findByUserName ( authentication.getName () );
            dto.put ( "player", player.toDTO () );
        }
        dto.put ( "games", gameRepository.findAll ()
                .stream ()
                .map ( Game::toDTO )
                .collect ( toList () ) );


        return new ResponseEntity <> ( dto, HttpStatus.OK );

    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity <Object> addShips(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody List <Ship> ships) {

        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );

        if (!gamePlayer.isPresent ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NO_LOG_IN ), HttpStatus.UNAUTHORIZED );
        }
        if ((isGuest ( authentication )) || !(gamePlayer.get ().getPlayer ().getUserName ().equals ( authentication.getName () ))) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NOT_VALID), HttpStatus.UNAUTHORIZED );
        }
        if (!gamePlayer.get ().getOpponent ().isPresent ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NO_OPPONENT), HttpStatus.FORBIDDEN );
        }

        if (ships.size () > 5) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_TOO_MANY + " SHIPS"), HttpStatus.FORBIDDEN );
        }

        if (!gamePlayer.get ().getSalvoes ().isEmpty () || !gamePlayer.get ().getOpponent ().get ().getSalvoes ().isEmpty ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_GAME_STARTED), HttpStatus.FORBIDDEN );
        }


        ships.stream ().forEach ( ship ->
            gamePlayer.get ().addShip ( ship ));

        gamePlayerRepository.save ( gamePlayer.get () );
        return new ResponseEntity <> (Response.INFO_SHIPS_SENT, HttpStatus.CREATED );

    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity <Object> getSalvoes(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo salvo) {

        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );

        if (!gamePlayer.isPresent ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NO_LOG_IN ), HttpStatus.UNAUTHORIZED );
        }
        if ((isGuest ( authentication )) || !gamePlayer.get ().getPlayer ().getUserName ().equals ( authentication.getName () )) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NOT_VALID), HttpStatus.UNAUTHORIZED );
        }

        if (!gamePlayer.get ().getOpponent ().isPresent ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NO_OPPONENT), HttpStatus.FORBIDDEN );
        }

        if (gamePlayer.get ().getShipsOpponent ().isEmpty ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_WAIT_OPPONENT + " SHIPS"), HttpStatus.FORBIDDEN );
        }

        if (!gamePlayer.get ().getSalvoes ().isEmpty () && salvo.getTurn ().longValue () < gamePlayer.get ().getSalvoes ().size () + 1) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_SALVO_FIRED_TWICE), HttpStatus.FORBIDDEN );
        }

        if (gamePlayer.get ().getState ().contains ( "ATTACK" ) || salvo.getTurn ().longValue () > gamePlayer.get ().getOpponent ().get ().getSalvoes ().size () + 1) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_WAIT_OPPONENT + " ATTACK!"), HttpStatus.FORBIDDEN );
        }

        if (salvo.getLocations ().size () > 5) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_TOO_MANY + " SHOTS"), HttpStatus.FORBIDDEN );
        }

        List <Salvo> opponentSalvoes = gamePlayer.get ().getOpponent ().get ().getSalvoes ().stream ().filter ( sa -> sa.getTurn () < salvo.getTurn ()).collect ( toList () );
        List <String> opponentSalvoesLocations = opponentSalvoes.stream ().map ( Salvo::getLocations ).collect ( toList () ).stream ().flatMap ( List::stream ).collect ( toList () );

        long sinks= gamePlayer.get ().getShips ().stream ().filter ( shi -> opponentSalvoesLocations.containsAll ( shi.getLocations () ) ).count ();

        if (gamePlayer.get().getMySunkenShips () > 0 && salvo.getLocations ().size () > gamePlayer.get ().getShips ().size () - sinks) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_TOO_MANY + " SHOTS"), HttpStatus.FORBIDDEN );
        }

        if (gamePlayer.get ().getGame ().getState ().contains ( "OVER" )) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_GAME_OVER ), HttpStatus.FORBIDDEN );
        }


        gamePlayer.get ().addSalvo ( salvo );


        if (gamePlayer.get ().getState ().contains ( "WON" )) {
            Score myPoints = new Score ( 1.0, gamePlayer.get ().getGame (), gamePlayer.get ().getPlayer (), (LocalDateTime.now ()) );
            Score opponentPoints = new Score ( 0d, gamePlayer.get ().getGame (), gamePlayer.get ().getOpponent ().get ().getPlayer (), (LocalDateTime.now ()) );
            scoreRepository.save ( myPoints );
            scoreRepository.save ( opponentPoints );


        } else if (gamePlayer.get ().getState ().contains ( "TIE" )) {
            Score myPoints = new Score ( 0.5, gamePlayer.get ().getGame (), gamePlayer.get ().getPlayer (), (LocalDateTime.now ()) );
            Score opponentPoints = new Score ( 0.5, gamePlayer.get ().getGame (), gamePlayer.get ().getOpponent ().get ().getPlayer (), (LocalDateTime.now ()) );
            scoreRepository.save ( myPoints );
            scoreRepository.save ( opponentPoints );


        } else if (gamePlayer.get ().getState ().contains ( "LOST" )) {
            Score myPoints = new Score ( 0d, gamePlayer.get ().getGame (), gamePlayer.get ().getPlayer (), (LocalDateTime.now ()) );
            Score opponentPoints = new Score ( 1.0, gamePlayer.get ().getGame (), gamePlayer.get ().getOpponent ().get ().getPlayer (), (LocalDateTime.now ()) );
            scoreRepository.save ( myPoints );
            scoreRepository.save ( opponentPoints );

        }


        gamePlayerRepository.save ( gamePlayer.get () );
        gamePlayerRepository.save ( gamePlayer.get ().getOpponent ().get () );
        return new ResponseEntity <> (Response.INFO_SALVOES_FIRED, HttpStatus.CREATED );

    }


    @PostMapping("/games")
    public ResponseEntity <Map <String, Object>> createGame(Authentication authentication) {

        if (isGuest ( authentication )) {
            return new ResponseEntity <> ( makeMap (Response.KEY_FAILURE, Response.ERR_NO_LOG_IN ), HttpStatus.UNAUTHORIZED );
        }

        Game newGame = gameRepository.save ( new Game ( (LocalDateTime.now ()) ) );
        Player player = playerRepository.findByUserName ( authentication.getName () );
        GamePlayer newGamePlayer = gamePlayerRepository.save ( new GamePlayer ( player, newGame ) );

        return new ResponseEntity <> ( makeMap ( "gpid", newGamePlayer.getId () ), HttpStatus.CREATED );


    }


    @PostMapping("/game/{gameId}/players")
    public ResponseEntity <Map <String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        if (isGuest ( authentication )) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NO_LOG_IN ), HttpStatus.UNAUTHORIZED );
        }

        Optional <Game> game = gameRepository.findById ( gameId );
        if (!game.isPresent ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NOT_FOUND), HttpStatus.FORBIDDEN );
        }

        if (game.get ().getPlayers ().size () > 1) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_TOO_MANY + " PLAYERS"), HttpStatus.FORBIDDEN );
        }


        Player player = playerRepository.findByUserName ( authentication.getName () );
        GamePlayer newGamePlayer = gamePlayerRepository.save ( new GamePlayer ( player, game.get () ) );

        return new ResponseEntity <> ( makeMap ( "gpid", newGamePlayer.getId () ), HttpStatus.CREATED );

    }


    private Map <String, Object> makeMap(String key, Object value) {
        Map <String, Object> map = new HashMap <> ();
        map.put ( key, value );
        return map;
    }


    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity <Map <String, Object>> gameView(@PathVariable Long gamePlayerId, Authentication authentication) {

        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );
        if (!gamePlayer.isPresent ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_NO_LOG_IN ), HttpStatus.UNAUTHORIZED );
        }

        Player player = playerRepository.findByUserName ( authentication.getName () );
        if (gamePlayer.get ().getPlayer ().getId () != player.getId ()) {
            return new ResponseEntity <> ( makeMap ( Response.KEY_FAILURE, Response.ERR_UNAUTHORIZED ), HttpStatus.UNAUTHORIZED );
        }

        return new ResponseEntity <> (gamePlayer.get ().toGameViewDTO (), HttpStatus.OK );
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }







}


