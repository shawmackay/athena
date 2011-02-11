/*
 * RMIChunkerImpl.java
 *
 * Created on April 3, 2002, 2:13 PM
 */

package org.jini.projects.athena.resultset;

import java.util.ArrayList;

import org.jini.projects.athena.resources.Chunkable;

/**
 * A Chunked resultset implementation that hold subsets of data, while
 * allowing the user and the system to imagine that the whol eset of data is available.
 * @author  calum
 */
public class ChunkLoaderImpl implements ChunkLoader {

    Chunkable chunk;

    /** Creates a new instance of RMIChunkerImpl */
    public ChunkLoaderImpl(Chunkable chunk) throws java.rmi.RemoteException {
        this.chunk = chunk;
    }

    public ArrayList getChunk(int i) throws java.rmi.RemoteException {
        return chunk.getChunk(i);
    }

    public ArrayList getChunkFor(int record) throws java.rmi.RemoteException {
        return chunk.getChunkFor(record);
    }

    public int getChunkSize() throws java.rmi.RemoteException {
        return chunk.getChunkSize();
    }

    public ArrayList getFirstChunk() throws java.rmi.RemoteException {
        return chunk.getFirstChunk();
    }

    public ArrayList getLastChunk() throws java.rmi.RemoteException {
        return chunk.getLastChunk();
    }

    public ArrayList getNextChunk() throws java.rmi.RemoteException {
        return chunk.getNextChunk();
    }

    public ArrayList getPreviousChunk() throws java.rmi.RemoteException {
        return chunk.getPreviousChunk();
    }

    public int numberofChunks() throws java.rmi.RemoteException {
        return chunk.numberofChunks();
    }

    public void cleanup() throws java.rmi.RemoteException {
        chunk.cleanup();
    }

}
