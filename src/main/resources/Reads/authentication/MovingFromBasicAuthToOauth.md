1. Add dependency `spring-boot-starter-oauth2-resource-server`
2. Generating keys : Fot OAuth2, we use asymmetric encryption
   Public key : Used by our app to verify token
    Private key : Used by our app to sign(create) token
3. Add TokenService - that would take Authentication object (after successful login) and turns it into a signed JWT token
4. Then update Security config to tell SecurityChainFilter to stop expecting userName and Password in the header and start expecting Bearer Token
5. The Login endpoint should be created 