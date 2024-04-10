drop database if exists ims;
create DATABASE ims;

use ims;
create table if not exists Item (
itemname varchar(100) not null,
quantity int,
itemDescription text,
associatedMagic varchar(100),
dangerLevel int check (dangerLevel Between 1 and 5),
pricePaid DECIMAL (10, 2),
priceSelling DECIMAL (10, 2),
primary key (itemName)
);

create table if not exists Customer (
customerId int not null auto_increment,
customerName varchar(100),
age int,
PRIMARY Key (customerId)
);

create table if not exists sale (
saleId int not null auto_increment,
price DECIMAL (10, 2),
saleDate DATE,
customerId int,
itemName varchar(100),
primary key (saleId),
foreign key (customerId) References Customer(customerId),
foreign key (itemName) References Item(itemName)
);


INSERT INTO Item (itemName, quantity, itemDescription, associatedMagic, dangerLevel, pricePaid, priceSelling) 
VALUES ('Magic Wand', 10, 'A powerful wand imbued with magic', 'Wizardry', 3, 50.00, 100.00),
       ('Fire Staff', 5, 'A staff capable of casting fire spells', 'Fire', 4, 80.00, 150.00),
       ('Ice Amulet', 3, 'An amulet that grants protection against ice attacks', 'Ice', 2, 40.00, 80.00);


INSERT INTO Customer (customerName, age)
VALUES ('John Doe', 25),
       ('Jane Smith', 30),
       ('Alice Johnson', 18);
       
INSERT INTO Sale (price, saleDate, customerId, itemName)
VALUES (100.00, '2024-04-01', 1, 'Magic Wand'),
       (150.00, '2024-04-02', 2, 'Fire Staff'),
       (80.00, '2024-04-03', 3, 'Ice Amulet');



DELIMITER $$
CREATE PROCEDURE searchInventory (
    IN dangerLevelFilter INT,
    IN magicTypeFilter VARCHAR(50),
    IN maxPriceFilter DECIMAL(10, 2)
)
BEGIN
    SELECT itemName, quantity, itemDescription, associatedMagic, dangerLevel, pricePaid, priceSelling
    FROM Item
    WHERE (dangerLevelFilter = 0 OR dangerLevel = dangerLevelFilter)
    AND (magicTypeFilter IS NULL OR associatedMagic = magicTypeFilter)
    AND (maxPriceFilter = 0 OR priceSelling <= maxPriceFilter);
END $$
DELIMITER ;

DELIMITER //

CREATE PROCEDURE addNewItem (
    IN myItemName VARCHAR(100),
    IN myQuantity INT,
    IN myItemDescription VARCHAR(255),
    IN myAssociatedMagic VARCHAR(50),
    IN myDangerLevel INT,
    IN myPricePaid DECIMAL(10, 2),
    IN myPriceSelling DECIMAL(10, 2)
)
BEGIN
    INSERT INTO Item (name, quantity, description, magic, danger_level, price_paid, price_sell)
    VALUES (myItemName, myQuantity, myItemDescription, myAssociatedMagic, myDangerLevel, myPricePaid, myPriceSelling);
END //

DELIMITER ;

DELIMITER $$

CREATE TRIGGER before_insert_sale
BEFORE INSERT ON sale
FOR EACH ROW
BEGIN
    DECLARE customer_age INT;
    DECLARE item_danger_level INT;

    -- Get the age of the customer
    SELECT Age INTO customer_age
    FROM Customer
    WHERE customerId = NEW.customerId;

    -- Get the danger level of the item
    SELECT Danger_Level INTO item_danger_level
    FROM Item
    WHERE Name = NEW.itemName;

    -- Check if the danger level exceeds 2 and customer age is less than 18
    IF item_danger_level > 2 AND customer_age < 18 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Sale not allowed due to danger level restrictions';
    END IF;
END $$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE check_item_existence(
    IN item_name VARCHAR(255),
    OUT item_exists BOOLEAN
)
BEGIN
    DECLARE item_count INT;
    
    SELECT COUNT(*) INTO item_count
    FROM inventory
    WHERE name = item_name;
    
    IF item_count > 0 THEN
        SET item_exists := TRUE;
    ELSE
        SET item_exists := FALSE;
    END IF;
END $$

DELIMITER ;