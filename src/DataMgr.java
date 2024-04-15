import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataMgr {
    private static final String INVENTORY_DB_NAME = "Inventory";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";

    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static Connection inventoryConnection;

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getInventoryConnection() {
        if (inventoryConnection == null) {
            try {
                inventoryConnection = DriverManager.getConnection(DB_URL + INVENTORY_DB_NAME, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return inventoryConnection;
    }    
}
