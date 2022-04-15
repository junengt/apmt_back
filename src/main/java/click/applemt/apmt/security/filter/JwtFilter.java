package click.applemt.apmt.security.filter;

import click.applemt.apmt.security.util.RequestUtil;
import click.applemt.apmt.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j

public class JwtFilter extends OncePerRequestFilter {

    private  UserService userService;
    private  FirebaseAuth firebaseAuth;

    public JwtFilter(UserService userService, FirebaseAuth firebaseAuth) {
        this.userService = userService;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        FirebaseToken decodedToken;
        try{
            String header = RequestUtil.getAuthorizationToken((request.getHeader("Authorization")));
            decodedToken = firebaseAuth.verifyIdToken(header);
        } catch (FirebaseAuthException | IllegalArgumentException  e) {
            log.info("token verify exception: " + e.getMessage());
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
            return ;

        }

        try {
            UserDetails user = userService.loadUserByUsername(decodedToken.getUid());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user,null, Collections.singleton(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "User";
                }
            }));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (UsernameNotFoundException e) {
            log.info("token verify exception: " + e.getMessage());
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
            return ;
        }
        filterChain.doFilter(request,response);
    }
}
