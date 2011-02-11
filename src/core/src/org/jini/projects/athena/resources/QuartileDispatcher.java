/*
 * QuartileDispatcher.java
 *
 * Created on May 7, 2002, 11:07 AM
 */

package org.jini.projects.athena.resources;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implements a Quartile Dispatcher, an object spawning threads up to certain
 * restratints and restarting at certain values. System has an amount of work
 * <i>n</i> which if done in parallel, assumimg all other factors to be equal,
 * should spawn a total of <i>n</i> thread A Quartile Dispatcher has four
 * values:<br>
 * <ul>
 *  <li>The maximum number of thread to use in the dispatcher</li>
 *  <li>The safety limit - the point at which the dispatcher will stop creating threads generally <i>0.9 < safety < max</i></li>
 *  <li>The restart value - the number of active worker threads, which will cause the dispatcher to restart creating threads
 *  <li>the CreateAt value - a number of active threads where the dispatcher may create holding structures
 *  <ul>
 *
 * <b>This is to be considerd an EXTREME ALPHA CLASS</b>
 * @author  calum
 */
public class QuartileDispatcher extends Object {
    private int max;
    private int safety;
    private int createAt;
    private int restart;
    private int currentTotal = 0;
    private boolean safetyhit = false;
    private ArrayList queue = new ArrayList();
    private static Random RANDOM = new Random();
    ArrayList arrList = new ArrayList(50);
    protected static QuartileDispatcher DISPATCHER = new QuartileDispatcher();

    /** Creates new QuartileDispatcher */

    public QuartileDispatcher() {
        for (int i = 0; i < 50; i++) {
            arrList.add(new availTest());
        }
        max = arrList.size();
        safety = (int) (max * 0.9);
        createAt = (int) (max * 0.5);
        restart = (int) (max * 0.1);

    }

    public QuartileDispatcher(int max, double safe, double rest) {
        arrList = new ArrayList(max);
        for (int i = 0; i < max; i++) {
            arrList.add(new availTest());
        }
        max = arrList.size();
        safety = (int) (max * safe);
        createAt = (int) (max * 0.5);
        restart = (int) (max * rest);

    }

    public synchronized Object getResource() {
        boolean returnnow = false;
        while (!returnnow) {
            while (safetyhit) {
                try {
                    wait(250);
                } catch (InterruptedException interex) {
                    System.out.println("Interrupted");
                }
            }
            for (int i = 0; i < (safetyhit ? safety : arrList.size()); i++) {
                availTest avail = (availTest) arrList.get(i);
                if (i > safety)
                    System.out.println("               ******AFTER SAFETY MARGIN*****");
                if (i == safety) {
                    safetyhit = true;
                }
                if (avail.available) {
                    avail.available = false;
                    //            System.out.println("Found one @ " + i);
                    currentTotal++;
                    return avail;
                }
                //ELSE QUEUE the REQUEST
            }
        }
        return null;

    }

    public synchronized void returnResource(Object obj) {

        currentTotal--;
        //System.out.println("Returning resource");
        ((availTest) obj).available = true;
        if (safetyhit = true && currentTotal == restart) {
//            System.out.println("Restart enabled: " + restart);
            safetyhit = false;
            notifyAll();
        }

    }

    public static void main(String[] args) {
        int numtorun = 100;
        int max = 10;
        double safety = 0.9;
        double restart = 0.2;
        if (args.length >= 1) {
            numtorun = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            max = Integer.parseInt(args[1]);
        }

        if (args.length >= 3) {
            safety = Double.parseDouble(args[2]);
        }
        if (args.length >= 4) {
            restart = Double.parseDouble(args[3]);
        }
        long waittime = 10000;
        if (args.length >= 5) {
            waittime = Long.parseLong(args[4]);
        }
        QuartileDispatcher dispatch = new QuartileDispatcher(max, safety, restart);

        for (int i = 0; i < numtorun; i++) {
            Thread hmm = new MultiRequest(i);
            hmm.start();
        }

        //Object obj1 = dispatch.getResource();
        //Object obj2 = dispatch.getResource();
        ///Object obj3 = dispatch.getResource();
        //Object obj4 = dispatch.getResource();
        // Object obj5 = dispatch.getResource();
        //Object obj6 = dispatch.getResource();
        //Object obj7 = dispatch.getResource();
        //Object obj8 = dispatch.getResource();
        //Object obj9 = dispatch.getResource();
        try {
            Thread.sleep(waittime);
        } catch (Exception ex) {
        }
        System.out.println("Tests Completed");
        System.out.println("Avgtime: " + ResourceStat.getAvg());
        System.out.println("Hi/Lo : " + ResourceStat.highestTime + "/" + ResourceStat.lowestTime);
    }


    public class availTest {
        public boolean available = true;
        public String test = "";

        public availTest() {
        }
    }
}

/**
 * Provides resource values for testing the QDispatcher
 */

class ResourceStat {
    static int totalThreads;
    static int totalTime;
    public static long lowestTime = -1;
    public static long highestTime = 0;

    public static synchronized void postResult(long timems) {
        totalTime += timems;
        if (lowestTime > timems || lowestTime == -1)
            lowestTime = timems;
        if (highestTime < timems)
            highestTime = timems;
        totalThreads++;
    }

    public static double getAvg() {
        return (double) totalTime / (double) totalThreads;
    }


}

/**
 * Makes requests for the dispatcher to do many actions
 */
class MultiRequest extends Thread {
    Object obj;
    int Index;

    private static Random RANDOM = new Random();

    public MultiRequest(int val) {
        Index = val;

    }

    public void run() {
        long start = System.currentTimeMillis();
        obj = QuartileDispatcher.DISPATCHER.getResource();
        if (obj == null) {
            System.out.println("Why returning null???");
            System.exit(0);
        }
        try {
            FileWriter fil = new FileWriter("myfile." + String.valueOf(Index));
            for (int i = 0; i < 250; i++) {
                fil.write("Data: " + Index + ":" + i);
            }
            fil.flush();
            fil.close();
        } catch (Exception ex) {
            System.out.println("Error: HMMMM");
        }
        QuartileDispatcher.DISPATCHER.returnResource(obj);
        ResourceStat.postResult(System.currentTimeMillis() - start);
        System.out.println("Thread " + Index + " completed");
    }
}