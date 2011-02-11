/*
 * ChunkedObject.java
 *
 * Created on April 3, 2002, 12:49 PM
 */

package org.jini.projects.athena.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

/**
 * ToDo Use Nio packages Represents an object, in this case an ArrayList, that
 * can be split into chunks.
 *
 * @author calum
 */
public class ChunkedObject implements Chunkable {
    BackgroundChunkStore back;
    ArrayList coll;
    char[] id;
    int chunkSize;
    int numberofChunks;
    int currentChunk = -1;
    static Random CHUNKRAND = new Random(System.currentTimeMillis());
    Thread t;
    Thread t2;
    Logger l = Logger.getLogger("org.jini.projects.athena.resources");

    /** Creates a new instance of ChunkedObject */
    public ChunkedObject(ArrayList coll, int chunkSize, String key) {
        Random rand = CHUNKRAND;
        this.coll = new ArrayList(coll);
        this.chunkSize = chunkSize;
        int size = coll.size();
        this.numberofChunks = (size % chunkSize == 0) ? (size / chunkSize) : (size / chunkSize) + 1;
        l.finer("Chunk size = " + chunkSize);
        l.finer("Number of chunks = " + this.numberofChunks);
        l.finer("Number of items = " + size);
        l.finer("Last Chunk size = " + ((size % chunkSize == 0) ? chunkSize : size % chunkSize));
        if (key == null) {
            String idval = String.valueOf(System.currentTimeMillis()) + String.valueOf(rand.nextLong());
            id = idval.toCharArray();
        } else {
            id = key.toCharArray();
        }
    }

    /** Creates a new instance of ChunkedObject */
    public ChunkedObject(ArrayList coll, int chunkSize) {
        this(coll, chunkSize, null);
    }

    /**
     * Retunrs the entire object as it was passed in the constructor
     */
    public ArrayList getCollection() {
        return coll;
    }

    public ArrayList getChunk(int i) {
        return getChunkFile(i);
    }

