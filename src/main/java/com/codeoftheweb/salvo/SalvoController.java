package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/games")
    public Map <String, Object> getPlayerPlusGames(Authentication authentication) {

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
        return dto;

    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map <String, Object> gameView(@PathVariable Long gamePlayerId) {
        Optional <GamePlayer> gamePlayer = gamePlayerRepository.findById ( gamePlayerId );
        
//        if (logged in player = gamePlayer)

        return gamePlayer.get ().toGameViewDTO ();
//        else(return error unauthorized)

    }


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity <Map<String,Object>> register(

            @RequestParam String email, @RequestParam String password) {

        if (!(email.contains ("@")) ||email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Please enter a valid e-mail/password"), HttpStatus.FORBIDDEN);
        }

       Player player = playerRepository.findByUserName ( email );
        if (player != null){
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }

        Player newPlayer = playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity <> (newPlayer.toDTO (), HttpStatus.CREATED);

    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
   

}


