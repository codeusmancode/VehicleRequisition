package view;

import javax.naming.NamingException;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

public class ConnectionProvider {
    private static DataSource myDS = null;
    static {
        try {
            Context ctx = new InitialContext();
            System.out.println("@@@@@@@@@@@initializing datasource@@@@@@@@@@");
            myDS = (DataSource) ctx.lookup("jdbc/appsDS");
            // your datasource jndi name as defined during configuration
            if (ctx != null)
                ctx.close();
        } catch (NamingException ne) {
            //ne.printStackTrace();//ideally you should log it
            throw new RuntimeException(ne);
            // means jndi setup is not correct or doesn't exist
        }
    }

    private ConnectionProvider() {
    }

    public static void closeConnection() throws SQLException {
        if (myDS != null)
            myDS.getConnection().close();
    }
    public static Connection getConnection() throws SQLException {
        
        if (myDS == null)
            throw new IllegalStateException("AppsDatasource is not properly initialized or available");
        return myDS.getConnection();
    }
}
