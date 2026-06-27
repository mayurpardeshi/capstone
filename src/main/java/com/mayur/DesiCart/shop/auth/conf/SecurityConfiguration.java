package com.mayur.DesiCart.shop.auth.conf;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/login",
            "/api/v1/users/all/**",
            "/api/v1/orders/razorpay"
    };

    private static final String PRODUCT_PATH = "/api/v1/products/**";

    private RSAKey rsaKey;
    // 1. to change to Oauth2 resource server
    @Bean
    public JWKSource<SecurityContext> jwkSource(){
        rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return ((jwkSelector, securityContext) -> jwkSelector.select(jwkSet));
    }

    private static RSAKey generateRsa() {
        KeyPair keyPair = generateKeyPair();
        return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();

    }

    private static KeyPair generateKeyPair(){
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return keyPair;
    }

    // define encoder(used by TokenService to sign tokens)
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource){
        // JwtEncoder : It uses the Private Key to sign our JWTs.
        return new NimbusJwtEncoder(jwkSource);
    }

    // define decoder (used by Resource Server to verify Tokens)
    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        // JwtDecoder: This uses the Public Key to ensure that any token coming into our API was actually created by us.
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    // 1. Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 2. Authentication Provider (Our validator)
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // 3. Authentication Manager (The BOss that coordinates provider)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    configureSwaggerPaths(auth);
                    configurePaymentPaths(auth);
                    configurePublicPaths(auth);
                    configureActuator(auth);
                    configureProductPaths(auth);
                    configureUserManagementPaths(auth);
                    auth.anyRequest().authenticated();
                        }
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    private void configureSwaggerPaths(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(SWAGGER_WHITELIST).permitAll();
    }

    private void configurePublicPaths(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
    }

    private void configurePaymentPaths(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/view/payments/**").permitAll();
    }

    private void configureActuator(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth){
        auth.requestMatchers("/actuator/**").hasRole("ADMIN");
    }

    private void configureUserManagementPaths(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/api/v1/users/a/**").hasRole("ADMIN");
        auth.requestMatchers("/api/v1/users/u/**").hasAnyRole("USER", "ADMIN");
    }

    private void configureProductPaths(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        // READ: Both roles
        auth.requestMatchers(HttpMethod.GET, PRODUCT_PATH).hasAnyRole("USER", "ADMIN");

        // WRITE: Admin only (Create, Update, Delete)
        auth.requestMatchers(HttpMethod.POST, PRODUCT_PATH).hasRole("ADMIN");
        auth.requestMatchers(HttpMethod.PATCH, PRODUCT_PATH).hasRole("ADMIN");
        auth.requestMatchers(HttpMethod.DELETE, PRODUCT_PATH).hasRole("ADMIN");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/**")
                        .allowedOrigins("http://127.0.0.1:5500", "http://localhost:3000") // Your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }




    /*
    By default, Spring Security’s JwtAuthenticationConverter looks at the scope or scp claim in our JWT and prefixes it with SCOPE_.
    This is the OAuth2 standard.
    * Because Oauth2 has by default JwtAuthenticationToken with authority as SCOPE_ROLE_USER
    * */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix(""); // Removing SCOPE_
        authoritiesConverter.setAuthoritiesClaimName("scope"); // use "scope" claim

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;

    }

    /*
    * Want to make swagger Api's out of my security chain, swagger, api-docs are not required to have key to enter
    * */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v3/api-docs/**", "/swagger-ui/**");
    }


}
