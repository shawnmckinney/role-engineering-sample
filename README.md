# Overview of the role-engineering-sample README

 * This document demonstrates how to build and deploy the fortress role engineering sample.
 * The intent is to demonstrate standard RBAC role engineering practice.
 * The intent is not a how-to guide for fortress security in java web envs.  For that look to [apache-fortress-demo](https://github.com/shawnmckinney/apache-fortress-demo)
 * For more info about the role engineering process: [The Seven Steps of Role Engineering](https://iamfortress.net/2015/03/05/the-seven-steps-of-role-engineering/)
 * Role Engineering Sample App System Diagram
 ![Use Case](src/main/javadoc/doc-files/Role-Engineering-Block-Diagram-Master.png "System Diagram")

-------------------------------------------------------------------------------
## Prerequisites
1. Java 8
2. Apache Maven 3++
3. Apache Tomcat 7++
4. Completed either section in Apache Fortress Core Quickstart:
    * *SECTION 4. Apache Tomcat Setup* in [README-QUICKSTART-SLAPD.md](https://github.com/apache/directory-fortress-core/blob/master/README-QUICKSTART-SLAPD.md)
    * *SECTION 5. Apache Tomcat Setup* in [README-QUICKSTART-APACHEDS.md](https://github.com/apache/directory-fortress-core/blob/master/README-QUICKSTART-APACHEDS.md)

-------------------------------------------------------------------------------
## Prepare role-engineering-sample package

1. [Download ZIP](https://github.com/shawnmckinney/role-engineering-sample/archive/master.zip)

2. Extract the zip archive to your local machine.

3. cd role-engineering-sample-master

4. Rename [fortress.properties.example](src/main/resources/fortress.properties.example) to fortress.properties.

 Pick One:

 a. Prepare fortress for apacheds usage:

 ```properties
 # This param tells fortress what type of ldap server in use:
 ldap.server.type=apacheds

 # Use value from [Set Hostname Entry]:
 host=localhost

 # ApacheDS defaults to this:
 port=10389

 # These credentials are used for read/write access to all nodes under suffix:
 admin.user=uid=admin,ou=system
 admin.pw=secret

 # This is min/max settings for LDAP administrator pool connections that have read/write access to all nodes under suffix:
 min.admin.conn=1
 max.admin.conn=10

 # This node contains fortress properties stored on behalf of connecting LDAP clients:
 config.realm=DEFAULT
 config.root=ou=Config,dc=example,dc=com

 # Used by application security components:
 perms.cached=true

 # Fortress uses a cache:
 ehcache.config.file=ehcache.xml
 ```

 b. Prepare fortress for openldap usage:

 ```properties
 # This param tells fortress what type of ldap server in use:
 ldap.server.type=openldap

 # Use value from [Set Hostname Entry]:
 host=localhost

 # OpenLDAP defaults to this:
 port=389

 # These credentials are used for read/write access to all nodes under suffix:
 admin.user=cn=Manager,dc=example,dc=com
 admin.pw=secret

 # This is min/max settings for LDAP administrator pool connections that have read/write access to all nodes under suffix:
 min.admin.conn=1
 max.admin.conn=10

 # This node contains fortress properties stored on behalf of connecting LDAP clients:
 config.realm=DEFAULT
 config.root=ou=Config,dc=example,dc=com

 # Used by application security components:
 perms.cached=true

 # Fortress uses a cache:
 ehcache.config.file=ehcache.xml
 ```

-------------------------------------------------------------------------------
## Prepare Tomcat for Java EE Security

This sample web app uses Java EE security.

1. Download the fortress realm proxy jar into tomcat/lib folder:

  ```
  wget http://repo.maven.apache.org/maven2/org/apache/directory/fortress/fortress-realm-proxy/2.0.0/fortress-realm-proxy-2.0.0.jar -P $TOMCAT_HOME/lib
  ```

  where *TOMCAT_HOME* matches your target env.

2. Restart tomcat for new settings to take effect.

 Note: The proxy is a shim that uses a [URLClassLoader](http://docs.oracle.com/javase/7/docs/api/java/net/URLClassLoader.html) to reach its implementation libs.  It prevents
 the realm impl libs, pulled in as dependency to web app, from interfering with the container’s system classpath thus providing an error free deployment process free from
 classloader issues.  The proxy offers the flexibility for each web app to determine its own version/type of security realm to use, satisfying a variety of requirements
 related to web hosting and multitenancy.

-------------------------------------------------------------------------------
## Build and deploy role-engineering-sample

1. Set java and maven home env variables.

2. Run this command from the root package:

  Deploy to tomcat server:

  ```maven
 mvn clean tomcat:deploy -Dload.file
  ```

  Or if already deployed:

  ```maven
 mvn clean tomcat:redeploy -Dload.file
  ```

   -Dload.file tells maven to automatically load the role-engineering-sample security policy into ldap.  Since the load needs to happen just once, you may drop the arg from future ops:
  ```maven
 mvn tomcat:redeploy
  ```
 **Note**: if problem  with tomcat auto-deploy, manually deploy role-engineering-sample.war to webapps or change connection info used during tomcat:deploy in [pom.xml](pom.xml).
 ```
 <plugin>
     <groupId>org.codehaus.mojo</groupId>
     <artifactId>tomcat-maven-plugin</artifactId>
     <version>1.0-beta-1</version>
     <configuration>
     ...
         <url>http://localhost:8080/manager/text</url>
         <path>/${project.artifactId}</path>
         <!-- Warning the tomcat manager creds here are for deploying into a demo environment only. -->
         <username>tcmanager</username>
         <password>m@nager123</password>
     </configuration>
 </plugin>
 ```

-------------------------------------------------------------------------------
## Understand the security policy using RBAC

Security policy was derived from this:
![Use Case](src/main/javadoc/doc-files/Role-Engineering-Sample-Use-Case.png "Simple Security Use Case")

To gain full understanding, check out the file used to load it into the LDAP directory: ![role-engineering-sample security policy](src/main/resources/role-engineering-sample-security-policy.xml).

App comprised of three pages, each has buttons and links that are guarded by permissions.  The permissions are granted to a particular user via their role assignments.  But those
privileges are only realized after role activation.

For this app, user-to-role assignments are:
### User-to-Role Assignment Table

| user          | Role_Buyers   | Role_Sellers  |
| ------------- | ------------- | ------------- |
| johndoe       | true          | true          |
| ssmith        | true          | false         |
| rtaylor       | false         | true          |

Both roles inherit from a single parent:
### Role Inheritance Table

| role name     | parent name   |
| ------------- | ------------- |
| Role_Buyers   | Users         |
| Role_Sellers  | Users         |

The page-level authorization uses Spring Security's **FilterSecurityInterceptor** which maps the roles activated using what was received from the servlet container (via the Tomcat Realm).

User to Page access is granted as:
### User-to-Page Access Table

| user          | Home Page     | Buyers Page  | Sellers Page |
| ------------- | ------------- | ------------- | ------------- |
| johndoe       | true          | true          | true          |
| ssmith        | true          | true          | false         |
| rtaylor       | true          | false         | true          |

But a mutual exclusion constraint between the **role_buyers** and **role_sellers** restricts activation during runtime:
### Role-to-Role Dynamic Separation of Duty Constraint Table

| set name      | Set Members   | Cardinality   |
| ------------- | ------------- | ------------- |
| BuySel        | Role_Sellers  | 2             |
|               | Role_Buyers   |               |
|               |               |               |

Preventing any one user from being both at same time.

The buttons are guarded by rbac permission checks.  The permissions are dependent on which roles are active.

Below is the list of permissions by user.  These list can be returned using [sessionPermissions](https://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/AccessMgr.html#sessionPermissions(org.apache.directory.fortress.core.rbac.Session)) API.  When testing, keep in mind that DSD constraints further limit preventing access to all at the same time.

### User-to-Permission Access Table

| user          | account.create | item.search    | item.bid       | item.buy       | item.ship      | auction.create | BuyersPage.link  | SellersPage.link |
| ------------- | -------------- | -------------- | -------------- | -------------- | -------------- | -------------- | ---------------- | ---------------- |
| johndoe       | true           | true           | true           | true           | true           | true           | true             | true             |
| ssmith        | true           | true           | true           | true           | false          | false          | true             | false            |
| rtaylor       | true           | false          | false          | true           | true           | true           | false            | true             |

**DSD constraint between the Role_Buyers and Role_Sellers prevents johndoe from activating both simultaneously.**

In a standard RBAC setting the web app could provide a drop-down control where the user gets to choose active role.
This would allow user to choose between performing as a Buyer or Seller on a given session.

To see how this can be done, check out: [apache-fortress-demo](https://github.com/shawnmckinney/apache-fortress-demo)

-------------------------------------------------------------------------------
## Manually Test the role engineering sample

 1. Open link to [http://localhost:8080/role-engineering-sample](http://localhost:8080/role-engineering-sample)

 2. Login with Java EE authentication form:
 ![Login Page](src/main/javadoc/doc-files/Role-Engineering-Sample-Login-Page.png "Home Page - johndoe")

 3. User-Password Table

 | userId        | Password      |
 | ------------- | ------------- |
 | johndoe       | password      |
 | ssmith        | password      |
 | rtaylor       | password      |

 4. If everything is working, the Home page loads with some links and buttons to click on:
 ![Home Page](src/main/javadoc/doc-files/Role-Engineering-Sample-Home-Page.png "Home Page - johndoe")

 5. Try a different user.
  * Each has different access rights to application.
  * A DSD constraint prevents user **johndoe** from activating both buyer and seller role at same time.
  * All users have **account.create** and **item.search** through role inheritance with the base role: **Users**.

 6. Switch Roles
  * Roles that are assigned to **Super_Users**, **Role_Buyers** and **Role_Sellers** may switch between Buyer and Seller functions.
  * Test with user **johndoe** who has the necessary role assignments.
  * Johndoe cannot activated both roles in session at same time but can activate either role - using the switch button.
  * Switch from Buyer to Seller ![Home Page](src/main/javadoc/doc-files/Role-Engineering-Sample-Buyer-to-Seller.png "Switch from Buyer to Seller")
  * Switch from Seller to Buyer ![Home Page](src/main/javadoc/doc-files/Role-Engineering-Sample-Seller-to-Buyer.png "Switch from Seller to Buyer")
  * Without **Super_Users** role assigned will receive this error: ![Home Page](src/main/javadoc/doc-files/Role-Engineering-Sample-Unauthorized.png "Unauthorized User")

 7. Test with different assignments.
  * Use [fortress-web](https://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-fortress-web.html) or the
  [fortress-core command-line-interface](https://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/cli/package-summary.html)
  to setup new users with new role combinations.  How does the app respond with different policies?  What needs to change to make it better?

## Automatically Test the role engineering sample

Run the selenium automated test:

 ```
 mvn test -Dtest=RoleSampleSeleniumITCase
 ```

 Selenium Test Notes:
 * *This test will log in as each user, perform positive and negative test cases.*
 * *Requires Firefox on target machine.*
