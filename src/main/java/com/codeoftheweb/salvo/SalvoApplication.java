package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run ( SalvoApplication.class, args );
    }

    @Bean public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ScoreRepository scoreRepository) {
        return args -> {
            Player player1 = new Player ( "j.bauer@ctu.gov", passwordEncoder.encode("24"));
            Player player2 = new Player ( "c.obrian@ctu.gov", passwordEncoder.encode( "42"));
            Player player3 = new Player ( "t.almeida@ctu.gov", passwordEncoder.encode("mole"));
            Player player4 = new Player ( "d.palmer@whitehouse.gov", passwordEncoder.encode("kb"));

            Ship ship1 = new Ship ( "Destroyer", Arrays.asList ( "H2", "H3", "H4" ) );
            Ship ship2 = new Ship ( "Submarine", Arrays.asList ( "E1", "F1", "G1" ) );
            Ship ship3 = new Ship ( "Patrol Boat", Arrays.asList ( "B4", "B5" ) );
            Ship ship4 = new Ship ( "Destroyer", Arrays.asList ( "B5", "C5", "D5" ) );
            Ship ship5 = new Ship ( "Patrol Boat", Arrays.asList ( "F1", "F2" ) );
            Ship ship6 = new Ship ( "Destroyer", Arrays.asList ( "B5", "C5", "D5" ) );
            Ship ship7 = new Ship ( "Patrol Boat", Arrays.asList ( "C6", "C7" ) );
            Ship ship8 = new Ship ( "Submarine", Arrays.asList ( "A2", "A3", "A4" ) );
            Ship ship9 = new Ship ( "Patrol Boat", Arrays.asList ( "G6", "H6" ) );
            Ship ship10 = new Ship ( "Destroyer", Arrays.asList ( "B5", "C5", "D5" ) );
            Ship ship11 = new Ship ( "Patrol Boat", Arrays.asList ( "C6", "C7" ) );
            Ship ship12 = new Ship ( "Submarine", Arrays.asList ( "A2", "A3", "A4" ) );
            Ship ship13 = new Ship ( "Patrol Boat", Arrays.asList ( "G6", "H6" ) );

            Salvo salvo1 = new Salvo ( 1, Arrays.asList ( "B5", "C5", "F1" ) );
            Salvo salvo2 = new Salvo ( 1, Arrays.asList ( "B4", "B5", "B6" ) );
            Salvo salvo3 = new Salvo ( 2, Arrays.asList ( "F2", "D5" ) );
            Salvo salvo4 = new Salvo ( 2, Arrays.asList ( "E1", "H3", "A2" ) );
            Salvo salvo5 = new Salvo ( 1, Arrays.asList ( "A2", "A4", "G6" ) );
            Salvo salvo6 = new Salvo ( 1, Arrays.asList ( "B5", "D5", "C7" ) );
            Salvo salvo7 = new Salvo ( 2, Arrays.asList ( "A3", "H6" ) );
            Salvo salvo8 = new Salvo ( 2, Arrays.asList ( "C5", "C6" ) );
            Salvo salvo9 = new Salvo ( 1, Arrays.asList ( "G6", "H6", "A4" ) );
            Salvo salvo10 = new Salvo ( 1, Arrays.asList ( "H1", "H2", "H3" ) );
            Salvo salvo11 = new Salvo ( 2, Arrays.asList ( "A2", "A3", "D8" ) );
            Salvo salvo12 = new Salvo ( 2, Arrays.asList ( "E1", "F2", "G3" ) );

            Game game1 = new Game ( (LocalDateTime.now ()) );
            Game game2 = new Game ( (LocalDateTime.now ().plusHours ( 1 )) );
            Game game3 = new Game ( (LocalDateTime.now ().plusHours ( 2 )) );


            GamePlayer gamePlayer1 = new GamePlayer ( player1, game1 );
            gamePlayer1.addShip ( ship1 );
            gamePlayer1.addShip ( ship2 );
            gamePlayer1.addShip ( ship3 );
            gamePlayer1.addSalvo ( salvo1 );
            gamePlayer1.addSalvo ( salvo3 );

            GamePlayer gamePlayer2 = new GamePlayer ( player2, game1 );
            gamePlayer2.addShip ( ship4 );
            gamePlayer2.addShip ( ship5 );
            gamePlayer2.addSalvo ( salvo2 );
            gamePlayer2.addSalvo ( salvo4 );



            GamePlayer gamePlayer3 = new GamePlayer ( player1, game2 );
            gamePlayer3.addShip ( ship6 );
            gamePlayer3.addShip ( ship7 );
            gamePlayer3.addSalvo ( salvo5 );
            gamePlayer3.addSalvo ( salvo7 );

            GamePlayer gamePlayer4 = new GamePlayer ( player2, game2 );
            gamePlayer4.addShip ( ship8 );
            gamePlayer4.addShip ( ship9 );
            gamePlayer4.addSalvo ( salvo6 );
            gamePlayer4.addSalvo ( salvo8 );

            GamePlayer gamePlayer5 = new GamePlayer ( player2, game3 );
            gamePlayer5.addShip ( ship10 );
            gamePlayer5.addShip ( ship11 );
            gamePlayer5.addSalvo ( salvo9 );
            gamePlayer5.addSalvo ( salvo11 );

            GamePlayer gamePlayer6 = new GamePlayer ( player3, game3 );
            gamePlayer6.addShip ( ship12 );
            gamePlayer6.addShip ( ship13 );
            gamePlayer6.addSalvo ( salvo10 );
            gamePlayer6.addSalvo ( salvo12 );

            Score score1 = new Score ( 1, game1, player1, game1.getCreationDate ().plusMinutes ( 30 ) );
            Score score2 = new Score ( 0,game1, player2, game1.getCreationDate ().plusMinutes ( 30 ) );
            Score score3 = new Score ( 0.5, game2, player1, game2.getCreationDate ().plusMinutes ( 30 ) );
            Score score4 = new Score ( 0.5, game2, player2, game2.getCreationDate ().plusMinutes ( 30 ) );
            Score score5 = new Score ( 1, game3, player2, game3.getCreationDate ().plusMinutes ( 30 ) );
            Score score6 = new Score ( 0, game3, player3, game3.getCreationDate ().plusMinutes ( 30 ) );

            playerRepository.save ( player1 );
            playerRepository.save ( player2);
            playerRepository.save ( player3);
            playerRepository.save ( player4 );

            gameRepository.save ( game1 );
            gameRepository.save ( game2 );
            gameRepository.save ( game3 );

            gamePlayerRepository.save ( gamePlayer1 );
            gamePlayerRepository.save ( gamePlayer2 );
            gamePlayerRepository.save ( gamePlayer3 );
            gamePlayerRepository.save ( gamePlayer4 );
            gamePlayerRepository.save ( gamePlayer5 );
            gamePlayerRepository.save ( gamePlayer6 );

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);

    };
    }


}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(email-> {
            Player player = playerRepository.findByUserName ( email );
            if (player != null) {
                return new User ( player.getUserName (), player.getPassword (),
                        AuthorityUtils.createAuthorityList ( "USER" ) ) {
                };
            } else {
                throw new UsernameNotFoundException ("Unknown user: " + email);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/web/game.html", "/api/game_view/**").hasAuthority("USER")
                .antMatchers("/rest/**").hasAuthority("ADMIN")
                .antMatchers("/web/**").permitAll ()
                .antMatchers("/api/**").permitAll ();

        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");
        // turn off checking for CSRF tokens
        http.csrf().disable();
        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError( HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler ());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute( WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
