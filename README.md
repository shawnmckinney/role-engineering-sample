# Overview of the role-engineering-sample README

 * This document demonstrates how to build and deploy the fortress role engineering sample.
 * The intent is to demonstrate standard RBAC role engineering practice.
 * The intent is not a how-to guide for fortress security in java web envs.  For that look to [apache-fortress-demo](https://github.com/shawnmckinney/apache-fortress-demo)
 * For more info about the role engineering process: [The Seven Steps of Role Engineering](https://iamfortress.net/2015/03/05/the-seven-steps-of-role-engineering/)

-------------------------------------------------------------------------------
## fortress-saml-demo prerequisites
1. Java 7 (or greater) sdk
2. Git
3. Apache Maven 3
4. Completion of these steps under the [Apache Fortress Ten Minute Guide](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/ten-minute-guide.html):
    * [Setup Apache Directory Server](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-directory-server.html)
    * [Setup Apache Directory Studio](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-directory-studio.html)
    * [Build Apache Fortress Core](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-fortress-core.html)
    * [Build Apache Fortress Realm](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-fortress-realm.html)
    * [Setup Apache Tomcat Web Server](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-tomcat.html)
    * [Build Apache Fortress Web](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/apache-fortress-web.html)

-------------------------------------------------------------------------------
## Prepare role-engineering-sample package

1. [Download ZIP](https://github.com/shawnmckinney/role-engineering-sample/archive/master.zip)

2. Extract the zip archive to your local machine.

3. cd role-engineering-sample-master

4. Rename [fortress.properties.example](src/main/resources/fortress.properties.example) to fortress.properties.

 Prepare fortress for ldap server usage.

 After completing the fortress ten minute guide, this step should be familiar to you.  It is how the fortress runtime gets hooked in with a remote ldap server.
 ```properties
# Tells fortress what type of LDAP server in use:
ldap.server.type=apacheds

# ApacheDS LDAP host name:
host=localhost

# ApacheDS default port:
port=10389

# ApacheDS default:
admin.user=uid=admin,ou=system
admin.pw=secret

# This is min/max settings for LDAP connections:
min.admin.conn=1
max.admin.conn=10

# This node contains more fortress properties stored on LDAP server:
config.realm=DEFAULT
config.root=ou=Config,dc=example,dc=com

# Fortress uses ehcache:
ehcache.config.file=ehcache.xml

# Fortress web will cache perms in session:
perms.cached=true
 ```

-------------------------------------------------------------------------------
## Build and deploy fortress-saml-demo

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

To gain full understanding of the policy, check out the file used to load it into the LDAP directory: ![role-engineering-sample security policy](src/main/resources/role-engineering-sample-security-policy.xml).

There are three pages, each page has buttons and links that are guarded by permissions.  The permissions are granted to a particular user via a role assignment.

For this app, user-to-role access is granted as follows:
### User-to-Role Assignment Table
| user          | Role_Buyers   | Role_Sellers  |
| ------------- | ------------- | ------------- |
| johndoe       | true          | true          |
| ssmith        | true          | false         |
| rtaylor       | false         | true          |

Both roles inherit from their parent role:
### Role Inheritance Table
| role name     | parent name   |
| ------------- | ------------- |
| Role_Buyers   | Users         |
| Role_Sellers  | Users         |

The pages are guarded with spring's **FilterSecurityInterceptor** which maps to the roles activated into the user's session by the container.

User to Page access is granted as follows:
### User-to-Page Access Table
| user          | Home Page     | Buyer's Page  | Seller's Page |
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

These buttons are guarded by permission checks.  The permissions are dependent on which roles are active.

Below is the list of permissions per user.  When testing, keep in mind that DSD constraints will further limit preventing access to all at the same time.

### User-to-Permission Access Table
| user          | account.create | item.search    | item.bid       | item.buy       | item.ship      | auction.create | BuyersPage.link  | SellersPage.link |
| ------------- | -------------- | -------------- | -------------- | -------------- | -------------- | -------------- | ---------------- | ---------------- |
| johndoe       | true           | true           | true           | true           | true           | true           | true             | true             |
| ssmith        | true           | true           | true           | true           | false          | false          | true             | false            |
| rtaylor       | true           | false          | false          | true           | true           | true           | false            | true             |

**DSD constraint between the Role_Buyers and Role_Sellers prevents johndoe from activating both simultaneously.**

In a standard RBAC setting the web app could provide a drop-down control where the user gets to choose active role.
This would allow user to choose between performing as a Buyer or Seller on a given session.

To see learn how this can be done, check out: [apache-fortress-demo](https://github.com/shawnmckinney/apache-fortress-demo)

## Test the role engineering sample

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
  * A DSD constraint prevents user **johndoe** from acquiring both buyer and seller role at same time.
  * All users have **account.create** and **item.search** through role inheritance with the base role: **Users**.