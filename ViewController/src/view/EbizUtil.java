package view;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.apps.fnd.ext.common.EBiz;

public class EbizUtil {
    private static final Logger logger = Logger.getLogger(EbizUtil.class.getName());
    private static EBiz INSTANCE = null;
    static {
        Connection connection = null;
        try {
            connection = ConnectionProvider.getConnection();
            // DO NOT hard code applServerID for a real application
            // Get applServerID as CONTEXT-PARAM from web.xml or
            //elsewhere
            INSTANCE = new EBiz(connection, "704DF51EF35541B7E0531A00A8C1DDE231403888722873272641275155755834" );//" 6A91D2EBA4AE155BE0532000A8C1A2B725313924803827079388228318891015");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException while creating EBiz instance -->", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while creating EBiz instance -->", e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

    }

    public static EBiz getEBizInstance() {
        return INSTANCE;
    }

}
