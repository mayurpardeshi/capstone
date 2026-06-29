## How we configure authrorization in Spring Security

Imagine we have a basic spring boot app and we have spring security dependencies added, and 
we have few users the whole idea is to make sure few apis' are accessible to certain users and certain to others


Enables

API - allow access to 
/   - all (unauthenticated)
/users   - only logged-in users
/admin   - users having admin role


How can we do this in Spring boot ?
Using `HttpSecurity` interface, we put our authorization configuration
WebSecurityConfigurerAdapter

We create a class which would extend `WebSecurityConfigurerAdapter` and we can override few methods 