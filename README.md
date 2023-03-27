![logo.png](assets/logo.png)

# Svydovets Bibernate

## Project description

---

## Get started

---

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

## Features

### Building a SessionFactory
To build a SessionFactory, first create an instance of BibernateConfiguration and call the `configure()` method.
You can use the default configuration, which reads from a *.properties file, or provide a custom ConfigurationSource.

#### Default Configuration
```
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure();
SessionFactory sessionFactory = configuration.buildSessionFactory();
```
Default configuration should pick up file with name `bibernate.properties` from resources folder

#### Property file
```
PropertyFileConfiguration propertyFileConfiguration = new PropertyFileConfiguration("custom.properties");
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure(propertyFileConfiguration);
SessionFactory sessionFactory = configuration.buildSessionFactory();
```
Same as default, but with custom name.


#### Xml configuration
```
XmlFileConfiguration xmlFileConfiguration = new XmlFileConfiguration("custom.xml");
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure(xmlFileConfiguration);
SessionFactory sessionFactory = configuration.buildSessionFactory();
```
Get properties from xml.

#### Java configuration
```
Map<String, String> propertiesMap = new HashMap<>();
propertiesMap.put("property.key", "property.value");
JavaConfiguration mapConfiguration = new JavaConfiguration(propertiesMap);
BibernateConfiguration configuration = new BibernateConfiguration();
configuration.configure(mapConfiguration);
SessionFactory sessionFactory = configuration.buildSessionFactory();
```
Get properties from Hash Map.

