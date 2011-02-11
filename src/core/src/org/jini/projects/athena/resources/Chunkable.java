/*
 * Chunkable.java
 *
 * Created on April 3, 2002, 12:38 PM
 */

package org.jini.projects.athena.resources;

import java.util.ArrayList;

/**
 * Allows an container (i.e. indexed collection)object to be split into parts(chunks) without any impact to a client. Intended to be low level
 * @see org.jini.projects.athena.resultset.ChunkedResultSetImpl
 * @see org.jini.projects.athena.resultset.ChunkLoader
 * @author  calum
 */
public interface Chunkable {
    /**
     * Obtains the next part of the overall object, relative to the current part that the client class holds.
     */
    public ArrayList getNextChunk();

    /**
     * Obtains the previous part of the overall object, relative to the current part that the client class holds.
     */
    public ArrayList getPreviousChunk();

    /**
     * Obtains the first part of the overall object.
     */
    public ArrayList getFirstChunk();

    /**
     * Obtains the last part of the overall object, in most cases this will be smaller in size than every other chunk.
     */
    public ArrayList getLastChunk();

    /**
     * Get chunk number whose absolute index is <code>i</code>
     */
    public ArrayList getChunk(int i);

    /**
     * Get the number of chunks that the overall object has been split into
     */
    public int numberofChunks();

    /**
     * Get the size of each chunk.<br>
     * For full client storage an abject could be specified as:<p><i>
     * ObjectSize=numberOfChunks*chunkSize</i><br> Optimum chunk size is detemined by<br><i>
     * OptimumSize =(numberOfChunks-1)*chunkSize+sizeOfLastChunk</i> - however this will require another
     * remote call to get the last chunk.
     */
    public int getChunkSize();

    /**
     * Get the chunk containing the object whose index in the overall object is <code>record</code>
     */
    public ArrayList getChunkFor(int record);

    /**
     * Split the object into a number of chunks
     */
    public void chunkNow();

    /**
     * Cleanup the chunk objects. If the chunks are held in memory, remove them, or if they are on disk, delete.
     */
    public void cleanup();
}
