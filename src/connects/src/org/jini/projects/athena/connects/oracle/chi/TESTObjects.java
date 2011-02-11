/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 21-Aug-2002
 * Time: 10:37:26
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.oracle.chi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;

import oracle.sql.STRUCT;

public class TESTObjects {
    public TESTObjects() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@nts4_008.countrywide-assured.co.uk:1521:SSDR", "sys", "super");
            String cmd;
            Statement stmt = conn.createStatement();
            /*cmd = "CREATE OR REPLACE TYPE type_struct AS object(field1 NUMBER, field2 DATE)";
            stmt.execute(cmd);
            cmd = "CREATE OR REPLACE TABLE struct_table(col1 type_struct)";
            stmt.execute(cmd);*/
            cmd = "Insert into struct_table VALUES (type_struct(10,'01-apr-2001'))";
            stmt.execute(cmd);
            cmd = "INSERT INTO struct_table VALUES (type_struct(20,'02-may-2002'))";
            stmt.execute(cmd);
            conn.commit();
            ResultSet rs = stmt.executeQuery("SELECT * from struct_table");
            while (rs.next()) {

                STRUCT oracleSTRUCT = (STRUCT) rs.getObject(1);
                Struct struct = (Struct) oracleSTRUCT;
                System.out.println("SQL Type: " + oracleSTRUCT.getSQLTypeName());
                ResultSetMetaData smd = oracleSTRUCT.getDescriptor().getMetaData();
                System.out.println(smd.getColumnName(1));
                Object[] objarr = oracleSTRUCT.getAttributes();

                System.out.println("Data: ");
                for (int i = 0; i < objarr.length; i++) {
                    System.out.println("\tType: " + objarr[i].getClass().getName() + "; Value: " + objarr[i]);
                }

            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TESTObjects app = new TESTObjects();
    }
}
