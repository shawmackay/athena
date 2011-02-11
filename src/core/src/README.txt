README for Athena 0.95community

Configuring Athena
-----------------------

Athena uses two configuration files, one is always used, the second is more 
instance specific. 
The configuration that is always used is 'conf/exportmgr.config' - this details 
all the exporters that will be built and tracked by the ExporterManager utility

There are 5 different exporter definitions that Athena uses:
	Service 		- 
		The proxy that is registered with the LUS
	Connection 	- 
		The proxy that defines a connection through Athena
	Participants 	- 
		Transaction Participant registration for the transaction Manager
	ResultSets		- 
		Proxies generated for remote access to ResultSet objects
	ThorListener - 
		Proxy used in exporting the listener to Thor (http://thor.jini.org) 
		for configuration changes
	
The second configuration file is for the particular instance that 
you are running and requires the following details to be specified

	athenaName	- 
		Name of the Instance which will also be shown as an attribute in a Lookup Browser
	groups			- 
		The group names that Athena will join and search for it's dependent services in
	codebase		- 
		The address from which Athena's downlaoded classes will be served from 
	policy			- 
		The name of the security policy file to run Athena under
	logLevel		- 
		The Text name of a java.util.logging.Level, representing the 
		amount of information Athena should output when running
		There are two instances, fileLogLevel and consoleLogLevel
	ui					- 
		The version of ui on the server you wish to use one of "clean", "light" or "none"
	thorName		- 
		The name of the thor instance the main configuration details are held in
	thorBranch	- 
		The Thor branch that holds the configuration information
	
Thor Configuration -
For a given instance, Athena requires part of it's configuration to be held in an instance of Thor.
For example, the following XML file describes a sample configuration for 
Oracle that can be imported onto a branch through Thor's serviceUI


<root>
	<props name="ORCL">
    	<item name="org.jini.projects.athena.connection.class" type="java.lang.String">org.jini.projects.athena.connects.oracle.OracleConnection</item>
	    <item name="org.jini.projects.athena.connect.password" type="java.lang.String">tiger</item>
	    <item name="org.jini.projects.athena.connect.type" type="java.lang.String">Oracle</item>
    	<item name="org.jini.projects.athena.connection.adhoctimeout" type="java.lang.String">50000</item>
	    <item name="org.jini.projects.athena.service.numconnect" type="java.lang.String">10</item>
    	<item name="org.jini.projects.athena.connect.url" type="java.lang.String">jdbc:oracle:thin:@myoracleserver.mycompany.com:1521:ORCL</item>
	    <item name="org.jini.projects.athena.connect.driver" type="java.lang.String">oracle.jdbc.driver.OracleDriver</item>
    	<item name="org.jini.projects.athena.connect.username" type="java.lang.String">scott</item>
	    <item name="org.jini.projects.athena.service.groups" type="java.lang.String">production</item>
    	<item name="org.jini.projects.athena.ui.clean" type="java.lang.String">true</item>
	    <item name="org.jini.projects.athena.service.name" type="java.lang.String">ORCL</item>
	</props>
</root>

Then place the name of the thor instance along with the name of the branch 
into your instance configuration file.
