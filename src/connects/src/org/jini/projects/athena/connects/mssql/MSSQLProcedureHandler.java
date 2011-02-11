package org.jini.projects.athena.connects.mssql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.jini.projects.athena.connection.ProcedureHandler;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.resultset.AthenaResultSetImpl;
import org.jini.projects.athena.util.builders.LocalResultSetBuilder;

/**
 * @author calum
 *
 * org.jini.projects.athena
 */
public class MSSQLProcedureHandler extends ProcedureHandler {
    static ResourceManager rm = ResourceManager.getResourceManager();
    AthenaResultSet rs = null;


    public MSSQLProcedureHandler() {

    }


    protected AthenaResultSet getProcedureDefinition(String catalog, String schema, String procedureName) throws SQLException {
        Statement dat = conn.createStatement();
        dat.execute("exec sp_sproc_columns " +
                "@procedure_name='" + procedureName + "', " +
                "@procedure_owner='" + schema + "', " +
                "@procedure_qualifier='" + catalog + "'"
        );
//        ResultSet rs = dat.getResultSet();
//        while(rs.next()){
//            System.out.println(rs.getString(1));
//
//        }
        ResultSet jdbcrs = dat.getResultSet();

        HashMap details = LocalResultSetBuilder.buildColDetails(jdbcrs);

        ArrayList arr = LocalResultSetBuilder.buildlocal(jdbcrs, 100);

        jdbcrs.close();
        dat.close();

        AthenaResultSet ars = new AthenaResultSetImpl(arr, details);
        try {
            for (int i = 0; i < ars.getColumnCount(); i++)
                System.out.println(ars.getFieldName(i) + ": " + ars.getField(i));
        } catch (AthenaException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
        return ars;
    }


//    public static void main(String[] args) {
//        MSSQLProcedureHandler pro = new MSSQLProcedureHandler();
//        pro.addProcedureDefinition("spaceDB", "dbo", "ins_simple");
//        ProcedureDefinition def = pro.getProcedure("spaceDB.ins_simple");
//        HashMap m = def.getInParams();
//        Iterator iter = m.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entr = (Map.Entry) iter.next();
//            ProcedureDefinition.StoredProcedureParameter param =
//                    (ProcedureDefinition.StoredProcedureParameter) entr.getValue();
//            System.out.println("Entry: " + entr.getKey());
//            System.out.println("\t" + param.colName);
//
//        }
//        System.exit(0);
//    }


}
