/*
 * ChunkedResultSetImpl.java
 *
 * Created on April 3, 2002, 2:09 PM
 */

package org.jini.projects.athena.resultset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jini.projects.athena.exception.AthenaException;

/**
 * A client-side implementaion of a result set where only a subset of data resides at the client.
 * Any invocations of methods that may cause the record 'pointer' to change result in a new subset of data
 *to be loaded from the server
 * @author  calum
 */
public class ChunkedResultSetImpl implements AthenaResultSet, java.io.Serializable {
    ChunkLoader chunk;
    ArrayList currentChunk;
    int chunksize;
    int currChunkNum;
    int numchunks;
    private ArrayList record = null;
    private HashMap columndetails = null;
    private int currRow = 0;
    private int checkRows = 0;

    /** Creates a new instance of ChunkedResultSetImpl */
    public ChunkedResultSetImpl(HashMap header, ChunkLoader chunk) {
        try {
            currentChunk = chunk.getFirstChunk();
            chunksize = chunk.getChunkSize();
            numchunks = chunk.numberofChunks();
            currChunkNum = 0;
            record = (ArrayList) currentChunk.get(0);
            this.chunk = chunk;
            columndetails = header;
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**  Closes the resultset
     *
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void close() throws AthenaException {
        try {
            chunk.cleanup();
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**  Returns index of column 'colname'
     *
     * @param  colname           String Name of column to find
     * @return                   int Column index
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public int findColumn(String colname) throws AthenaException {
        if (columndetails.containsKey(colname)) {
            return ((Integer) columndetails.get(colname)).intValue();
        }
        return -1;

    }

    /**  Moves the record pointer to the first record in the set
     *
     * @return                   boolean success value
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean first() throws AthenaException {
        try {
            this.currentChunk = chunk.getFirstChunk();
            record = (ArrayList) currentChunk.get(0);
            return true;
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    /**  Gets the ColumnCount attribute of the AthenaResultSet object
     *
     * @return                      The ColumnCount value
     * @exception  AthenaException  Description of Exception
     * @since
     */
    public int getColumnCount() throws AthenaException {
        return record.size();
    }

    /**  Returns concurrency model generally, READCOMMITTED or READSERIALIZABLE or
     *  NONE
     *
     * @return                   Integer
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public Integer getConcurrency() throws AthenaException {
        return new Integer(java.sql.ResultSet.CONCUR_READ_ONLY);
    }

    /**  Obtains a field for a given column index
     *
     * @param  columnIndex       int
     * @return                   the value of the given field
     * @since
     * @throws  AthenaException  wraps all exceptions
     */
    public Object getField(int columnIndex) throws AthenaException {
        if (record != null) {
            return record.get(columnIndex);
        } else {
            return null;
        }
    }

    /**  Obtains a field for a given name
     *
     * @param  name              String representing a 'column' name
     * @return                   the value of the given field
     * @since
     * @throws  AthenaException  wraps all exceptions
     */
    public Object getField(String name) throws AthenaException {
        if (record != null) {
            String tname = name.toLowerCase();
            int idx = ((Integer) columndetails.get(tname)).intValue();
            return record.get(idx);
        }
        return null;
    }

    /**  Gets the FieldName of the field at the given index.
     *
     * @param  field                Description of Parameter
     * @return                      The FieldName value
     * @exception  AthenaException  Description of Exception
     * @since
     */
    public String getFieldName(int field) throws AthenaException {
        if (columndetails.containsValue(new Integer(field))) {
            Set entries = columndetails.entrySet();
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
                Object x = iter.next();
                Map.Entry entr = (Map.Entry) x;
                Integer value = (Integer) entr.getValue();
                if (value.intValue() == field) {
                    return (String) entr.getKey();
                }
            }
        }
        return null;
    }

    /**  Moves the record pointer to the last record in the set
     *
     * @return                   boolean success value
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean last() throws AthenaException {
        try {
            this.currentChunk = chunk.getLastChunk();
            record = (ArrayList) currentChunk.get(currentChunk.size());
            return true;
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;

    }

    /**  Moves you to record <CODE>pos</CODE> . If pos is positive, the record
     *  pointer is placed on the record <CODE>0+pos</CODE> , if the value is
     *  negative the record pointer is placed on record <CODE>&gt;setlength&lt;-pos
     *  </CODE>;
     *
     * @param  pos               record number you wish to move to
     * @return                   Integer
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public Integer moveAbsolute(int pos) throws AthenaException {
        /*try {
     this.currentChunk = chunk.getChunkFor(pos);
     record = (ArrayList) currentChunk.get(0);
     return true;
 } catch (Exception ex) {
     System.out.println("Err: " + ex.getMessage());
     ex.printStackTrace();
 }
 return false;
         */
        return new Integer(0);
    }

    /**  Moves the record pointer to the next record in the set
     *
     * @return                   boolean success value
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean next() throws AthenaException {
        try {

            if (this.currRow == this.chunksize) {
                if (this.currChunkNum < this.numchunks - 1) {
                    currentChunk = chunk.getNextChunk();
                    record = (ArrayList) currentChunk.get(0);
                    currRow = 1;
                    this.currChunkNum++;
                } else {
                    //Hit end of resultset
                    return false;
                }
            } else {
                if (currRow == currentChunk.size()) {
                    return false;
                }
                record = (ArrayList) currentChunk.get(currRow++);
            }
            return true;
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;

    }

    /**  Moves the record pointer to the PREVIOUS record in the set
     *
     * @return                   boolean success value
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean previous() throws AthenaException {
        try {
            if (this.currRow == 0) {
                if (this.currChunkNum > 0) {
                    currentChunk = chunk.getPreviousChunk();
                    record = (ArrayList) currentChunk.get(currentChunk.size());
                    currRow = currentChunk.size();
                    this.currChunkNum--;
                } else {
                    //Hit end of resultset
                    return false;
                }
            } else
                record = (ArrayList) currentChunk.get(currRow--);
            return true;
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    /**  Refreshes the row to flush any updates into it. <BR>
     *  <B>NOTE:</B> Successful implementation of this method is optional.For JDBC,
     *  Some JDBC2.0 drivers support this others do not. <BR>
     *  In the case of other resources the amount of overhead may prove
     *  implementation to be too inefficient. In these cases it is advised that
     *  refreshRow does not yield new information
     *
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void refreshRow() throws AthenaException {
    }

    /**  Updates the Object at column <CODE>columnindex</CODE> to value <CODE>obj
     *  </CODE>. <B>NOTE:</B> Successful implementation of this method is optional.
     *  Some JDBC2.0 drivers support this others do not. <BR>
     *  In the case of other resources the amount of overhead may prove
     *  implementation to be too inefficient. In these cases it is advised that
     *  updateObject throws an <CODE>AthenaException <CODE>
     *
     *
     *
     * @param  columnindex       Value representing index of field within record
     * @param  obj               New value for field
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void updateObject(int columnindex, Object obj) throws AthenaException {
    }

    /**  Flushes updates in row to ResultSet the single row that the resultset
     *  record pointer is looking at. <B>NOTE:</B> Successful implementation of
     *  this method is optional. Some JDBC2.0 drivers support this others do not.
     *  <BR>
     *  In the case of other resources the amount of overhead may prove
     *  implementation to be too inefficient. In these cases it is advised that
     *  <CODE>updateRow</CODE> throws an <CODE>AthenaException <CODE>
     *
     *
     *
     * @since
     * @throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void updateRow() throws AthenaException {
    }


    /**
     * @see org.jini.projects.athena.resultset.AthenaResultSet#getRowCount()
     */
    public long getRowCount() throws AthenaException {
        return 0;
    }

}
