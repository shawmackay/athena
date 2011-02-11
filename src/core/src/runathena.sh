STUBHOST=http://193.32.103.55/jinistubs
#LOCALDIR=/home/calum/development/athena/src
echo -------------------------
echo      Starting Athena
echo -------------------------
java -classpath .classes12.zip:/usr/local/jlibs/eros-itf.jar:/usr/local/jlibs/thor-itf.jar:$CLASSPATH -Dathena.set=$1 -Djava.security.policy=/home/calum/policy.all -Djava.rmi.server.codebase=$STUBHOST/athena-dl.jar athena.ServiceApplication

