echo -------------------------
echo      Starting Athena
echo -------------------------
echo Ensure that your classes for your host connections are specified in the DRIVERLIBS environment variable
java -classpath .:$[prodlibs]:$[jinilibs]:$[serviceuiloc]:$[additionallibs]:${DRIVERLIBS} -server org.jini.projects.athena.ServiceApplication $[config]
