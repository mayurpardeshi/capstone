## The SELECT in sql:
SELECT statement isn't just a "command"; 
it’s a Declarative Query that the **RDBMS Query Optimizer** transforms into an imperative execution plan


When we hit execute for any SELECT query, it goes through diff phases:

#### 1. Logical Processing Order
The true execution order is:
1. FROM/JOIN :Locate tables and create cartesian product
2. WHERE: filters rows based on predicate
3. Group By: Aggregates rows into buckets
4. Having: Filter those aggregated buckets
5. SELECT: finally pick the columns and calculate expressions
6. DISTINCT: removes duplicates
7. ORDER BY: Sorts the final result
8. Limit/Offset: Slice the result

#### 2. Query Lifecycle
A. Parser and Transformer: The engine checks the syntax against SQL grammar 
B. Query optimizer: The optimizer looks at the query tree and asks "Should I use the Index on user_id, or is it faster to do a Full Table Scan?". Should I perform a Hash Join or a Nested Loop Join?" It uses Statistics (histograms of data distribution) to estimate the "cost" of different paths.
C. Executor: The optimizer handles the physical plan to executor and executor interacts with `Storage Engine` to pull blocks of data


#### The result set as a Relation

For queries like `SELECT * FROM (SELECT user_id FROM Users) AS subquery;` - The inner SELECT produces a set, and the outer SELECT treats that set as its source.

If we want to see exactly how our SELECT is working under the hood, prepend query with **EXPLAIN**
`EXPLAIN ANALYZE SELECT name FROM Users WHERE email = 'nerd@example.com';`