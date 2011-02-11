/*
 * Created by IntelliJ IDEA. User: calum Date: 09-Jul-2002 Time: 14:11:49 To
 * change template for new class use Code Style | Class Templates options (Tools |
 * IDE Options).
 */

package org.jini.projects.athena.harness;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.entry.Name;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.connection.AthenaConnection;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.service.AthenaRegistration;
import org.jini.projects.athena.xml.HarnessLoader;
import org.xml.sax.SAXException;

import com.sun.jini.constants.TimeConstants;
/**
 * Client program that parses information from an XML file into a set of commands and executes it.<br/><br/>
 * Usage:<br/>
 * <pre>
 *  java [jvm-options] org.jini.projects.athena.harness.Client [xmlfile] [ABORT|COMMIT] [L|D|R] [ATHENANAME] [JINIGROUP] [number of runs (default 1)] [keepconnection (Y|N) (default N)]
 * </pre>
 * Explanation of options:<br/>
<table border="1">
<tr><td width=100><b><center>xmlfile</center></b></td><td>Follows the format: <br/><br/><pre>&lt;harness:commands
    xmlns:harness=&quot;http://harness.athena.projects.jini.org&quot;&gt;
    &lt;command callname=&quot;athenatest&quot;&gt;
        &lt;param name=&quot;code&quot; value=&quot;ABCDE&quot;/&gt;
        &lt;param name=&quot;description&quot; value=&quot;Generic Widget&quot;/&gt;
        &lt;param name=&quot;quantity&quot; value=&quot;1&quot;/&gt;
    &lt;/command&gt;
    &lt;command callname=&quot;athenatest&quot;&gt;
        &lt;param name=&quot;code&quot; value=&quot;ABCDE&quot;/&gt;
        &lt;param name=&quot;description&quot; value=&quot;Generic Widget&quot;/&gt;
        &lt;param name=&quot;quantity&quot; value=&quot;2&quot;/&gt;
    &lt;/command&gt;
    &lt;command callname=&quot;athenatest&quot;&gt;
        &lt;param name=&quot;code&quot; value=&quot;ABCDE&quot;/&gt;
        &lt;param name=&quot;description&quot; value=&quot;Generic Widget&quot;/&gt;
        &lt;param name=&quot;quantity&quot; value=&quot;3&quot;/&gt;
    &lt;/command&gt;
&lt;/harness:commands&gt;
</pre></td></tr>
<tr><td><b><center>Transaction<br/>Completion</center></b></td><td>Whether you wish to either COMMIT or ABORT all of this command set</td></tr>
<tr><td><b><center>Resultset<br/>Type</center></b></td><td>Whether you wish to use Local, Disconnected or Remote ResultSets</td></tr>
<tr><td><b><center>AthenaName <br/>&<br/>Jini Group</center></b></td><td>Which group and name the client will use to find the Athena instance you require</td></tr>
<tr><td><b><center>Number <br/>of Runs</center></b></td><td>How many loops do you want the client to perform(all operations in a single XML file on a single loop are executed under a single transaction)</td></tr>
<tr><td><b><center>Keep Connection</center></b></td><td>Y/N - Keep the same connection between runs, or return it and get a new connection on each run</td></tr>
</table><br/>

Commands run through this harness should have parameters that are all strings. Support to define other object types will be added in a future version.
 * @author calum
 *
 */
 
 
 
public class Client implements DiscoveryListener {
	LookupDiscoveryManager ldm;
	boolean discovered = false;
	static String ATHENANAME = "VHAMCIX";
	protected TransactionManager txmgr = null;
	AthenaRegistration athReg = null;
	LeaseRenewalManager lrm;
	Lease athenaLease;
	static boolean abortflag = false;
	static boolean disconnected = true;
	static boolean remote = false;
	static String XMLFILENAME = null;
	static String GROUP;
	static int NUMRUNS = 1;
	static boolean keepconnection = false;
	int numaborts = 0;
	static int numoperations = 0;
	private List comms;

