## JPA Repositories
To understand JPA we need to start thinking of it as a **dynamic proxy factory**
When we extend JPARepository - then we are not writing code, but providing a specification that a sophisticated engine uses to generate byte code at runtime

1. The proxy pattern (Magic stuff)
    
    Since `UserRepository` is an interface it has no implementation. When spring boots up, it uses dynamic JDK proxies
2. Spring scans teh classpath and finds interface
3. It sees it extends `JPARepository` 
4. Hence, it creates a proxy object - that implements this interface
5. so whenever we call `userRepository.save(user)` we are actually calling this method on the proxy
6. Under the hood proxy redirects every call to `SimpleJPARepository`
   7. It is the real worker - it contains actual logic for : save() : calls entityManager.persists() or entityManager.merge()
   8. for `findBy()` : calls entityManager.find()
9. So when we implement JPARepository: we are actually implementing pre-written logic of SimpleJPARepository


## Query parsing:
The nerdiest part is how Spring handles methods like findByEmailAndFirstName(String email, String name).
Spring uses a **Query Derivation Engine**. It breaks the method name into "_Atomic Parts_" using a _state machine_:
**Prefix**: findBy (This tells it to start a SELECT).
**Property**: Email (It checks your User entity for a field named email).
**Operator**: And.
**Property**: FirstName.
It then translates these tokens into a Criteria API Tree, which Hibernate eventually converts into raw SQL. 
If you make a typo (e.g., findByEmailll), the parsing fails at startup—which is why your app won't even boot 
if the query names are wrong.


## The Bridge : Java to SQL

1. Our Interface - API Definition
2. Spring Proxy - Intercepts teh call and manage the Transactions (@Transaction)
3. SimpleJPARepository - translates this call to JPA commands
4. Persistence Context - Manages 1st level cache(L1) and "Dirty checking"
5. Hibernate - Translate JPA commands to a SQL specific query(Dialect)
6. JDBC - Sends the raw SQL bytes over the network to the Db