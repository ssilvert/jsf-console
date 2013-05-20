jsf-console
===========

JSF-based JBoss Admin Console used as a proof-of-concept to show dynamic plugins, auto-generated UI, fine-grained autorization, and i18n.

To run the JSF Console, deploy the WAR created in the "myconsole" project.  The WAR that "core" produces is also deployable.

To log in, you will need to create users with the WildFly add-user tool.  There are four hard-coded roles.  They are admin, user, superuser, and super-duper-user.  If you log in as admin you can grant rights to the other roles concerning what plugins they can see and also what resources and attributes they can see in management-model. The rights are only persisted in memory right now, so they go away if you redploy the console.

***
>Here is a sample run of add-user that creates an admin user for the JSF Console.  Note that you are creating an Application User in ApplicationRealm:

C:\as7trunk\jboss-as\build\target\jboss-as-8.0.0.Alpha1-SNAPSHOT\bin>__add-user__

What type of user do you wish to add?  
 a) Management User (mgmt-users.properties)   
 b) Application User (application-users.properties)  
(a): __b__  

Enter the details of the new user to add.  
Realm (ApplicationRealm) :  
Username : __myadminuser__  

Password :  
Re-enter Password :  
What roles do you want this user to belong to? (Please enter a commaseparated list, or leave blank for none)[  ]: __admin__  
About to add user 'myadminuser' for realm 'ApplicationRealm'  
Is this correct yes/no? __yes__  
Added user 'myadminuser' to file  
'C:\as7trunk\jboss-as\build\target\jboss-as-8.0.0.Alpha1-SNAPSHOT\standalone\configuration\application-users.properties'  
Added user 'myadminuser' to file
'C:\as7trunk\jboss-as\build\target\jboss-as-8.0.0.Alpha1-SNAPSHOT\domain\configuration\application-users.properties'  
Added user 'myadminuser' with roles admin to file
'C:\as7trunk\jboss-as\build\target\jboss-as-8.0.0.Alpha1-SNAPSHOT\standalone\configuration\application-roles.properties'  

Added user 'myadminuser' with roles admin to file
'C:\as7trunk\jboss-as\build\target\jboss-as-8.0.0.Alpha1-SNAPSHOT\domain\configuration\application-roles.properties'  
Is this new user going to be used for one AS process to connect to
another AS process?  
e.g. for a slave host controller connecting to the master or for a
Remoting connection for server to server EJB calls.  
yes/no? __yes__  
To represent the user add the following to the server-identities
definition <secret value="ITFwYXNzd29yZA==" />  
Press any key to continue . . .  