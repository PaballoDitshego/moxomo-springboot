
package za.co.moxomo.config;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import za.co.moxomo.model.Role;
import za.co.moxomo.model.User;
import za.co.moxomo.repository.mongodb.UserRepository;
import za.co.moxomo.services.UserService;


@Component
public class JwtTokenProvider {


    private String secretKey;
    private long validityInMilliseconds;

    private UserService userService;

    private JwtConfig jwtConfig;

    @Autowired
    public JwtTokenProvider(UserService userService, JwtConfig jwtConfig) {
        this.userService = userService;
        this.jwtConfig=jwtConfig;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecret().getBytes());
        validityInMilliseconds=jwtConfig.getExpiration();
    }

    public String createToken(User user) {

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("authorities", user.getRoles().stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).filter(Objects::nonNull).collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String username) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public JwtConfig getJwtConfig() {
        return jwtConfig;
    }
}