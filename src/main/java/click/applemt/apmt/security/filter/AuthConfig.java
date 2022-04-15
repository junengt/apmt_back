package click.applemt.apmt.security.filter;

import click.applemt.apmt.config.FirebaseInit;
import click.applemt.apmt.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class AuthConfig {

    @Autowired
    UserService userService;
    @Autowired
    FirebaseAuth firebaseAuth;

    @Bean
    public AuthFilterContainer jwtAuthFilter(){
        AuthFilterContainer authFilterContainer = new AuthFilterContainer();
        authFilterContainer.setAuthFilter(new JwtFilter( userService, firebaseAuth));
        return authFilterContainer;
    }

}
