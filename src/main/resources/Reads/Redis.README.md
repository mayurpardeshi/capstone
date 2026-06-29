## How to implement caching in our Spring boot project??
I am not using Redis Template directly, just in case, if we have to change from Redis to Caffeine tomorrow
Cons of using Redis Template directly :
1. Couples business logic to Redis
2. Makes testing harder
3. violates separation of concern and make switching the cache provider tough


Controller
↓
Service  (@Cacheable)
↓
Repository

REDIS sits behind Spring Cache

Steps to add : 
1. add dependency in pom.xml file : `spring-boot-starter-data-redis`
2. Enable caching at main entry point : add `@EnableCaching`
3. Add redis conf in application.yml : give info like which cache provider? where is redis ? TTL ? Serialization and pool size
4. Add redis configuration
After above steps we can use caching using `@Cacheable`
Do not forget to evict from cache conditions