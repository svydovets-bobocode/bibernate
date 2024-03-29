![logo.png](assets/logo.png)

# Svydovets Bibernate

Requirements for the project can be found [here](https://docs.google.com/document/d/1_DSY6ABkkDlmEPQPoheQzob_dR_k--pd-TzVhtH9_xY/edit).

## Project description

---

Bibernate is an open-source Object-Relational Mapping (ORM) tool for Java applications that provides a framework for
mapping an object-oriented domain model to a relational database. It simplifies the database-related programming tasks,
such as CRUD (Create, Read, Update, Delete) operations, by providing a high-level, object-oriented abstraction layer
over SQL-based database interactions.

Overall, Bibernate is a powerful and popular tool for building Java-based applications that interact with relational
databases in a convenient and efficient manner.

For demonstration you can use the **[Bibernate demo project](https://github.com/svydovets-bobocode/bibernate-demo-project)**.

## Get started

---

Or follow these steps:

1. ```git clone https://github.com/svydovets-bobocode/bibernate```
2. ```cd <path_to_bibernate_svydovets>/bibernate-svydovets```
3. ```mvn clean install -DskipTests```
4. add as a dependency

```xml
<dependency>
   <groupId>com.bobocode.svydovets</groupId>
   <artifactId>bibernate-svydovets</artifactId>
   <version>1.0</version>
</dependency>
```

5. add database dependency

```xml
<dependency>
   <groupId>org.postgresql</groupId>
   <artifactId>postgresql</artifactId>
   <version>42.5.4</version>
</dependency>
```

## Project packages structure

Below is the package structure for the Bibernate ORM:

```
com.bobocode.svydovets.bibernate
├── action            # provides an API for DB actions creation and execution
│   ├── executor
│   ├── key
│   ├── mapper
│   └── query
├── annotation        # core ORM annotations
├── config            # API for Bibernate configuration
├── connectionpool    # Connection pooling API
├── exception         # Bibernate exceptions
├── lazy              # lazy collections
├── locking           # Locking API
│   └── optimistic
├── session           # Session API
│   └── service
│       └── model
├── state             # Entity states managing API
├── transaction       # Transaction control management API
├── util              # Util classes
└── validation        # Validation on the entity mapping and entity states
    ├── annotation
    │   └── required
    │       └── processor
    └── state
```

## How to start Bibernate

---

1. Create [datasource configuration file](#datasource-configuration).
2. Create [entities](#mapping).
3. Create [session factory](#session-factory)
4. Create [session](#session)

#### Datasource configuration file `src/main/resources/bibernate.properties` example:

```properties
svydovets.bibernate.driverClassName=org.postgresql.Driver
svydovets.bibernate.db.url=jdbc:postgresql://localhost:5432/postgres
svydovets.bibernate.db.username=postgres
svydovets.bibernate.db.password=password
```

#### Mapping entity example:

```java

import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;

@Entity
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @Column(name = "phone_number")
  private String phone;

  @Column(updatable = false)
  private LocalDateTime creationTime;
}
```

#### SessionFactory and open session example:

```java
public class StartExample {
    public static void main(String[] args) {
        BibernateConfiguration configuration = new BibernateConfiguration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(new User("John", "937992", LocalDateTime.now()));
            session.commitTransaction();
        } catch (Exception ex) {
            session.rollbackTransaction();
        } finally {
            session.close();
        }
    }
}
```

## Features

---

- **[Datasource configuration](#datasource-configuration)**
- **[Session factory](#session-factory)**
- **[Session](#session)**
- **[Mapping](#mapping)**
- **[Entity](#entity)**
- **[Cache](#cache)**
- **[Transaction](#transaction)**
- **[Dirty checking](#dirty-checking)**
- **[Action Queue](#action-queue)**
- **[Optimistic locking](#optimistic-locking)**

### Datasource configuration

---

To build a [SessionFactory](#session-factory), first create an instance
of [`BibernateConfiguration`](src/main/java/com/bobocode/svydovets/bibernate/config/BibernateConfiguration.java) and
call the `configure()` method.

You can use the default configuration, which reads from a `src/main/resources/bibernate.properties` file, or provide a
custom ConfigurationSource.

<details>
<summary>Configuration properties name description</summary>

```properties
svydovets.bibernate.db.url - string. The JDBC connection url
svydovets.bibernate.db.username - string. The JDBC connection user name
svydovets.bibernate.db.password - string. The JDBC connection user password
svydovets.bibernate.driverClassName - String. The name of the JDBC Driver class to use
```

</details>

 <details>
 <summary>Default Configuration</summary>

```java
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure();
SessionFactory sessionFactory = configuration.buildSessionFactory();
```

Default configuration should pick up file with name `bibernate.properties` from resources folder

> File must be on the path: `src/main/resources/`

 <details>
 <summary>Configuration bibernate.properties example</summary>

```properties
svydovets.bibernate.driverClassName=org.postgresql.Driver
svydovets.bibernate.db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
svydovets.bibernate.db.username=sa
svydovets.bibernate.db.password=
```

 </details>
 </details>

 <details>
 <summary>Properties configuration</summary>

```java
PropertyFileConfiguration propertyFileConfiguration = new PropertyFileConfiguration("custom.properties");
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure(propertyFileConfiguration);
SessionFactory sessionFactory = configuration.buildSessionFactory();
```

Same as default, but with custom file name.

> File must be on the path: `src/main/resources/`

 <details>
 <summary>Configuration custom_file_name.properties example</summary>

```properties
svydovets.bibernate.driverClassName=org.postgresql.Driver
svydovets.bibernate.db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
svydovets.bibernate.db.username=sa
svydovets.bibernate.db.password=
```

 </details>
 </details>

 <details>
 <summary>Xml configuration</summary>

```java
XmlFileConfiguration xmlFileConfiguration = new XmlFileConfiguration("custom_file_name.xml");
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure(xmlFileConfiguration);
SessionFactory sessionFactory = configuration.buildSessionFactory();
```

Get properties from xml.

> File must be on the path: `src/main/resources/`

 <details>
 <summary>Configuration custom_file_name.xml example</summary>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
   <property name="svydovets.bibernate.driverClassName">org.postgresql.Driver</property>
   <property name="svydovets.bibernate.db.url">jdbc:postgresql://localhost:5432/testdatabase</property>
   <property name="svydovets.bibernate.db.username">testuser</property>
   <property name="svydovets.bibernate.db.password">testpassword</property>
</configuration>
```

 </details>
 </details>

 <details>
 <summary>Java configuration</summary>

```java
Map<String, String> propertiesMap = new HashMap<>();
propertiesMap.put("property.key", "property.value");
JavaConfiguration mapConfiguration = new JavaConfiguration(propertiesMap);
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure(mapConfiguration);
SessionFactory sessionFactory = configuration.buildSessionFactory();
```

Get properties from Hash Map.

 <details>
 <summary>Configuration java properties example</summary>

```java
HashMap<String, String> properties = new HashMap<>();
properties.put("svydovets.bibernate.driverClassName", POSTGRES_DRIVER_CLASS_NAME);
properties.put("svydovets.bibernate.db.url", POSTGRES_DB_URL);
properties.put("svydovets.bibernate.db.username", POSTGRES_DB_USERNAME);
properties.put("svydovets.bibernate.db.password", POSTGRES_DB_PASSWORD);
new JavaConfiguration(properties);
```

 </details>
 </details>

### Session factory

---

The Bibernate SessionFactory is a factory class that is responsible for creating sessions.

To build a [`SessionFactory`](src/main/java/com/bobocode/svydovets/bibernate/session/SessionFactory.java), first create
an instance of [BibernateConfiguration](#datasource-configuration) and call the `configure()` method.

The SessionFactory is typically instantiated only once during the application's startup process and is used to create
sessions
throughout the lifetime of the application.

### Session

---

[`Session`](src/main/java/com/bobocode/svydovets/bibernate/session/Session.java) encapsulates the connection to the
database
and makes it possible to interact with the database.

It provides a way to create, read, update, and delete persistent objects and maintains a first-level cache of persistent
objects to avoid repeated database access.

> The Session is created from a SessionFactory and **should** be closed when the conversation is over.

[`Session`](src/main/java/com/bobocode/svydovets/bibernate/session/Session.java) methods definition:

|          Method           |                                                                            Description                                                                             |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `find(class, primaryKey)` | find an entity by primary key<br/>the returned entity will be contained in a persistent entity                                                                     |
| `save(entity)`            | save an entity into the database <br/>the entity state is changed from transient to persistent                                                                     |
| `delete(entity)`          | remove an entity from the database <br/>the entity state is changed from persistent to removed                                                                     |
| `merge(entity)`           | merge state of the given entity with the current state of a managed entity in the persistence context.<br/>the entity state is changed from detached to persistent |
| `detach(entity)`          | remove entity from the persistence context                                                                                                                         |
| `getEntityState(entity)`  | return entity state from the persistence context                                                                                                                   |
| `close`                   | close and flush current session                                                                                                                                    |
| `beginTransaction`        | start transaction                                                                                                                                                  |
| `commitTransaction`       | commit current transaction, writing any unflushed changes to the database                                                                                          |
| `rollbackTransaction`     | roll back current transaction                                                                                                                                      |

### Mapping

---

 <details>
<summary>@Table</summary>

[`@Table`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Table.java)
annotation is used to specify the name of the database table that a Java entity is mapped to.

```java

@Table(value = "employees")
public class Employee {
}
```

> If the `@Table` annotation is not used, Bibernate will use the default table name, which is the same as the lowercase
> entity class name.
>
> ```java
> @Table
> public class Employee {
> }
> ```

</details>

 <details>
<summary>@Entity</summary>

[`@Entity`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Entity.java)
annotation is used to indicate that a Java class is a Bibernate entity

```java

@Entity
public class Employee {
}
```

 </details>

 <details>
<summary>@Id</summary>

[`@Id`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Id.java)
annotation is used to mark a field or property of a Java class as the primary key of the corresponding database table.

```java
@Id
private Long id;
```

 </details>

 <details>
<summary>@GeneratedValue</summary>

[`@GeneratedValue`](src/main/java/com/bobocode/svydovets/bibernate/annotation/GeneratedValue.java)
annotation is used to providing specification of generation strategies for the values of primary keys

```java
import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;

@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, 
                sequenceName = "custom_seq", 
                allocationSize = 50)
private Long id;
```

For details see **[Id generation strategies](#id-generation-strategies)**

 </details>

 <details>
<summary>@Column</summary>

[`@Column`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Column.java)
annotation can be used to specify the name of the database column

```java
@Column(value = "phone_number")
private String phone;
```

> If the `@Column` annotation is not used, Bibernate will use the default column name, which is the same as the entity
> field name.
>
> ```java
> private String name;
> ```

 </details>

<details>
<summary>@Version</summary>

[`@Version`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Version.java)
annotation is used to indicate that the field will be used for optimistic locking

```java
@Version
private long version; 
```

 </details>

<details>
<summary>@ManyToOne</summary>

[`@ManyToOne`](src/main/java/com/bobocode/svydovets/bibernate/annotation/ManyToOne.java)
Used for Many - to - One DB relation (child entity has one parent entity, when parent entity can
have multiple children).
<br>
When you retrieve from the DB the child entity, the parent entity will be eagerly loaded and
set too. There is no need to perform the explicit loading of the parent entity. Currently, you
cannot configure it if you want to load the parent lazily.

```java
import com.bobocode.svydovets.bibernate.annotation.JoinColumn;
import com.bobocode.svydovets.bibernate.annotation.ManyToOne;

@Entity
@Table(name = "employees")
public class Employee {

  public Employee() {
  }

  @Id
  private Long id;

  @ManyToOne
  private List<User> user;
  
}
```

 </details>

<details>
<summary>@JoinColumn</summary>

[`@JoinColumn`](src/main/java/com/bobocode/svydovets/bibernate/annotation/JoinColumn.java)
Used among with the **@OneToMany** mapping in order to provide the ORM with the information
about the foreign key relation column name.

 </details>

<details>
<summary>@OneToMany</summary>

[`@OneToMany`](src/main/java/com/bobocode/svydovets/bibernate/annotation/OneToMany.java)
Used for One-to-Many DB relation (when parent entity have multiple relations with the child
entities, child entity stores the reference to the parent via the foreign key column).
<br>
This type of the relation does not immediately load from the DB. Also, your provided
collection will not be used at all. Instead, the Bibernate ORM will create the instance of the
**SvydovetsLazyList**. It is the implementation of the **java.util.List** interface, but
it acts as a lazy collection. The entities will be loaded only at the first time that you access
it.

```java
import com.bobocode.svydovets.bibernate.annotation.JoinColumn;
import com.bobocode.svydovets.bibernate.annotation.OneToMany;

@Entity
@Table(name = "employees")
public class Employee {

  public Employee() {
  }

  @Id
  private Long id;

  @OneToMany
  @JoinColumn(name = "note_id")
  private List<Note> notes;

}
```

 </details>

### Entity

---

To use a Java class as a Bibernate entity, the class must meet certain requirements:

- The class must be annotated with the [**@Entity**](#mapping) annotation.
- The class must have a **no-argument constructor that is either public or protected**.
- The class must have **at least one field that is marked with the [@Id](#mapping)** annotation
  to serve as the primary key of the corresponding database table.

<details>

```java
// Entity is required to be marked with @Entity
@Entity
@Table(name = "employees")
public class Employee {

    // Entity is required to have public non-arg constructor
    public Employee() {
    }
    
    // Entity is required to have @Id field
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    // getters and setters
}
```

</details>

In Bibernate, entities can exist in different [`states`](src/main/java/com/bobocode/svydovets/bibernate/state/EntityStateService.java) depending on their lifecycle:

- **Transient**: An entity is in the transient state if it has just been instantiated and is not associated with a
  Bibernate Session.
  In this state, the entity is not yet mapped to any database record.

- **Persistent**: An entity is in the persistent state if it has been associated with a Bibernate Session.
  In this state, Bibernate tracks changes made to the entity and synchronizes them with the database when a transaction
  is committed.

- **Detached**: An entity is in the detached state if it was previously associated with a Bibernate Session but is
  no longer in that state.
  This can happen when a Session is closed, or when an entity is explicitly detached from a Session.
  In this state, the entity is still mapped to a database record, but changes made to the entity are not automatically
  synchronized with the database.

- **Removed**: An entity is in the removed state if it has been marked for deletion using the Session.delete()
  method.
  In this state, the entity is still associated with the Bibernate Session,
  but will be deleted from the database when the transaction is committed.

### Id generation strategies

___

Bibernate provides 3 types of Id management strategies

- **MANUAL**: Default strategy. Applies also if the annotation won't be provided.
  The whole id management process is the user responsibility. User have to set Id to each entity manually before
  saving. Bibernate won't let to save an entity with empty Id.

<details>
<summary>Example</summary>

```java
import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;

@Id
@GeneratedValue(strategy = GenerationType.MANUAL)
private Long id;
```

</details>

- **IDENTITY**: Required to have any of autogenerate types of the id column in database (e.g. serial or bigserial for Posgtresql).
  The Id will be getting from database for each entity before saving.

<details>
<summary>Example</summary>

```java
import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

</details>

- **SEQUENCE**: Required to create a sequence in the database. Default name will be `{columnName}_seq`.
  Will select the sequence for the first time and based on the `increment by` value will take the ids from cache for the range.

<details>

```java
import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;

@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, 
                sequenceName = "custom_seq", 
                allocationSize = 50)
private Long id;
```

<summary>Example and details</summary>

`allocationSize`: should be align with `increment by` sequence value

`sequenceName`: could be specify any custom sequence name instead of default

</details>

### Cache

---

> Bibernate only supports first level cache.

First level cache is also known as the session cache.
It is a cache that is created and managed by Bibernate within a session.

The first level cache stores objects that have been queried or saved by Bibernate,
allowing them to be retrieved quickly without having to make additional database calls.

> The cache is enabled by default and cannot be disabled.

It is limited to the scope of the session in which it was created.
This means that objects stored in the first level cache are not accessible outside of the session in which they were
created.

### Transaction

---

[`Transaction`](src/main/java/com/bobocode/svydovets/bibernate/transaction/Transaction.java)
represents a unit of work that is performed on a database.

> The main goal of a transaction is to provide ACID characteristics to ensure the consistency and validity of your data.

A transaction in Bibernate can be managed using the [Session](#session) interface.

Transaction can be started using the `beginTransaction()` method and can be committed using the `commitTransaction()` method.
If an error occurs during the transaction, it can be rolled back using the `rollbackTransaction()` method.

<details>
<summary>Example</summary>

```java
try {
    session.beginTransaction();
    saveDefaultPersonIntoDb();
    session.commitTransaction();
} catch (Exception ex) {
    session.rollbackTransaction();
}
```

</details>

### Dirty checking

---

Bibernate Dirty checking is a mechanism that tracks changes made to entities and their associated persistent state
during a session.

It identifies any changes made to an entity's state and propagates those changes to the database during a session flush,
reducing the amount of code required to manage persistence.

### Action Queue

---

The Bibernate Action Queue is responsible for managing and executing the following types of actions:

- `INSERT`: Represents the insertion of a new entity into the database.
- `UPDATE`: Represents the update of an existing entity in the database.
- `DELETE`: Represents the deletion of an existing entity from the database.

When an action is added to the queue, it is not immediately executed.
Instead, the Action Queue collects all actions that need to be executed within a transaction, and then executes them in the correct order when the transaction is committed.

To ensure that the actions are executed in the correct order, the Action Queue follows these rules:

`INSERT` actions are executed before `UPDATE` actions.
`UPDATE` actions should be skipped if follow with `DELETE` actions.
Actions are executed in the order they were added to the queue.
This ordering guarantees that all insertions are completed before any updates or deletions are performed, preventing any inconsistencies in the database.

Here is an example of using the Action Queue within a transaction:

```java
try {
  session.beginTransaction(); // Start the transaction
  
  // Perform database operations (these actions will be added to the Action Queue)
  
  Person personsFromDb = session.find(Person.class,1L);
  
  personsFromDb.setFirstName("Jane");
  
  session.delete(personsFromDb);
  
  session.commitTransaction(); // Commit the transaction (this will execute the actions in the Action Queue)
  } catch (Exception ex) {
  session.rollbackTransaction(); // Rollback the transaction in case of any errors
}
```

In this example, when the transaction is committed, the actions in the Action Queue are executed in the following order: UPDATE, DELETE.
This ensures that the employee is first retrieved from db, it will be deleted from the database, skipping update.

### Optimistic locking

---

The optimistic locking mechanism can be applied by using [**@Version**](#mapping) annotation.
Supported data types for fields annotated by @Version: Short, Integer, Long, short, int, long. The initial value for the field annotated after the insert operation is 0.
Optimistic lock works for update and as well delete operations. When the entity is subsequently updated or deleted, Bibernate checks whether
the version number in the database matches the version number of the entity being updated/deleted. If the version numbers match, the update/delete
is allowed to proceed. If the version numbers do not match, it means that the entity has been updated by another transaction, and the update/delete
is rejected.

<details>

```java
@Entity
public class Product {
    @Id
    private Long id;

    private String name;
    
    @Version
    private long version;
}
```

</details>

