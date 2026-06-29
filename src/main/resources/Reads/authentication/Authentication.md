## Authorization and Authentication

Authentication tells us who are you whereas Authorization tells us do you have correct rights/roles to do some activity 


## Types of Authentication mechanism

1. **Session based** : The server creates a session in memory(or REDIS) and sends `JSESSIONID` cookie to the client.
    Pro: Easy to revoke immediately
    Con: Doesn't scale horizontally well without shared session, prone to CSRF
2. **Token based (Stateless/ JWT)** : The server validates the credentials and hands back the cryptographically signed token
    Pro: perfect for REST API and microservices, no server memory usage for sessions
    Con: hard to revoke a token before it actually expires(requires a "blacklist")
3. **Oauth2 + OpenID connect(OIDC)** : The industrial standard, we delegate the identity check to an **Identity Provider (IdP)**
    Pro: Login with Google/GitHub, extremely secure, separates the _user info_ from _access permissions_.
    Con: High initial complexity



## How and what we should implement?

Let's follow the `Token Exchange` sequence

**Step 1**: The **Authentication Manager**:
In our `SecurityConfig` , we should define `AuthenticationManager` bean. This bean uses a `DaoAuthenticationProvoder` to check the `UserDeatilsService`(our database)
and compare the password using `BCryptPasswordEncoder`

**Step 2**: The Login Endpoint
1. Create an `/auth/login` controller
2. Receive `UserNamePasswordAuthenticationToken`
3. Pass it to `AuthenticationManager`
4. if successful, use `JwtUtils` class to build JWT string contains **Claims**(Subject, Roles, IssuedAt, Expiration)
5. Return JWT in response body


**Step 3**: The Global filter (The interceptor)
Create a class extending `OncePerRequestFilter`
1. Extract: Grab the `authorization` header
2. Validate the signature against the secretKey
3. Inject: if valid, create a `UsernamePasswordAuthenticationToken` and place it into the `SecurityContextHolder`




Steps : 
1. Add cryptographic dependencies:
> jjwt-api: This contains interfaces and classes we actually import in Java code like Jwt and Claims
> jjwt-impl: This contains actual logic for signing and verifying. we don't call this directly, jjwt-api uses reflection at the runtime
> jjwt-jackson: JWT are just Base64-encoded JSON. This module tells JJWT how to use existing Springboot Jackson library to turn Java objects/claims into JSON strings
>
    `<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-impl</artifactId>
       <version>0.11.5</version>
       <scope>runtime</scope>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-jackson</artifactId>
       <version>0.11.5</version>
       <scope>runtime</scope>
   </dependency>`



2. The JWTUtils Service - This is going to be the token factory, It would generate the token after a successful login and validates it when a request comes back.

`@Component
public class JwtUtils {
// This key must be long and complex!
private String jwtSecret = "afafasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf";
private int jwtExpirationMs = 86400000; // 24 hours

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parse(authToken);
            return true;
        } catch (Exception e) {
            // Log the error (Expired, Malformed, etc.)
        }
        return false;
    }
}`

3. The AuthTokenFilter - The interceptor
    This filter sits in-front of everyRequest. It looks for `Authorization: Bearer <TOKEN>` header, validates it and tells Spring Security who is making the request
    
        `public class AuthTokenFilter extends OncePerRequestFilter {
           @Autowired private JwtUtils jwtUtils;
           @Autowired private MyUserDetailsService userDetailsService;
        
           @Override
           protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
           throws ServletException, IOException {
           try {
           String jwt = parseJwt(request);
           if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
           String username = jwtUtils.getUserNameFromJwtToken(jwt);
           UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) { /* Log error */ }
        filterChain.doFilter(request, response);
       }
    
       private String parseJwt(HttpServletRequest request) {
       String headerAuth = request.getHeader("Authorization");
       if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
       return headerAuth.substring(7);
       }
       return null;
       }
       }`
   4. The Security configuration : now we tie it all together and tell Spring Security to be **Stateless** and now use our new `AuthTokenFilter`
            `@Configuration
              @EnableWebSecurity
              public class WebSecurityConfig {
        
              @Bean
              public AuthTokenFilter authenticationJwtTokenFilter() {
              return new AuthTokenFilter();
              }
        
              @Bean
                public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                    http.csrf(csrf -> csrf.disable())
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Login/Register endpoints
                        .anyRequest().authenticated()
                        );

           http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
           return http.build();
            }

          @Bean
          public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
          }
          }`
      5. The Login controller : Now we would need a dedicated endpoint to exchange the username/password for a JWT

           `@RestController
              @RequestMapping("/api/v1/auth")
              public class AuthController {
              @Autowired AuthenticationManager authenticationManager;
              @Autowired JwtUtils jwtUtils;
    
              @PostMapping("/login")
              public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
              Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
    
               SecurityContextHolder.getContext().setAuthentication(authentication);
               String jwt = jwtUtils.generateToken(authentication);
            
               return ResponseEntity.ok(new JwtResponse(jwt));
          }
      }`
    