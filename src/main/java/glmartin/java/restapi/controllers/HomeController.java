package glmartin.java.restapi.controllers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "MSA REST API";
    }

    @GetMapping("/token")
    public Token getToken(JwtAuthenticationToken jwtToken) {
        return new Token(
                jwtToken.getToken(),
                jwtToken.getAuthorities()
        );
    }
    public record Token(Jwt token, Collection<GrantedAuthority> authorities){}
}
