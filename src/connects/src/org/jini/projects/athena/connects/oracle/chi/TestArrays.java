/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 21-Aug-2002
 * Time: 11:56:33
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.oracle.chi;


import java.sql.*;


public class TestArrays {
    public TestArrays() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@nts4_008.countrywide-assured.co.uk:1521:SSDR", "sys", "super");
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                stmt.execute("DROP TABLE varray_table");
                stmt.execute("DROP TYPE num_varray");
                stmt.execute("DROP TYPE ts_varray");
            } catch (SQLException ex) {

            }

            stmt.execute("CREATE TYPE num_varray AS VARRAY(10) OF NUMBER(12,2)");
            stmt.execute("CREATE TYPE ts_varray as VARRAY(5) OF type_struct");
            stmt.execute("CREATE TABLE varray_table(col1 ts_varray, col2 type_struct)");
            stmt.execute("INSERT INTO varray_table VALUES (ts_varray(type_struct(20,'02-may-2002'),type_struct(40,'03-AUG-2002'))," +
                    //stmt.execute("INSERT INTO varray_table VALUES (num_varray(100,200)," +
                    "" +
                    "" +
                    "type_struct(20,'02-may-2002'))");

            ResultSet rs = stmt.executeQuery("SELECT * FROM varray_table");
            CallableStatement cs = conn.prepareCall("begin getObjArr(?);end;");
            cs.registerOutParameter(1, Types.ARRAY, "TS_ARR");
            if (cs.execute()) {
                System.out.println("Call completed Successfully - ResultSet");
            } else {
                Array oarr = (Array) cs.getArray(1);
                //ARRAY oarr = (ARRAY) pstmt.getObject(1);
                System.out.println(oarr.getBaseTypeName());
                Object[] objarr = (Object[]) oarr.getArray();
                for (int i = 0; i < objarr.length; i++) {
                    Struct struct = (Struct) objarr[i];

                    Object[] attr = struct.getAttributes();
                    for (int j = 0; j < attr.length; j++) {
                        System.out.println("Value: " + attr[j]);
                    }
                }
                System.out.println("Call not completed");

            }
            cs.close();
            cs = conn.prepareCall("begin insertObj(type_struct(50,'28-Feb-1976'));end;");
            cs.execute();
            showResultSet(rs);
            rs.close();
            conn.close();

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void showResultSet(ResultSet rs) throws SQLException {
        int line = 0;
        ResultSetMetaData rsm = rs.getMetaData();
        while (rs.next()) {
            line++;
            System.out.println("Row " + line + " : ");
            java.sql.Array array = rs.getArray(1);
            //ARRAY array = ((OracleResultSet) rs).getARRAY(1);
            if (rsm.getColumnType(2) == Types.STRUCT)
                System.out.println("Column 2 is an Object TYPE");
            System.out.println("Array is of type " + array.getBaseTypeName());
            array.getResultSet();
            System.out.println("Array element is of typecode " + array.getBaseType());

            System.out.println("Array Class: " + array.getArray().getClass().getName());
            Object[] objarray = (Object[]) array.getArray();
            System.out.println("Item Class: " + objarray[0].getClass().getName());
            for (int i = 0; i < objarray.length; i++) {
                java.sql.Struct oracleSTRUCT = (java.sql.Struct) objarray[i];
                System.out.println("SQL Type: " + oracleSTRUCT.getSQLTypeName());
                Object[] objarr = oracleSTRUCT.getAttributes();
                System.out.println("Data: ");
                for (int j = 0; j < objarr.length; j++) {
                    System.out.println("\tType: " + objarr[j].getClass().getName() + "; Value: " + objarr[j]);
                }
            }

        }
    }

    public static void main(String[] args) {
        TestArrays app = new TestArrays();
    }
}
