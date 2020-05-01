# LoginServer

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6e144fb897194ae294c3a083aae5fa3e)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Ashime/LoginServer&amp;utm_campaign=Badge_Grade)

## Requirements
- Java Development Kit 8
- Eclipse or IntelliJ IDEA Community
- Microsoft SQL Server (MSSQL) 2019 Express
- SQL Server Management Studio (SSMS)

## Getting Started
To successfully run the project, please download and install everything from the top to the bottom of the list. If you already have some of this downloaded or installed, please skip the current step and move on to the next step. There is installation guides to help install some of these applications.

### Download and Install
#### Java Development Kit 8
1) Download JDK 8 (<https://www.oracle.com/java/technologies/javase-jdk8-downloads.html>)
<br><b>NOTE:</b> Login required to download. Can sign up for free.

#### Eclipse
1) Download Eclipse (<https://www.eclipse.org/downloads/>)
<br><b>NOTE:</b> Only need Eclipse or IntelliJ IDEA Community, and not both.
2) Install Eclipse. Please follow this installation guide in the link below for help.
<br><b>LINK:</b> <https://www.eclipse.org/downloads/packages/installer>
 
 #### IntelliJ IDEA Community
 1) Download IntelliJ IDEA Community (<https://www.jetbrains.com/idea/download/#section=windows>)
 <br><b>NOTE:</b> Only need Eclipse or IntelliJ IDEA Community, and not both.
 2) Install IntelliJ IDEA. Please folow the Standalone section in this installation guide in the link below.
 <br><b>LINK:</b> <https://www.jetbrains.com/help/idea/installation-guide.html#>
 
 #### Microsoft SQL Server (MSSQL) 2019 Express
 1) Download Microsoft SQL Server 2019 Express (<https://go.microsoft.com/fwlink/?linkid=866658>)
 2) Install Microsoft SQL Server 2019 Express. Please follow the installation guide in the link below.
<br><b>LINK:</b> <https://www.sqlshack.com/sql-server-2019-overview-and-installation/>
 
 #### SQL Server Management Studio (SSMS)
1) Download SQL Server Management Studio (<https://aka.ms/ssmsfullsetup>)
2) Install SQL Server Management Studio. Please follow the installation guide starting at the section called "Download and install SSMS Release 18 Preview 4" near the bottom of the page in the link below. Only follow the installing instruction.
<br><b>LINK:</b> <https://www.sqlshack.com/sql-server-2019-overview-and-installation/>
 
 ### Setting up and Configuring
 #### Importing to Eclipse
1) Open Eclipse IDE.
2) Click File in the toolbar at the top.
3) Click Import...
4) In the pop-up, click the Git folder in the center section.
5) Click Projects from Git (with smart import).
6) Click Next at bottom of the current window.
7) Click Clone URI and click Next
8) Paste "<https://github.com/Ashime/LoginServer>" into URI section and click Next.
<br><b>NOTE:</b> The window will fill out some sections automatically.
9) Click Next
10) Ensure the directory path is where you want the project to store. Click Next.
11) Click Finish

#### Importing to IntelliJ IDEA Community
1) Open IntelliJ IDEA Community.
<br><b>NOTE:</b> If any existing projects are open, click File in the toolbar at the top, and click Close Project.
2) On the right hand side of the IntelliJ IDEA launcher click Get from Version Control.
<br><b>NOTE:</b> This window will automatically appear if all currently opened projects are closed.
3) On the left side of the window make sure Repository URL is selected.
4) In the center, copy and paste <https://github.com/Ashime/LoginServer> into the URL box.
5) Confirm where you want the files to clone to in the Directory box.
6) Click Clone at the bottom.

