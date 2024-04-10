import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryManagerDAL {

    private Connection getMySQLConnection(String databaseName, String user, String password)
    {
        try
        {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName, user, password);
        } 
        catch (SQLException exception)
        {
            System.out.println("Failed to connect to the database" + exception.getMessage());
            return null;
        }
    }

    public InventoryManagerDAL(String databaseName, String username, String password) {
        getMySQLConnection(databaseName, username, password);
    }


     // Method to search inventory based on filters
    public ResultSet searchInventory(String databaseName, String user, String password, int dangerLevelFilter, String magicTypeFilter, BigDecimal maxPriceFilter) throws SQLException {
        Connection myConnection = getMySQLConnection(databaseName, user, password);
        String sql = "SELECT itemName, quantity, itemDescription, associatedMagic, dangerLevel, pricePaid, priceSelling " +
                     "FROM Item " +
                     "WHERE (? = 0 OR dangerLevel = ?) " +
                     "AND (? IS NULL OR associatedMagic = ?) " +
                     "AND (? = 0 OR priceSelling <= ?)";
        PreparedStatement statement = myConnection.prepareStatement(sql);
        statement.setInt(1, dangerLevelFilter);
        statement.setInt(2, dangerLevelFilter);
        statement.setString(3, magicTypeFilter);
        statement.setString(4, magicTypeFilter);
        statement.setBigDecimal(5, maxPriceFilter);
        statement.setBigDecimal(6, maxPriceFilter);
        return statement.executeQuery();
    }

    // Method to insert a new inventory item
    public boolean addNewItem(String databaseName, String user, String password, String itemName, int itemQuantity, String itemDescription, String itemMagic, int itemDangerLevel, BigDecimal itemPricePaid, BigDecimal itemPriceSelling) {
        Connection myConnection = getMySQLConnection(databaseName, user, password);
        try {
            CallableStatement addNewItemProcedure = myConnection.prepareCall("{CALL addNewItem(?, ?, ?, ?, ?, ?, ?)}");
            addNewItemProcedure.setString(1, itemName);
            addNewItemProcedure.setInt(2, itemQuantity);
            addNewItemProcedure.setString(3, itemDescription);
            addNewItemProcedure.setString(4, itemMagic);
            addNewItemProcedure.setInt(5, itemDangerLevel);
            addNewItemProcedure.setBigDecimal(6, itemPricePaid);
            addNewItemProcedure.setBigDecimal(7, itemPriceSelling);
            addNewItemProcedure.execute();
            return true;
        } catch (SQLException ex) {
            System.out.println("Failed to add new item: " + ex.getMessage());
            return false;
        }
    }

    public void salesReport(String databaseName, String username, String password, int month) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        try {
            // Establish connection
            connection = getMySQLConnection(databaseName, username, password);
    
            // SQL query to retrieve sales data for the specified month
            String query = "SELECT itemName, CustomerName, saleprice, purchasedate FROM sales WHERE MONTH(purchasedate) = ?";
            System.out.println("Executing query: " + query); // Debug statement
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, month);
    
            // Execute the query
            resultSet = preparedStatement.executeQuery();
    
            // Print report header
            System.out.println("Sales Report for Month " + month + ":");
            System.out.println("---------------------------------------------------");
            System.out.printf("%-20s %-20s %-20s %-15s%n", "Item Name", "Customer", "Sale Date", "Price");
            System.out.println("---------------------------------------------------");
    
            // Write sales data to the console
            while (resultSet.next()) {
                String itemName = resultSet.getString("itemName");
                String purchaser = resultSet.getString("CustomerName");
                java.sql.Timestamp saleDate = resultSet.getTimestamp("purchasedate");
                double price = resultSet.getDouble("saleprice");
    
                // Format sale date as string
                String saleDateString = saleDate.toString();
    
                // Print sales data
                System.out.printf("%-20s %-20s %-20s %-15.2f%n", itemName, purchaser, saleDateString, price);
            }
    
            System.out.println("Sales report generated successfully."); // Debug statement
        } catch (SQLException e) {
            System.out.println("Failed to generate sales report: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Failed to close resources: " + e.getMessage());
            }
        }
    }

}