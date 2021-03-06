## Broadleaf Commerce Spring Boot B2B Starter

This Maven project is meant to be used as a solid starter project for any [Broadleaf Commerce](http://www.broadleafcommerce.org) application.
It has many sensible defaults set up along with examples of how a fully functioning eCommerce site based on Broadleaf might work.

> Please Note: this project is based on several commercial modules and will not build and run without access to them.

## Running the applications
This starter contains several runnable applications: `site`, `admin`, and `api`.
You can run these as any other Spring Boot application (http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)
These applications automatically configure and start an HSQL DB and SOLR instance (if not already configured).
By default, these will be located in your `java.io.tmp` directory.

### Run using the Maven plugin (Recommended)
You can run any of the applications in an exploded form using the Spring Boot Maven Plugin:

##### Running Site:
```
   > cd site
   > mvn spring-boot:run
```
> Note: Default debug port open on `8083` 

##### Running Admin:
```
   > cd admin
   > mvn spring-boot:run
```
> Note: Default debug port open on `8084` 

##### Running API:
```
   > cd api
   > mvn spring-boot:run
```
> Note: Default debug port open on `8085` 

### Running as a packaged application
You can also run any of the applications as an executable jar:

##### Running Site:

```
   > cd site
   > java -Xmx1536M -Xdebug -Xrunjdwp:transport=dt_socket,address=8083,server=y,suspend=n -javaagent:target/agents/spring-instrument.jar -jar ./target/boot-starter-site-b2b-5.2.0-GA.jar
```

##### Running Admin:

```
   > cd admin
   > java -Xmx1536M -Xdebug -Xrunjdwp:transport=dt_socket,address=8084,server=y,suspend=n -javaagent:target/agents/spring-instrument.jar -jar ./target/boot-starter-admin-b2b-5.2.0-GA.jar
```

##### Running API:

```
   > cd api
   > java -Xmx1536M -Xdebug -Xrunjdwp:transport=dt_socket,address=8085,server=y,suspend=n -javaagent:target/agents/spring-instrument.jar -jar ./target/boot-starter-api-b2b-5.2.0-GA.jar
```

## License

The Broadleaf Commerce Demo Site is itself licensed under the Broadleaf Fair Use License Agreement - Version 1.0 (http://license.broadleafcommerce.org/fair_use_license-1.0.txt). The Broadleaf Commerce core and module libraries have different license models you should be aware of.

Broadleaf Commerce core libraries, as well as the menu library, are released under a dual license format. They may be used under the terms of the Fair Use License 1.0 (http://license.broadleafcommerce.org/fair_use_license-1.0.txt) unless the restrictions on use therein are violated and require payment to Broadleaf, in which case the Broadleaf End User License Agreement (EULA), Version 1.1 (http://license.broadleafcommerce.org/commercial_license-1.1.txt) shall apply. Alternatively, the Commercial License may be replaced with a mutually agreed upon license between you and Broadleaf Commerce.

Various commercial modules that are also available (for instance, price list management) are released under the applicable commercial license. These are not included with the core Broadleaf framework.