# okex-bookmap-adapter
This is an adapter for using bookmap to connect and trade futures using OKEX exchange.

You will need to maven install bookmap bm-l1api.jar and javadoc for this project to compile.  Please take a look at lib/install.sh as an example.
You can get the latest bookmap bm-l1api.jar and javadoc from the bookmap installation that you have (i.e. C:\Program Files\Bookmap\lib\).  The easiest way to do is to run the command below inside the Bookmap\lib folder:

mvn install:install-file -Dfile=bm-l1api.jar -DgroupId=com.bookmap -DartifactId=bm-l1api -Dversion=7.0 -Dpackaging=jar

After installing the bookmap jar files.  You can generate the adapter jar file by running 'mvn package' on this project.  This will generate a jar file in the target folder. You will use the jar file with filename okex-bookmap-adapter-&lt;version&gt;.jar

OKEX does not have a test account so you need to have a live account in order to use this adapter.

Here's a quick guide on how to get started using this adapter:  https://1drv.ms/w/s!ArbBbZ723WK6gR9mZ-bCuLo6bn6k

Updates:

WARNING FOR TRADERS: this is an alpha version of the adapter. Although it is functional it is intended mostly for testing purposes.
Please use it at your own risk.
Before using the adapter for trading please make sure you can trade using OKEx web interface.

Please insert [apiKey]::[passPhrase] in the login field
Please insert [secretKey] in the password field.

For example, if your apiKey is fgRo9yT and your passPhrase is jhdfgd768df what you should enter for login is

fgRo9yT::jhdfgd768df

For example, if your secretKey is fjksdi6esdYhdhH what you should enter for password is

fjksdi6esdYhdhH
