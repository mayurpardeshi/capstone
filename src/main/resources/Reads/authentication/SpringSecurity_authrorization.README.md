In modern Spring Boot (3.x), we’ve moved away from the deprecated WebSecurityConfigurerAdapter 
toward a component-based security configuration.

Core dependency is `spring-boot-starter-security`
We need to define a bean of type SecurityFilterChain. This is where we map URL's to specific permissions
