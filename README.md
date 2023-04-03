![logo.png](assets/logo.png)

# Svydovets Bibernate

## Project description

---

Bibernate is an open-source Object-Relational Mapping (ORM) tool for Java applications that provides a framework for
mapping an object-oriented domain model to a relational database. It simplifies the database-related programming tasks,
such as CRUD (Create, Read, Update, Delete) operations, by providing a high-level, object-oriented abstraction layer
over SQL-based database interactions.

Overall, Bibernate is a powerful and popular tool for building Java-based applications that interact with relational
databases in a convenient and efficient manner.

## Get started

---

You can use the **[Bibernate demo project]()**.

Or follow these steps:

1. ```git clone https://github.com/svydovets-bobocode/bibernate```
2. ```cd <path_to_bibernate_svydovets>/bibernate-svydovets```
3. ```mvn clean install -DskipTests```
4. add as a dependency

```
<dependency>
   <groupId>com.bobocode.svydovets</groupId>
   <artifactId>bring-svydovets</artifactId>
   <version>1.0</version>
</dependency>
```

## How to start Bibernate

---

1. Create [datasource configuration file](#datasource-configuration).
2. Create [entities](#mapping).
3. Create [session factory](#session-factory)
4. Create [session](#session)

<details>
<summary>Start example</summary>

#### Datasource configuration file `src/main/resources/bibernate.properties` example:

```markdown
svydovets.bibernate.driverClassName=org.postgresql.Driver
svydovets.bibernate.db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
svydovets.bibernate.db.username=sa
svydovets.bibernate.db.password=
```

#### Mapping entity example:

```java
@Entity
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
   @Id private Integer id;
   private String name;

   @Column(insertable = false, updatable = false)
   private LocalDateTime creationTime;

   @Column(name = "phone_number")
   private String phone;
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
           session.begin();
           saveDefaultUserIntoDb();
           session.commit();
      } catch (Exception ex) {
           session.rollback();
      }
   }
}
```

</details>

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

### Datasource configuration

---

To build a [SessionFactory](#session-factory), first create an instance of [`BibernateConfiguration`](src/main/java/com/bobocode/svydovets/bibernate/config/BibernateConfiguration.java) and call the `configure()` method.

You can use the default configuration, which reads from a `src/main/resources/bibernate.properties` file, or provide a custom ConfigurationSource.

<details>
<summary>Configuration properties name description</summary>

```markdown
svydovets.bibernate.db.url - string. The JDBC connection url
svydovets.bibernate.db.username - string. The JDBC connection user name
svydovets.bibernate.db.password - string. The JDBC connection user password
svydovets.bibernate.driverClassName - String. The name of the JDBC Driver class to use
```

</details>

> File must be on the path: `src/main/resources/`
>
> #### Default Configuration
>
> <details>
>
> ```
> BibernateConfiguration configuration = new BibernateConfiguration();
> configuration.configure();
> SessionFactory sessionFactory = configuration.buildSessionFactory();
> ```
>
> Default configuration should pick up file with name `bibernate.properties` from resources folder
>
> <details>
> <summary>Configuration bibernate.properties example</summary>
>
> ```
> svydovets.bibernate.driverClassName=org.postgresql.Driver
> svydovets.bibernate.db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
> svydovets.bibernate.db.username=sa
> svydovets.bibernate.db.password=
> ```
>
> </details>
> </details>
>
> #### Property file
>
> <details>
>
> ```
> PropertyFileConfiguration propertyFileConfiguration = new PropertyFileConfiguration("custom.properties");
> BibernateConfiguration configuration = new BibernateConfiguration();
> configuration.configure(propertyFileConfiguration);
> SessionFactory sessionFactory = configuration.buildSessionFactory();
> ```
>
> Same as default, but with custom file name.
>
> <details>
> <summary>Configuration custom_file_name.properties example</summary>
>
> ```
> svydovets.bibernate.driverClassName=org.postgresql.Driver
> svydovets.bibernate.db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
> svydovets.bibernate.db.username=sa
> svydovets.bibernate.db.password=
> ```
>
> </details>
> </details>
>
> #### Xml configuration
>
> <details>
>
> ```
> XmlFileConfiguration xmlFileConfiguration = new XmlFileConfiguration("custom_file_name.xml");
> BibernateConfiguration configuration = new BibernateConfiguration();
> configuration.configure(xmlFileConfiguration);
> SessionFactory sessionFactory = configuration.buildSessionFactory();
> ```
>
> Get properties from xml.
>
> <details>
> <summary>Configuration custom_file_name.xml example</summary>
>
> ```
> <?xml version="1.0" encoding="UTF-8"?>
> <configuration>
>    <property name="svydovets.bibernate.driverClassName">org.postgresql.Driver</property>
>    <property name="svydovets.bibernate.db.url">jdbc:postgresql://localhost:5432/testdatabase</property>
>    <property name="svydovets.bibernate.db.username">testuser</property>
>    <property name="svydovets.bibernate.db.password">testpassword</property>
> </configuration>
> ```
>
> </details>
> </details>
>
> #### Java configuration
>
> <details>
>
> ```
> Map<String, String> propertiesMap = new HashMap<>();
> propertiesMap.put("property.key", "property.value");
> JavaConfiguration mapConfiguration = new JavaConfiguration(propertiesMap);
> BibernateConfiguration configuration = new BibernateConfiguration();
> configuration.configure(mapConfiguration);
> SessionFactory sessionFactory = configuration.buildSessionFactory();
> ```
>
> Get properties from Hash Map.
>
> <details>
> <summary>Configuration java properties example</summary>
>
> ```
> HashMap<String, String> properties = new HashMap<>();
> properties.put("svydovets.bibernate.driverClassName", POSTGRES_DRIVER_CLASS_NAME);
> properties.put("svydovets.bibernate.db.url", POSTGRES_DB_URL);
> properties.put("svydovets.bibernate.db.username", POSTGRES_DB_USERNAME);
> properties.put("svydovets.bibernate.db.password", POSTGRES_DB_PASSWORD);
> new JavaConfiguration(properties);
> ```
>
> </details>
> </details>
>
  ### Session factory

---

The Bibernate SessionFactory is a factory class that is responsible for creating sessions.

To build a [`SessionFactory`](src/main/java/com/bobocode/svydovets/bibernate/session/SessionFactory.java), first create
an instance of [BibernateConfiguration](#datasource-configuration) and call the `configure()` method.

The SessionFactory is typically instantiated only once during the application's startup process and is used to create sessions
throughout the lifetime of the application.

### Session

---

[`Session`](src/main/java/com/bobocode/svydovets/bibernate/session/Session.java) encapsulates the connection to the database
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
| `begin`                   | start transaction                                                                                                                                                  |
| `commit`                  | commit current transaction, writing any unflushed changes to the database                                                                                          |
| `rollback`                | roll back current transaction                                                                                                                                      |

### Mapping

---

> #### @Table
>
> <details>
>
> [`@Table`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Table.java)
> annotation is used to specify the name of the database table that a Java entity is mapped to.
>
> ```java
> @Table(value = "employees")
> public class Employee {
> }
> ```
>
>> If the `@Table` annotation is not used, Bibernate will use the default table name, which is the same as the lowercase entity class name.
>>
>> ```java
>> @Table
>> public class Employee {
>> }
>> ```
>>
>> </details>
>>
>  #### @Entity
>
> <details>
>
> [`@Entity`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Entity.java)
> annotation is used to indicate that a Java class is a Bibernate entity
>
> ```java
> @Entity
> public class Employee {
> }
> ```
>
> </details>
>
> #### @Id
>
> <details>
>
> [`@Id`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Id.java)
> annotation is used to mark a field or property of a Java class as the primary key of the corresponding database table.
>
> ```java
> @Id
> private Long id;
> ```
>
> </details>
>
> #### @Column
>
> <details>
>
> [`@Column`](src/main/java/com/bobocode/svydovets/bibernate/annotation/Column.java)
> annotation can be used to specify the name of the database column
>
> ```java
> @Column(value = "phone_number")
> private String phone;
> ```
>
>> If the `@Column` annotation is not used, Bibernate will use the default column name, which is the same as the entity field name.
>>
>> ```java
>> private String name;
>> ```
>>
>> </details>
>>
   ### Entity

---

To use a Java class as a Bibernate entity, the class must meet certain requirements:
- The class must be annotated with the [**@Entity**](#mapping) annotation.
- The class must have a **no-argument constructor that is either public or protected**.
- The class must have **at least one field that is marked with the [@Id](#mapping)** annotation
to serve as the primary key of the corresponding database table.

<details>

```
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

In Bibernate, entities can exist in different states depending on their lifecycle:
- **Transient**: An entity is in the transient state if it has just been instantiated and is not associated with a Bibernate Session.
In this state, the entity is not yet mapped to any database record.

- **Persistent state**: An entity is in the persistent state if it has been associated with a Bibernate Session.
  In this state, Bibernate tracks changes made to the entity and synchronizes them with the database when a transaction is committed.

- **Detached state**: An entity is in the detached state if it was previously associated with a Bibernate Session but is no longer in that state.
  This can happen when a Session is closed, or when an entity is explicitly detached from a Session.
  In this state, the entity is still mapped to a database record, but changes made to the entity are not automatically synchronized with the database.

- **Removed state**: An entity is in the removed state if it has been marked for deletion using the Session.delete() method.
  In this state, the entity is still associated with the Bibernate Session,
  but will be deleted from the database when the transaction is committed.

### Cache

---

> Bibernate only supports first level cache.

First level cache is also known as the session cache.
It is a cache that is created and managed by Bibernate within a session.

The first level cache stores objects that have been queried or saved by Bibernate,
allowing them to be retrieved quickly without having to make additional database calls.

> The cache is enabled by default and cannot be disabled.

It is limited to the scope of the session in which it was created.
This means that objects stored in the first level cache are not accessible outside of the session in which they were created.

### Transaction

---

[`Transaction`](src/main/java/com/bobocode/svydovets/bibernate/transaction/Transaction.java)
represents a unit of work that is performed on a database.

> The main goal of a transaction is to provide ACID characteristics to ensure the consistency and validity of your data.

A transaction in Bibernate can be managed using the [Session](#session) interface.

Transaction can be started using the `begin()` method and can be committed using the `commit()` method.
If an error occurs during the transaction, it can be rolled back using the `rollback()` method.

<details>

```
try {
    session.begin();
    saveDefaultPersonIntoDb();
    session.commit();
} catch (Exception ex) {
    session.rollback();
}
```

</details>

### Dirty checking

---

Bibernate Dirty checking is a mechanism that tracks changes made to entities and their associated persistent state during a session.

It identifies any changes made to an entity's state and propagates those changes to the database during a session flush,
reducing the amount of code required to manage persistence.

### Action Queue

---

The Bibernate Action Queue is a collection of pending database operations that are queued up to be executed as part of a transaction.
It ensures that database operations are executed in the correct order and that the database remains consistent.