#### Configuring MSSQL 2019 Express
1) Open SSMS (SQL Server Management Studio).
2) Sign in through Windows Authentication.
3) Right click on Databases and click Restore Database...
<br><b>NOTE:</b> The database can be downloaded from the Files section below.
4) Click the Device option under the Source section.
5) Click the ... button under the Source section.
6) Click Add and browse for SUNOnline_v1006.bak file.
7) Click OK at the bottom of the window.
8) After the SUNOnline_v1006 database is restored, please follow the instructions on how to restore a login under the "Restore a login using a script" section in the link below. The script being restored is LoginServer.sql - check the Files section below.
<br><b>LINK:</b> <https://docs.microsoft.com/en-us/biztalk/core/how-to-back-up-and-restore-sql-server-logins>

## Features
- Unique IP Filter
<br>Prevent users from connecting multiple times on the same IP. This feature can be turned on/off in the Common.ini under the Security section.

- MAC Address Filter
 <br> Prevents banned users from connecting to the LoginServer. All connections and packets will be dropped. This feature cannot be turned off.
 
 - SHA512 w/ Salt and HMAC key
 <br>All passwords are encrypted in SHA512 with each user's individual salt and the server's customizable HMAC key. All user's password are stored encrypted in the database. The HMAC key can be changed in the LoginServer.ini under the Security section. The HMAC key is recommended to be 128 bytes long.
 
 - Trusted Devices
<br> Allows users add up to five (5) trusted devices. The user can only login the game with a trusted device. A user is allowed to remove a trusted device if they are logged into a trusted device. Users can add one (1) temporary device to their list. The temporary device can access their account until they log out.
 
 - Dynamic Packet System (95% complete)
 <br> The LoginServer responses to the client's requests by dynamically creating packets based on the user's information. Majority (95%) of the packets or packet information are not hard-coded.
 
 - Dynamic TEA (Tiny Encryption Algorithm) keys
 <br> The LoginServer creates a secure random key for TEA at the start of each user's active session. The keys are used to decrypt packet information from the client.

## Goals
- Dynamic Packet System (100%)
<br> Further understanding the server packet data to remove all hard coded packets or packet information. Please check the AnsServerSelect and AnsServerList classes to review hard coded packet information.

- Refactoring and Adding New Features
<br> Improving on the codes structure and implementing new features.

## Dependencies
### NettyIO
This 3rd party library improves performance and throughput by using NIO threads while doing server socketing and processing.
> <b>Download Link:</b> <https://netty.io/downloads.html><br>
> <b>Javadocs:</b> <https://netty.io/4.1/api/index.html>
    
### ini4j
This 3rd party library provides the ability to read and write with ini files.
> <b>Download Link:</b> <http://ini4j.sourceforge.net/download.html><br>
> <b>Javadocs:</b> <http://ini4j.sourceforge.net/apidocs/index.html>

## Documentation
### Packet Structure - General Info
> Google Drive (04/30/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=1QV0VcxBSgxViPs7iNgzVv2JCfXXGK6q31b68nk8Hq_I>

### Client Packets
> Google Drive (04/30/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=1TsGWK_btRPwFd9AKnjrKfYj90Bk0-yc0nVdO_W3A628>

### Server Packets
> Google Drive (04/30/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=1d1GMJrXX2EmDce9PQWMeo6Dg30QY0Hc1lZRb-BreVNk>

### Login Server - General Info
> Google Drive (04/30/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=19MXwaweLQj383rBZnmSIO9iw57KXVMpN_lZn3gpZ6Ks>

## Notes
### Client Login - Default
> Username - 123456
> <br>Password  - 123456

### Trusted Device - Default
> IP Address - 127.0.0.1

## Files
### SUNOnline_v1007 Database
> Google Drive (05/01/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=1xJrOjqoLCMNN0K2zrlFvO7JapMXcRcAI>

### LoginServer Script
> Google Drive (05/01/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=1-YAaW6exdia3W09itH06A9ps_7IRjgw0>

### CH SUN v1006 Client
> Google Drive (05/01/2020)
> <br><b>LINK:</b> <https://drive.google.com/open?id=1KQJVbfN8LPupaxk8H0-p4V6R4Bhy4I4W>
