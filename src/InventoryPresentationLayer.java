import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InventoryPresentationLayer {
    private static InventoryManagerDAL GetDal() {
        Scanner credentialScanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        // String input
        String userName = credentialScanner.nextLine();
        System.out.print("Enter password: ");
        String password = credentialScanner.nextLine();
        return new InventoryManagerDAL("Inventory", userName, password);
    }

    public static void main(String[] args) {
            // Get database connection details from InventoryPresentationLayer
            InventoryManagerDAL inventoryDAL = InventoryPresentationLayer.GetDal();

            // Scanner for user input
            Scanner scanner = new Scanner(System.in);


        int choice;
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add item to inventory");
            System.out.println("2. Search inventory");
            System.out.println("3. Save search results to file");
            System.out.println("4. Generate Sales Report");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    // Add item to inventory
                    addItemToInventory(inventoryDAL, scanner);
                    break;
                case 2:
                    // Search inventory
                    searchInventory(inventoryDAL, scanner);
                    break;
                case 3:
                    // Save search results to file
                    saveSearchResultsToFile(inventoryDAL, scanner);
                    break;
                case 4:
                    // Generate Sales Report
                    generateSalesReport(inventoryDAL, scanner);
                    break;
                case 5:
                    // Exit
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
   
    

    private static void addItemToInventory(InventoryManagerDAL dal, Scanner scanner) {
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter item quantity: ");
        int itemQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter item description: ");
        String itemDescription = scanner.nextLine();
        System.out.print("Enter item magic type: ");
        String itemMagic = scanner.nextLine();
        System.out.print("Enter item danger level: ");
        int itemDangerLevel = scanner.nextInt();
        System.out.print("Enter item price paid: ");
        BigDecimal itemPricePaid = scanner.nextBigDecimal();
        System.out.print("Enter item price selling: ");
        BigDecimal itemPriceSelling = scanner.nextBigDecimal();
        scanner.nextLine(); // Consume newline
        
        // Call DAL method to add item to inventory
        boolean success = dal.addNewItem("Inventory", "username", "password", itemName, itemQuantity, itemDescription, itemMagic, itemDangerLevel, itemPricePaid, itemPriceSelling);
        if (success) {
            System.out.println("Item added to inventory successfully.");
        } else {
            System.out.println("Failed to add item to inventory.");
        }
    }

    private static void searchInventory(InventoryManagerDAL dal, Scanner scanner) {
        System.out.print("Enter danger level filter: ");
        int dangerLevelFilter = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter magic type filter: ");
        String magicTypeFilter = scanner.nextLine();
        System.out.print("Enter max price filter: ");
        BigDecimal maxPriceFilter = scanner.nextBigDecimal();
        scanner.nextLine(); // Consume newline
        
        try {
            // Call DAL method to search inventory
            ResultSet resultSet = dal.searchInventory("Inventory", "username", "password", dangerLevelFilter, magicTypeFilter, maxPriceFilter);
            
            // Display search results
            // Print header
            System.out.println("Inventory Items:");
            System.out.println("---------------------------------------------------");
            System.out.printf("%-20s %-20s %-20s %-15s%n", "Item Name", "Quantity", "Description", "Price Selling");
            System.out.println("---------------------------------------------------");

        // Print inventory items
            while (resultSet.next()) {
                String itemName = resultSet.getString("itemName");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("itemDescription");
                BigDecimal priceSelling = resultSet.getBigDecimal("priceSelling");
                
                System.out.printf("%-20s %-20d %-20s %-15.2f%n", itemName, quantity, description, priceSelling);
            }
        } catch (SQLException e) {
        System.out.println("Failed to search inventory: " + e.getMessage());
        }
    }

    private static void saveSearchResultsToFile(InventoryManagerDAL dal, Scanner scanner) {
        // Prompt user for filename
        System.out.print("Enter filename to save search results: ");
        String filename = scanner.nextLine();

        try (FileWriter writer = new FileWriter(filename)) {
            // Call DAL method to search inventory
            ResultSet resultSet = dal.searchInventory("Inventory", "username", "password", 0, null, BigDecimal.ZERO);

            // Write search results to file
            while (resultSet.next()) {
                String itemName = resultSet.getString("itemName");
                int quantity = resultSet.getInt("quantity");
                String itemDescription = resultSet.getString("itemDescription");
                String associatedMagic = resultSet.getString("associatedMagic");
                int dangerLevel = resultSet.getInt("dangerLevel");
                double pricePaid = resultSet.getDouble("pricePaid");
                double priceSelling = resultSet.getDouble("priceSelling");

                // Format the data and write to file
                writer.write("Item Name: " + itemName + "\n");
                writer.write("Quantity: " + quantity + "\n");
                writer.write("Description: " + itemDescription + "\n");
                writer.write("Magic: " + associatedMagic + "\n");
                writer.write("Danger Level: " + dangerLevel + "\n");
                writer.write("Price Paid: " + pricePaid + "\n");
                writer.write("Price Selling: " + priceSelling + "\n\n");
            }

            System.out.println("Search results saved to file: " + filename);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    
    }


    private static void generateSalesReport(InventoryManagerDAL dal, Scanner scanner) {
        // Prompt user for month
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Call DAL method to generate sales report
        dal.salesReport("Inventory", "username", "password", month);
    }
}
