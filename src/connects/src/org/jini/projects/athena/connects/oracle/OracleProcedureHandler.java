package org.jini.projects.athena.connects.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jini.projects.athena.connection.ProcedureHandler;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.resultset.AthenaResultSetImpl;
import org.jini.projects.athena.util.builders.LocalResultSetBuilder;

/**
 * @author calum
 *
 * org.jini.projects.athena
 */
public class OracleProcedureHandler extends ProcedureHandler {
    static ResourceManager rm = ResourceManager.getResourceManager();
    AthenaResultSet rs = null;


    public OracleProcedureHandler() {

    }


    protected AthenaResultSet getProcedureDefinition(String catalog, String schema, String procedureName) throws SQLException {
        ResultSet jdbcrs = conn.getMetaData().getProcedureColumns(catalog, schema, procedureName, null);
        HashMap details = LocalResultSetBuilder.buildColDetails(jdbcrs);

        ArrayList arr = LocalResultSetBuilder.buildlocal(jdbcrs, 100);

        jdbcrs.close();

        AthenaResultSet ars = new AthenaResultSetImpl(arr, details);
        return ars;
    }


//    public static void main(String[] args) {
//        OracleProcedureHandler pro = new OracleProcedureHandler();
//        pro.addProcedureDefinition("SALESUPP", "PKG_CORRESPONDENCE", "CORRES_MAINT");
//        pro.addProcedureDefinition("SALESUPP", "PKG_CONTACT_CENTRE", "CC_CASE_LOAD_NO_COMM");
//        pro.getProcedure("PKG_CORRESPONDENCE.CORRES_MAINT");
//    }


}
