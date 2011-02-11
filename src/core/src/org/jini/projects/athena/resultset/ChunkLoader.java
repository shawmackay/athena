/*
 * RMIChunker.java
 *
 * Created on April 3, 2002, 2:10 PM
 */

package org.jini.projects.athena.resultset;

import java.util.ArrayList;

/**
 *  Interface for block resultsets. Chunks are sub-units of
 * a ResultSet allowing greater performance than a DisconnectedResultset,
 * but a smaller memory footprint than a large LocalResultSet.
 *Remote calls are only made when the record 'pointer' crosses a chunk boundary
 * at whioch point the required chunk is downloaded from the server.
 *Chunks will generally be stored on disk
 * @author  calum
 */
public interface ChunkLoader extends java.rmi.Remote {
    public ArrayList getNextChunk() throws java.rmi.RemoteException;

    public ArrayList getPreviousChunk() throws java.rmi.RemoteException;

    public ArrayList getFirstChunk() throws java.rmi.RemoteException;

    public ArrayList getLastChunk() throws java.rmi.RemoteException;

    public ArrayList getChunk(int i) throws java.rmi.RemoteException;

    public int numberofChunks() throws java.rmi.RemoteException;

    public int getChunkSize() throws java.rmi.RemoteException;

    public ArrayList getChunkFor(int record) throws java.rmi.RemoteException;

    public void cleanup() throws java.rmi.RemoteException;
}
