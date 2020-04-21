package com.codeoftheweb.salvo.configurations;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder ();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService ( email -> {
            Player player = playerRepository.findByUserName ( email );
            if (player != null) {
                return new User ( player.getUserName (), player.getPassword (),
                        AuthorityUtils.createAuthorityList ( "USER" ) ) {
                };
            } else {
                throw new UsernameNotFoundException ( "Unknown user: " + email );
            }
        } );
    }
}