    /**
     * Physically loads the specified Chunk from disk
     */
    private ArrayList getChunkFile(int n) {
        int retries = 10;
        while (back.getCurrentChunk() + 1 == n)
            Thread.yield();
        for (int i = 0; i < retries; i++) {
            try {
                File tDir = new File("chunkdir");
                l.finest("opening file " + String.valueOf(this.id) + n + ".chunk");
                File tFile = new File(tDir, String.valueOf(this.id) + n + ".chunk");
                while (!tFile.exists())
                    Thread.yield();
                FileInputStream fil = new FileInputStream(tFile);
                BufferedInputStream bos = new BufferedInputStream(fil);
                ObjectInputStream oos = new ObjectInputStream(bos);
                ArrayList arr = (ArrayList) oos.readObject();
                oos.close();
                return arr;
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage() + " : " + ex.getClass().getName());
                try {
                    wait(15);
                } catch (Exception ex2) {
                }
            } catch (EOFException ex) {
                System.out.println(ex.getMessage() + " : " + ex.getClass().getName());
                try {
                    wait(25);
                } catch (Exception ex2) {
                }
            } catch (Exception ex) {
                System.out.println("Err: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("Chunk loadDefs error");
        return null;
    }

    /**
     * NEEDS IMPLEMENTING - will return the chunk containg this record as would
     * be found in the overall collection
     */
    public ArrayList getChunkFor(int record) {
        return null;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public ArrayList getFirstChunk() {
        currentChunk = 0;
        return getChunkFile(0);
    }

    public ArrayList getLastChunk() {
        currentChunk = numberofChunks - 1;
        while (back.getCurrentChunk() < this.currentChunk) {
            Thread.yield();
        }
        return getChunkFile(this.currentChunk);
    }

    public ArrayList getNextChunk() {
        //System.out.println("Getting next Chunk");
        if (back.getCurrentChunk() < numberofChunks)
            while (back.getCurrentChunk() < this.currentChunk) {
                Thread.yield();
            }
        if (this.currentChunk == this.numberofChunks - 1) {
            return getChunkFile(this.currentChunk);
        }
        //System.out.println("CurrentChunk is : " + this.currentChunk);
        return getChunkFile(++this.currentChunk);
    }

    public ArrayList getPreviousChunk() {
        if (this.currentChunk == 0)
            return getChunkFile(0);
        return getChunkFile(--this.currentChunk);
    }

    public int numberofChunks() {
        return numberofChunks;
    }

    public static void main(String[] args) {
        ArrayList arr = new ArrayList();
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('a'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('b'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('c'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('d'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('e'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('f'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('g'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('h'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('i'));
        }
        for (int a = 0; a < 10000; a++) {
            arr.add(new Character('j'));
        }
        ChunkedObject obje = new ChunkedObject(arr, 20);
        //obje.chunkNow();
        obje = new ChunkedObject(arr, 3);
        //obje.chunkNow();
        obje = new ChunkedObject(arr, 4);
        //obje.chunkNow();
        obje = new ChunkedObject(arr, 5);
        //obje.chunkNow();
        obje = new ChunkedObject(arr, 1000);
        obje.chunkNow();
        //ArrayList arr;
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration: " + i);
            arr = obje.getNextChunk();
        }
        arr = obje.getFirstChunk();
        System.out.println("Output : " + arr.get(0));
        arr = obje.getNextChunk();
        System.out.println("Output : " + arr.get(0));
        arr = obje.getLastChunk();
        System.out.println("Output : " + arr.get(0));
        arr = obje.getPreviousChunk();
        System.out.println("Output : " + arr.get(0));
        arr = obje.getChunk(4);
        System.out.println("Output : " + arr.get(0));
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        obje.cleanup();
    }

    class BackgroundChunkStore implements Runnable {
        boolean firstchunkcomplete = false;
        private int currentChunk = 0;

        public int getCurrentChunk() {
            return currentChunk;
        }

        public void run() {
            File tDir = new File("chunkdir");
            if (!tDir.exists()) {
                System.out.println("Creating directory");
                tDir.mkdir();
            }
            long tnumChunks = numberofChunks;
            int tChunkSize = chunkSize;
            System.out.println("***" + tChunkSize);
            ArrayList tColl = coll;
            long currRec = 0;
            for (int a = 0; a < tnumChunks; a++) {
                l.finest("\rWriting " + a + " chunk of " + tnumChunks);
                try {
                    File tFile = new File(tDir, String.valueOf(id) + a + ".chunk");
                    if (tFile.exists()) {
                        l.finest("Not re-writing chunk exists");
                    } else {
                        ArrayList arr = new ArrayList((int) tChunkSize);
                        Iterator iter = tColl.iterator();
                        currRec = 0;
                        //System.out.println("Item " +
                        // ((ArrayList)tColl.get(0)).get(0));
                        while (currRec < (int) tChunkSize) {
                            if (iter.hasNext()) {
                                arr.add(iter.next());
                                iter.remove();
                            }
                            currRec++;
                        }
                        FileOutputStream fil = new FileOutputStream(tFile);
                        BufferedOutputStream bos = new BufferedOutputStream(fil);
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        oos.writeObject(arr);
                        oos.flush();
                        Thread.yield();
                        oos.close();
                    }
                    if (a == 0) {
                        l.finest("First Chunk completed");
                        firstchunkcomplete = true;
                    }
                } catch (Exception ex) {
                    System.out.println("Err: " + ex.getMessage());
                    ex.printStackTrace();
                }
                Thread.yield();
                currentChunk = a;
            }
        }

        public boolean completedFirst() {
            return firstchunkcomplete;
        }
    }

    public synchronized void chunkNow() {
        back = new BackgroundChunkStore();
        t = new Thread(back);
        t.start();
        while (!back.completedFirst()) {
            Thread.yield();
        }
        l.finest("Exiting chunker");
    }

    public void finalize() {
    }

    public void cleanup() {
        File tFile = new File("chunkdir");
        File[] names = tFile.listFiles();
        for (int a = 0; a < names.length; a++) {
            if (names[a].getName().indexOf(String.valueOf(this.id)) != -1) {
                l.finest("Deleting Chunk file: Name " + names[a].getName());
                names[a].delete();
            }
        }
    }
}
