package org.jini.projects.athena.connection;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.util.HashMap;

import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 * Represents an internally cached definition of a stored procedure. This
 * can then be used, in conjunction with an appropriate stored proecuder dialect, to
 * call a procedure without having to make constant round trips to the database.
 */
public class ProcedureDefinition {

    AthenaResultSet rs;

    /**
     * Pass the stored procedure metadata and use it to help construct PreparedStatements later.
     * @param rs metadata regarding a stored procedure
     */
    public ProcedureDefinition(AthenaResultSet rs) {
        this.rs = rs;
    }
    
    /**
     * Get the map of output parameters
     * @return the OUT parameter list
     */
    public HashMap getOutParams() {
        return getParametersOfType(DatabaseMetaData.procedureColumnOut);
    }
    /**
     * Get the map of input parameters
     * @return the IN parameter list
     */
    public HashMap getInParams() {
        return getParametersOfType(DatabaseMetaData.procedureColumnIn);
    }
    /**
     * Get the map of input/output parameters
     * @return the INOUT parameter list
     */
    public HashMap getInOutParams() {
        return getParametersOfType(DatabaseMetaData.procedureColumnInOut);
    }

    private HashMap getParametersOfType(int columntype) {
        HashMap outparm = new HashMap();
        if (rs != null) {
            try {
                rs.first();
                int loop = 1;
                while (rs.next()) {
                    Object item = rs.getField("COLUMN_TYPE");
                    int coltype = -1;
                    if (item instanceof BigDecimal)
                        coltype = ((BigDecimal) item).intValue();
                    if (item instanceof Integer)
                        coltype = ((Integer) item).intValue();
                    if (item instanceof Short)
                        coltype = ((Short) item).intValue();
                    
                    if (coltype == columntype) {
                        String columnname = (String) rs.getField("COLUMN_NAME");
                        Object internalType = rs.getField("DATA_TYPE");
                    
                        int intType = -1;
                        if (internalType instanceof BigDecimal)
                            intType = ((BigDecimal) internalType).intValue();
                        if (internalType instanceof Integer)
                            intType = ((Integer) internalType).intValue();
                        if (internalType instanceof Short)
                            intType = ((Short) internalType).intValue();

                        outparm.put(columnname, new StoredProcedureParameter(columnname, intType, loop));
                    }
                    loop++;
                }
            } catch (AthenaException e) {
                System.out.println("An exception occured while looking at the def");
                return null;
            }
        }
        return outparm;
    }
/**
 * Represents a parameter used for stored proecedures
 * @author calum
 *
 */
    public class StoredProcedureParameter {
        public String colName;
        public int colType;
        public int colIndex;

        public StoredProcedureParameter(String colname, int type, int index) {
            colName = colname;
            colType = type;
            colIndex = index;
        }
    }
}
