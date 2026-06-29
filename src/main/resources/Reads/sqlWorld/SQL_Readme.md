## Databases(Db)

Databases are something which can store some value of a particular data type and when we say it's a relational Db
it means it logically stores data in form of rows and columns.
Another important aspect is to get the data back, how can we do it ? Fetching rows and columns.

Also to Db to function correctly, while storing to maintain integrity of data we must also ensure that at least there should be something from which we can uniquely identify a particular row.
Hence, comes the concept of key constraints.
Keys are something we enforce **Entity Integrity** and **Referential Integrity**
Few types of keys:
1. Super key: This is a set of keys which can uniquely identify a record. 
    This becomes any set of one or more columns that can taken together to uniquely identify a row in table
    Think of it as the "overkill" key. If you have a Users table, a Super Key could be just the user_id. 
    But, it could also be (user_id, email) or even (user_id, email, favorite_flavor_of_chips). 
    As long as the combination is unique across the entire table, it's a Super Key
2. Candidate key: To be a Candidate Key, the set of attributes must uniquely identify a row, but if you remove even one attribute from the set, it loses its uniqueness.
    The Criteria: 1. Uniqueness: No two rows can have the same value. 2. Irreducibility: No proper subset of the key is a super key.
   In our Users table, user_id is a candidate key. email is likely a candidate key. However, (user_id, email) is not a candidate key because it isn't minimal (you could remove email and still identify the row).
3. Primary key: The Primary Key is simply whichever Candidate Key the Database Architect selects to be the official unique identifier for the table
4. Foreign key: A Foreign Key is where the "Relational" part of RDBMS comes to life. It is an attribute (or collection of attributes) in one table that refers to the Primary Key of another table


## Schema Design:

### Normalization
To design a good schema we need to worship at the altar of Normalization. The goal is to reduce data redundancy and also ensure data integrity.
1. 1NF (First Normal Form): Eliminate duplicate columns and ensure every cell contains an atomic (indivisible) value. 
    No arrays or comma-separated strings!
2. 2NF (Second Normal Form): Meet 1NF and ensure all non-key attributes are fully functional dependent on the entire Primary Key (no partial dependencies).
3. 3NF (Third Normal Form): Meet 2NF and ensure no non-key attribute depends on another non-key attribute (No transitive dependencies). "Tell the truth, the whole truth, and nothing but the truth, so help me Codd."


Let us decode above using an example:
Imagine we create a table tracking players and their matches:
**Player_ID      Player_Name        Matches_Played         Character_Classes      Tournament_Location**
101            Leeroy             Match_A, Match_B        Paladin, Mage          New York, NY

**1NF says:** Every column must have single atomic value, no sets , no arrays
so, we flatten table by making Match_A, Match_B to new rows

**Player_ID      Player_Name        Matches_Played         Character_Classes      Tournament_Location**
101            Leeroy             Match_A                     Paladin, Mage          New York, NY
101            Leeroy             Match_B                     Paladin, Mage          New York, NY

At this point we don't have a primary key, but we have a composite primary key

**2NF says:** meets 1NF and ensure every non-key attribute depends on entire primary key and not just piece of it.
Look at `Player_Name`. Does it depend on the `Match_ID`? No. It only depends on the `Player_ID`. 
This is a **Partial Dependency**. If Leeroy changes his name, we have to update multiple rows.
Hence, we can split table and now:
**Table: Players**:
* player_id (PK)
* player_name

**Table: Player_Matches**:
* player_id
* match_id
* character_class
* tournament_location

**Table: Matches**:
* match_id (PK)
* match_name

We made it 2NF complaint

**3NF says**: meet 2NF and says no non-key attribute depends on another non-key attribute
So look at `tournament_location` - depends on matchId, however if we also had `tournament_id` 
the location would be dependent on `tournament_id`

**Table: Players**:
* player_id (PK)
* player_name

**Table: Player_Matches**:
* player_id
* match_id
* character_class
* tournament_id

**Table: Tournament**:
* tournament_id (PK)
* tournament_location

**Table: Matches**:
* match_id (PK)
* match_name

By now we have achieved 3NF, we have now single source of truth:
1. Insert Anomaly: You can add a new Tournament without needing a Player to be signed up for it.

2. Update Anomaly: If a Player changes their name, you update exactly one cell in the Players table.

3. Delete Anomaly: If you delete a match, you don't accidentally delete the Player's existence from the database


### Cardinality:
Cardinality Constraints that dictate how the relational engine traverses the B-Tree indexes to find data.
In the Spring Boot ecosystem, specifically using Spring Data JPA (Hibernate), we move from Relational Algebra to ORM (Object-Relational Mapping).
We are mapping a Directed Graph of Java Objects to a Relational Set of SQL tables. 
Hibernate handles the "impedance mismatch" using specific annotations.
1. One to one : `@OneToOne` - This is implemented with a shared primary key or a unique foreign key
    When we use `optional = false` to enforce inner join at Db level, maps a single foreign key with a `UNIQUE` constraints
        Eg. `@Entity
            public class User {
            @Id @GeneratedValue
            private Long id;
           @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
           private UserProfile profile;
           }`
        
            `@Entity
            public class UserProfile {
            @Id
            private Long id;
    
            @OneToOne
            @MapsId // Shares the Primary Key with User
            @JoinColumn(name = "user_id")
            private User user;
            }`
   Note: `mappedBy` tells Hibernate that the UserProfile table "owns" the foreign key. Without it, Hibernate might try to create a join table or FKs in both tables, which is redundant.
2. **Many to one** or **One to many**: This represents a parent child relationship
    `@ManyToOne` side is always the owner of the relationship, also avoid using `List<>` for _many_ side, if we plan to remove items
    Use `Set` to prevent hibernate from deleting and re-inserting the entire collection(known N+1 problem)
    Eg.
        `@Entity
         public class Publisher {
             @Id @GeneratedValue
             private Long id;
             @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
             private Set<Book> books = new HashSet<>();
         }`

         @Entity
         public class Book {
             @Id @GeneratedValue
             private Long id;

             @ManyToOne(fetch = FetchType.LAZY) // Always use LAZY for performance
             @JoinColumn(name = "publisher_id") // This creates the FK in the Book table
             private Publisher publisher;
         }`
3. Many to Many: `@ManyToMany` - this would create a junction table. Hibernate handles the bridge table for us.
    if we wish to store extra data on relationship(like enrollment date), we can't use many to many. we must promote the junction table to have a full `@Entity` and use two one to one relationships
    Eg. : `@Entity
           public class Student {
               @Id @GeneratedValue
               private Long id;
               @ManyToMany
               @JoinTable(
               name = "enrollments",
               joinColumns = @JoinColumn(name = "student_id"),
               inverseJoinColumns = @JoinColumn(name = "course_id")
               )
               private Set<Course> courses = new HashSet<>();
           }`




When we connect these entities, we must manage the Persistence Context:

1. `FetchType.LAZY` vs `EAGER`: By default, @ManyToOne is Eager. This is a performance killer. Always set it to LAZY to avoid fetching the entire database in a single SELECT.

2. The `mappedBy` Attribute: This defines the "**Inverse**" side. If you forget it, Hibernate assumes both sides own the relationship and creates two separate sets of FKs or join tables.

3. `CascadeType`: ALL is dangerous. It means EntityManager.remove(parent) will wipe out all children. Be surgical: use PERSIST and MERGE mostly.

4. `orphanRemoval = true`: This is unique to JPA. If you remove a Book from the Publisher.books set, JPA will automatically issue a DELETE query for that book in the DB.