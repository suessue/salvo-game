package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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



    @PostMapping("/players")
    public ResponseEntity <Map<String,Object>> register(@RequestParam String email, @RequestParam String password) {

        if (!(email.contains ("@")) || !email.contains(".") ||email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Please enter a valid e-mail/password"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName ( email );
        if (player != null){
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }

        Player newPlayer = playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity <> (newPlayer.toDTO (), HttpStatus.CREATED);

    }


    @GetMapping("/games")
    public ResponseEntity <Map<String,Object>> getPlayerPlusGames(Authentication authentication) {

        Map <String, Object> dto = new LinkedHashMap <> (  );
        if (isGuest (authentication)) {
            dto.put ("player", null);

        }else {
            Player player = playerRepository.findByUserName ( authentication.getName ());
            dto.put ( "player", player.toDTO ()) ;
        }
        dto.put ( "games",  gameRepository.findAll ()
                .stream ()
                .map ( Game::toDTO )
                .collect ( Collectors.toList () ));


        return new ResponseEntity<> (dto, HttpStatus.OK);

    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity <Object> getShips(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody List <Ship> ships ) {

        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );

        if (!gamePlayer.isPresent ()){
            return new ResponseEntity <> (  "Log in first!" , HttpStatus.UNAUTHORIZED );
        }
        if ((isGuest ( authentication )) || gamePlayer.get ().getPlayer ().getUserName () != authentication.getName ()) {
            return new ResponseEntity <> ( "Not valid" , HttpStatus.UNAUTHORIZED );
        }

        if (gamePlayer.get ().getShips ().size() >= 5) {
            return new ResponseEntity <> ( "You have already picked all your ships." , HttpStatus.FORBIDDEN );
        }



        ships.stream ().forEach ( ship -> {
            gamePlayer.get ().addShip ( ship );
        } );

        gamePlayerRepository.save ( gamePlayer.get () );
        return new ResponseEntity <> ( "Your ship is on the game!" , HttpStatus.CREATED );

    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity <Object>  getSalvoes(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo salvo){

        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );
        if (!gamePlayer.isPresent ()){
            return new ResponseEntity <> (  "Log in first!" , HttpStatus.UNAUTHORIZED );
        }
        if ((isGuest ( authentication ))  || gamePlayer.get ().getPlayer ().getUserName () != authentication.getName ()) {
            return new ResponseEntity <> (  "You are not the game player" + gamePlayer.get().getId (), HttpStatus.UNAUTHORIZED );
        }

        if (salvo. getTurn() != gamePlayer.get().getSalvoes ().size() + 1 ) {
            return new ResponseEntity <> ( "Salvo already fired for this turn" , HttpStatus.FORBIDDEN );
        }


        if (gamePlayer.get().getSalvoes ().stream ().count () > 5) {
            return new ResponseEntity <> ( "Too many shots in salvo" , HttpStatus.FORBIDDEN );
        }


        gamePlayer.get ().addSalvo ( salvo );
       
        gamePlayerRepository.save ( gamePlayer.get () );
        return new ResponseEntity <> ( "Your shots have been fired!" , HttpStatus.CREATED );
    }


    @PostMapping("/games")
    public ResponseEntity <Map<String,Object>> createGame(Authentication authentication){

        if (isGuest (authentication)) {
            return new ResponseEntity<>(makeMap("error", "You need to login first!"), HttpStatus.UNAUTHORIZED);
        }

        Game newGame = gameRepository.save(new Game((LocalDateTime.now ())));
        Player player = playerRepository.findByUserName ( authentication.getName ());
        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer( player, newGame));

       return new ResponseEntity <> (makeMap ("gpid", newGamePlayer.getId()), HttpStatus.CREATED);


    }


    @PostMapping("/game/{gameId}/players")
    public ResponseEntity <Map<String,Object>> joinGame(@PathVariable Long gameId, Authentication authentication )  {

    if (isGuest (authentication)) {
        return new ResponseEntity<>(makeMap("error", "You need to login first."), HttpStatus.UNAUTHORIZED);
    }

        Optional <Game> game = gameRepository.findById (gameId);
        if (!game.isPresent ()){
            return new ResponseEntity<>(makeMap("error", "No such game."), HttpStatus.FORBIDDEN);
        }

        if (game.get().getPlayers ().size() > 1) {
            return new ResponseEntity <> ( makeMap ("error", "Game is full." ), HttpStatus.FORBIDDEN );
        }


        Player player = playerRepository.findByUserName ( authentication.getName ());
        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer( player, game.get()));

        return new ResponseEntity <> (makeMap ("gpid", newGamePlayer.getId()), HttpStatus.CREATED);

    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    @GetMapping ("/game_view/{gamePlayerId}")
    public ResponseEntity <Map<String,Object>> gameView(@PathVariable Long gamePlayerId, Authentication authentication ) {

        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );
        if (!gamePlayer.isPresent ()) {
            return new ResponseEntity<>(makeMap("error", "This game has not been found."), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName ( authentication.getName ());
        if (gamePlayer.get ().getPlayer ().getId () != player.getId ()){
            return new ResponseEntity<>(makeMap ("error", "You are not authorized to see this info."), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity <> (gamePlayer.get().toGameViewDTO (), HttpStatus.OK);
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
   

}