	public Client() {
		try {
			ldm = new LookupDiscoveryManager(new String[]{GROUP}, null, this);
			synchronized (this) {
				wait(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new java.rmi.RMISecurityManager());
		} else {
			System.out.println("Your security Manager is: " + System.getSecurityManager().getClass().getName());
		}
		if (args.length < 5) {
			System.out.println("Athena XML test harness\n" + "--------------------------\n" + "Usage:\n\t" + "java [jvmoptions] org.jini.projects.harness.Client " + "[xmlfile] [ABORT|COMMIT] [L|D|R] [ATHENANAME] [JINIGROUP] [number of runs (default 1)] [keepconnection (Y|N) (default N)]");
			System.exit(0);
		}
		XMLFILENAME = args[0];
		if (args[1].trim().equals("ABORT")) {
			System.out.println("This harness will abort transactions");
			abortflag = true;
		} else if (args[1].trim().equals("COMMIT")) {
			System.out.println("This harness will commit transactions");
		} else {
			System.out.println("Please enter either ABORT or COMMIT for transaction completion");
			System.exit(0);
		}
		String type = null;
		if (args[2].trim().equals("D")) {
			type = "Disconnected";
			disconnected = true;
		} else {
			type = "Local";
			disconnected = false;
		}
		if (args[2].trim().equals("R")) {
			type = "Remote";
			remote = true;
		}
		System.out.println("If Resultsets are returned, they will be " + type + " resultsets.");
		ATHENANAME = args[3];
		GROUP = args[4];
		if (args.length >= 6)
			NUMRUNS = Integer.parseInt(args[5]);
		if (args.length >= 7)
			keepconnection = (args[6].equalsIgnoreCase("y") ? true : false);
		if (keepconnection)
			System.out.println("Connection will be reused between runs");
		else
			System.out.println("New connections will be requested for each run");
		System.out.println("Will use the " + ATHENANAME + " instance, in the " + GROUP + " jini group");
		Client ptctClient1 = new Client();
	}

	public void discarded(DiscoveryEvent event) {
	}

	public void discovered(DiscoveryEvent event) {
		try {
			if (!discovered) {
				discovered = true;
				//System.out.println("Discovered");
				net.jini.core.lookup.ServiceRegistrar[] registrars = event.getRegistrars();
				//Assume registrars[0] has service registered with it
				Class[] classType = {AthenaRegistration.class};
				Entry[] attr = new Entry[]{new Name(ATHENANAME)};
				net.jini.core.lookup.ServiceTemplate srvtemp = new net.jini.core.lookup.ServiceTemplate(null, classType, attr);
				//System.out.println("Looking up Athena: " + ATHENANAME);
				athReg = (AthenaRegistration) registrars[0].lookup(srvtemp);
				//System.out.println("Athena Found");
				System.out.println("About to start runs....");
				if (athReg != null) {
					//System.out.println("Initiating a transaction");
					Class[] txType = {TransactionManager.class};
					srvtemp = new net.jini.core.lookup.ServiceTemplate(null, txType, null);
					net.jini.core.lookup.ServiceMatches matches = null;
					try {
						matches = registrars[0].lookup(srvtemp, 1);
						if (matches.totalMatches > 0)
							txmgr = (TransactionManager) matches.items[0].service;
					} catch (java.rmi.RemoteException rex) {
						rex.printStackTrace();
						System.exit(1);
					}
					if (txmgr == null) {
						System.out.println("\tNo transaction manager found!");
					}
					//
					AthenaConnection conn = null;
					long start = System.currentTimeMillis();
					for (int j = 0; j < NUMRUNS; j++) {
						if (!keepconnection) {
							conn = athReg.getConnection("ClientHarness", 2 * TimeConstants.MINUTES);
							//  System.out.println("Creating new connection for
							// run");
						}
						if (conn == null) {
							System.out.println("Creating reusable connection");
							conn = athReg.getConnection("ClientHarness", 2 * TimeConstants.MINUTES);
						}
						if (disconnected) {
							Command comm = conn.getCommand();
							comm.setCallName("GETORDERS");
							comm.setParameter("LowestVal",new Integer(150));
							AthenaResultSet rrs = conn.executeQuery(comm);
							doTransactedWork(conn, org.jini.projects.athena.connection.AthenaConnection.DISCONNECTED, 1000);
						} else if (remote) {
							
							doTransactedWork(conn, org.jini.projects.athena.connection.AthenaConnection.REMOTE, 1000);
						} else {
						
							doTransactedWork(conn, org.jini.projects.athena.connection.AthenaConnection.LOCAL, 1000);
						}
						if (!keepconnection) {
							try {
								while (!conn.canRelease()) {
									Thread.yield();
								}
								//System.out.println("Releasing");
								conn.release();
								conn = null;
								//System.exit(0);
							} catch (Exception ex) {
								System.err.println("Oops");
								ex.printStackTrace();
							}
						}
					}
					if (keepconnection && conn != null) {
						try {
							while (!conn.canRelease()) {
								Thread.yield();
							}
							//System.out.println("Releasing");
							conn.release();
							conn = null;
							//System.exit(0);
						} catch (Exception ex) {
							System.err.println("Oops");
						}
					}
					long end = System.currentTimeMillis();
					System.out.println("Number of runs made was: \t" + NUMRUNS);
					System.out.println("Number of aborts was: \t\t" + numaborts);
					System.out.println("Number of operations was: \t" + numoperations);
					System.out.println("Time to complete all runs was:\t" + (end - start) + "ms (@ " + ((end - start) / 1000) + "s)");
                    //conn = athReg.getConnection("LeaseFailure",10 * TimeConstants.SECONDS);
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Could not find Athena");
		System.exit(0);
	}

	private void doTransactedWork(AthenaConnection connection, int local, int i) {
		if (connection != null && this.txmgr != null) {
			//System.out.println("Found participant and
			// Manger...continuing....");
			//TransactionManager.Created tcs = null;
			Transaction.Created txf = null;
			Transaction tx = null;
			try {
				//System.out.println("Creating transaction....");
				txf = TransactionFactory.create(txmgr, 200000);
			} catch (java.rmi.RemoteException rex) {
				txf = null;
				return;
			} catch (net.jini.core.lease.LeaseDeniedException ldex) {
				txmgr = null;
				return;
			}
			//Don't renew the lease in order to check for Prepare failures!
			new LeaseRenewalManager(txf.lease, Lease.FOREVER, null);
			tx = txf.transaction;
			//System.out.println("Executing updates now!");
			try {
				connection.setConnectionType(AthenaConnection.LOCAL);
				loadXml(XMLFILENAME, connection, tx);
				//System.out.println("Beginning end of transaction");
				Thread.yield();
                System.out.println("Waiting");
                try {
                	Thread.sleep(1000);
                } catch (Exception ex){}
;				if (abortflag) {
					try {
						//System.out.println("Going to abort!");
						tx.abort();
						//txmgr.commit(transactionID);
					} catch (net.jini.core.transaction.CannotAbortException ccex) {
						//System.out.println("Abortfailed!");
						ccex.printStackTrace();
					}
				} else {
					try {
						//System.out.println("Going to commit!");
						tx.commit();
						//txmgr.commit(transactionID);
					} catch (net.jini.core.transaction.CannotCommitException ccex) {
						//System.out.println("Commit failed!");
						numaborts++;
						ccex.printStackTrace();
					}
				}
			} catch (Exception e) {
				if (e instanceof CannotUpdateException)
					System.out.println("CannotUpdateEx!!!!");
				try {
					System.out.println(e.getMessage());
					e.printStackTrace();
					System.out.println("*****************Aborting!****************");
					tx.abort();
					numaborts++;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//System.out.println("Hello");
			}
		}
	}

	public void loadXml(String filename, AthenaConnection conn, Transaction tx) {
		//System.out.println("Loading " + filename);
		try {
			File f = new File(filename);
			try {
				BufferedInputStream b_xmlin = new BufferedInputStream(new FileInputStream(f));
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				HarnessLoader loader = new HarnessLoader(conn);
				org.xml.sax.helpers.DefaultHandler dh = loader;
				parser.parse(b_xmlin, dh);
				comms = loader.getCommands();
			} catch (SAXException e) {
				System.err.println("Error parsing XML: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error loading XML: " + e.getMessage());
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// URGENT Handle ParserConfigurationException
				e.printStackTrace();
			}
			List l = comms;
			System.out.println("List: " + l.size());
			int index = 0;
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				index++;
				numoperations++;
				Command c = (Command) iter.next();
				System.out.println(c);
				Object returnData = null;
				returnData = conn.executeUpdate(c, tx);
				processResults(c.getCallName(), index, returnData);
			}
		} catch (Exception e) {
			// URGENT Handle Exception
			e.printStackTrace();
		}
	}

	public void processResults(String callName, int callIndex, Object returnedResults) {
		//System.out.println("Call " + callIndex + "{ " + callName + "}");
		if (returnedResults instanceof java.util.HashMap) {
			System.out.println("\tData Output: " + returnedResults.toString());
		}
		if (returnedResults instanceof org.jini.projects.athena.resultset.AthenaResultSet) {
			AthenaResultSet rs = (AthenaResultSet) returnedResults;
			int row = 0;
			try {
				while (rs.next()) {
					row++;
					System.out.println("\tRow " + row);
					for (int i = 0; i < rs.getColumnCount(); i++)
						System.out.println("\t\t" + rs.getFieldName(i) + "\t(" + rs.getField(i) + ")");
				}
				rs.close();
			} catch (AthenaException e) {
				// URGENT Handle AthenaException
				e.printStackTrace();
			}
		}
	}
}