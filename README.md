# Overview of the role-engineering-sample README

 * This document demonstrates how to build and deploy the fortress role engineering sample.
 * It builds on this blog posting:
  * [The Seven Steps of Role Engineering](https://iamfortress.wordpress.com/2015/03/05/the-seven-steps-of-role-engineering/)
 * We use the Apache Wicket web framework for app.  To learn the details of combining Apache Wicket and Fortress, check out:
 [wicket-sample](https://github.com/shawnmckinney/wicket-sample)

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

## Test fortress security with spring saml sso enabled

 To get understanding of security policy, check out ![role-engineering-sample security policy](src/main/resources/RoleEngineeringSample.xml).

 excerpt from file:
 ```
 ...

 <adduserrole>
     <userrole userId="johndoe" name="Buyers"/>
     <userrole userId="johndoe" name="Sellers"/>
     <userrole userId="ssmith" name="Buyers"/>
     <userrole userId="rtaylor" name="Sellers"/>
 </adduserrole>


 <addpermgrant>
     <permgrant objName="SellersPage" opName="link" roleNm="Sellers"/>
     <permgrant objName="BuyersPage" opName="link" roleNm="Buyers"/>
     <permgrant objName="Item" opName="bid" roleNm="Buyers"/>
     <permgrant objName="Item" opName="buy" roleNm="Buyers"/>
     <permgrant objName="Item" opName="ship" roleNm="Sellers"/>
     <permgrant objName="Auction" opName="create" roleNm="Sellers"/>
     <permgrant objName="Item" opName="search" roleNm="Users"/>
     <permgrant objName="Account" opName="create" roleNm="Users"/>
 </addpermgrant>
 ...
 ```
 There are three pages, each page has three buttons.  Page access is granted as follows:

| user          | page1         | page2         | page3         |
| ------------- | ------------- | ------------- | ------------- |
| sam*          | true          | true          | true          |
| sam1          | true          | false         | true          |
| sam2          | false         | true          | false         |
| sam3          | false         | false         | true          |

 1. Open link to [http://localhost:8080/role-engineering-sample](http://localhost:8080/role-engineering-sample)

 2. Login to authentication form.
 ![IdP Login Page](src/main/javadoc/doc-files/SSO-Circle-Login.png "IdP Login Page")

 4. If everything works you load the Home page which has links to click on:
 ![sam*](src/main/javadoc/doc-files/Fortress-Saml-Demo-SuperUser.png "Home Page - sam*")

 5. Try a different user...
  * Map to different fortress users at [**MY Profile**](https://idp.ssocircle.com/sso/hos/SelfCare.jsp) page on ssocircle.com.
  * Enter a new **Surname**.  (Originally called **Last Name** when profile first created - both refer to same field)
  * Pick from one of these: sam1, sam2, sam3 or sam*.
  * Be sure to enter the original IdP password in **Old password** field before clicking on the **Submit** button to save your changes.
 ![User Profile Page](src/main/javadoc/doc-files/SSO-Circle-Change-Sam1-User.png "User Profile Page")
  * Delete the cookies from browser corresponding with the IdP and SP websites.
  * Now, go back to Step 1 and login again.  Will be different authorizations corresponding with other userIds mapped when redirected to **Launch Page**.
 ![sam1](src/main/javadoc/doc-files/Fortress-Saml-User1-Page.png "Home Page - sam1")

 6. Each fortress userId (mapped to **Last Name** field at IdP) has different access policy.
  * sam1 - access to page one
  * sam2 - access to page two
  * sam3 - access to page three
  * sam* - access to all pages