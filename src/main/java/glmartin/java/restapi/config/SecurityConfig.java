package glmartin.java.restapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwksUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    private static final String[] PROTECTED_PATHS = new String[]{
            "/api/organizations/**",
            "/api/app_users/**"
    };

    // TODO: Allow public paths
    private static final String[] PUBLIC_PATHS = new String[]{
            "/api/home"
    };

    /**
     * Override the default SecurityFilterChain
     * <p>
     * See: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
     * <p>
     * https://www.baeldung.com/spring-security-map-authorities-jwt
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PROTECTED_PATHS).access(hasAuthority("msa-orgs"))
                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                                        .decoder(jwtDecoder())
                        )
                )
        ;
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter kcJWTRealmRoleConverter = new JwtAuthenticationConverter();

        // implement a new Converter<Jwt, Collection<GrantedAuthority>> to convert the realm_access.roles from th
        // JWT into a Collection of GrantedAuthority objects.
        kcJWTRealmRoleConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            return ((List<String>) realmAccess.get("roles")).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return kcJWTRealmRoleConverter;
    }
}