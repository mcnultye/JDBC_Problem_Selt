import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class InventoryBusinessLogic {
    // Method to save search results to a text file
    public void saveSearchResultsToFile(ResultSet resultSet, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Iterate over the result set and write each item to the file
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                // Retrieve other columns as needed...

                // Write item details to the file
                writer.write("Name: " + name + ", Quantity: " + quantity + ", Description: " + description + "\n");
            }
            System.out.println("Search results saved to file: " + filePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

     public void generateSalesReport(ResultSet salesResultSet, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("Sales Report\n");
            //writer.write("Date: " + new Date() "\n\n");
            writer.write("Item Name, Customer Name, Sale Date, Sale Price, Magic Type, Danger Level\n");

            // Write sales data
            while (salesResultSet.next()) {
                String itemName = salesResultSet.getString("itemName");
                String customerName = salesResultSet.getString("customerName");
                String saleDate = salesResultSet.getString("saleDate");
                double salePrice = salesResultSet.getDouble("price");
                String magicType = salesResultSet.getString("magicType");
                int dangerLevel = salesResultSet.getInt("dangerLevel");

                // Write sales information to file
                writer.write(itemName + ", " + customerName + ", " + saleDate + ", " + salePrice + ", " + magicType + ", " + dangerLevel + "\n");
            }
            System.out.println("Sales report generated successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to generate sales report.");
        }
    }
}
