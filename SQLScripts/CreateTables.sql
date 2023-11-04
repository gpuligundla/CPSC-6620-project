/*
This script is developed by Geethakrishna Puligundla
mail: gpuligu@clemson.edu

This script includes creation of tables which are designed in the ERD
All copyrights are Reserved.
*/

-- Database Schema
DROP SCHEMA IF EXISTS Pizzeria;
CREATE SCHEMA Pizzeria;
use Pizzeria;

-- TOPPINGS Table
CREATE TABLE topping (
    ToppingID INT AUTO_INCREMENT,
    ToppingName VARCHAR(255) NOT NULL UNIQUE,
    ToppingPrice DECIMAL(10 , 2 ) NOT NULL,
    ToppingCTC DECIMAL(10 , 2 ) NOT NULL,
    ToppingOnPersonal DECIMAL(4 , 2 ) NOT NULL,
    ToppingOnMedium DECIMAL(4 , 2 ) NOT NULL,
    ToppingOnLarge DECIMAL(4 , 2 ) NOT NULL,
    ToppingOnXLarge DECIMAL(4 , 2 ) NOT NULL,
    ToppingCurInventory INT,
    ToppingMinInventory INT,
    CONSTRAINT Topping_PK PRIMARY KEY (ToppingID)
);

-- PIZZA_BASE_PRICE Table
CREATE TABLE pizzabase (
    PizzaBasePriceID INT AUTO_INCREMENT,
    PizzaBaseCrustType VARCHAR(255) NOT NULL,
    PizzaBaseSize VARCHAR(255) NOT NULL,
    PizzaBasePrice DECIMAL(10 , 2 ) NOT NULL,
    PizzaBaseCTC DECIMAL(10 , 2 ) NOT NULL,
    CONSTRAINT PizzaBase_PK PRIMARY KEY (PizzaBasePriceID)
);

-- CUSTOMERS Table
CREATE TABLE customer (
    CustomerID INT AUTO_INCREMENT,
    CustomerName VARCHAR(255) NOT NULL,
    CustomerPhone DECIMAL(10) NOT NULL,
    CustomerAddress VARCHAR(255),
    CustomerCity VARCHAR(255),
    CustomerState VARCHAR(255),
    CustomerZipCode VARCHAR(255),
    CONSTRAINT Customer_PK PRIMARY KEY (CustomerID)
);

-- ORDERS Table
CREATE TABLE orderhistory (
    OrderID INT AUTO_INCREMENT,
    OrderDateTime DATETIME NOT NULL,
    OrderPrice DECIMAL(10 , 2 ) NOT NULL,
    OrderCTC DECIMAL(10 , 2 ) NOT NULL,
    OrderIsReady BOOLEAN DEFAULT 0,
    CONSTRAINT Order_PK PRIMARY KEY (OrderID)
);

-- PIZZA Table
CREATE TABLE pizza (
    PizzaID INT AUTO_INCREMENT,
    PizzaCrustType VARCHAR(255) NOT NULL,
    PizzaSize  VARCHAR(255) NOT NULL,
    PizzaPrice DECIMAL(10,2) NOT NULL,
    PizzaCTC DECIMAL(10,2) NOT NULL,
    PizzaIsReady BOOLEAN DEFAULT 0,
    PizzaOrderID INT,
    PizzaBasePriceID INT,
    CONSTRAINT Pizza_PK PRIMARY KEY(PizzaID),
    CONSTRAINT PizzaOrderID_FK FOREIGN KEY(PizzaOrderID) REFERENCES orderhistory(OrderID)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PizzaBasePriceID_FK FOREIGN KEY(PizzaBasePriceID) REFERENCES pizzabase(PizzaBasePriceID)
    ON DELETE CASCADE ON UPDATE CASCADE
);

-- PIZZA_TOPPINGS Table
CREATE TABLE pizzatopping (
    PizzaToppingPizzaID INT,
    PizzaToppingToppingID INT,
    PizzaToppingIsDouble BOOLEAN DEFAULT 0,
    CONSTRAINT PizzaTopping_PK PRIMARY KEY (PizzaToppingPizzaID , PizzaToppingToppingID),
    CONSTRAINT PizzaToppingPizzaID_FK FOREIGN KEY (PizzaToppingPizzaID)
        REFERENCES pizza (PizzaID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PizzaToppingToppingID_FK FOREIGN KEY (PizzaToppingToppingID)
        REFERENCES topping (ToppingID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- DISCOUNT Table
CREATE TABLE discount (
    DiscountID INT AUTO_INCREMENT,
    DiscountName VARCHAR(255) NOT NULL,
    DiscountPercentOff DECIMAL(10 , 2 ),
    DiscountDollarOff DECIMAL(10 , 2 ),
    CONSTRAINT Discount_PK PRIMARY KEY (DiscountID)
);

-- PIZZA_DISCOUNT Table
CREATE TABLE pizzadiscount (
    PizzaDiscountPizzaID INT,
    PizzaDiscountDiscountID INT,
    CONSTRAINT PizzaDiscount_PK PRIMARY KEY (PizzaDiscountPizzaID , PizzaDiscountDiscountID),
    CONSTRAINT PizzaDiscountPizzaID_FK FOREIGN KEY (PizzaDiscountPizzaID)
        REFERENCES pizza (PizzaID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PizzaDiscountDiscountID_FK FOREIGN KEY (PizzaDiscountDiscountID)
        REFERENCES discount (DiscountID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ORDERS_DISCOUNT Table
CREATE TABLE orderdiscount (
    OrderDiscountOrderID INT,
    OrderDiscountDiscountID INT,
    CONSTRAINT OrderDiscount_PK PRIMARY KEY(OrderDiscountOrderID, OrderDiscountDiscountID),
    CONSTRAINT OrderDiscountOrderID_FK FOREIGN KEY(OrderDiscountOrderID) REFERENCES orderhistory(OrderID)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT OrderDiscountDiscountID_FK FOREIGN KEY(OrderDiscountDiscountID) REFERENCES discount(DiscountID)
    ON DELETE CASCADE ON UPDATE CASCADE
);

/* ------sub type tables from order table ------
here i'm using table-per-class pattern to represent the hirerachy of tables.
ref: https://stackoverflow.com/questions/3579079/how-can-you-represent-inheritance-in-a-database
*/
CREATE TABLE dinein (
    DineInID INT,
    DineInTableNum INT,
    CONSTRAINT Dinein_PK PRIMARY KEY (DineInID),
    CONSTRAINT Dinein_FK FOREIGN KEY (DineInID)
        REFERENCES orderhistory (OrderID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- PICKUP_CUSTOMER Table
CREATE TABLE pickup (
    PickupID INT,
    PickupCustomerID INT,
    CONSTRAINT Pickup_PK PRIMARY KEY (PickupID),
    CONSTRAINT Pickup_FK FOREIGN KEY (PickupID)
        REFERENCES orderhistory (OrderID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PickupCustomerID_FK FOREIGN KEY (PickupCustomerID)
        REFERENCES customer (CustomerID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- DELIVERY_CUSTOMER Table
CREATE TABLE homedelivery (
    HomeDeliveryID INT,
    HomeDeliveryCustomerID INT,
    CONSTRAINT HomeDelivery_PK PRIMARY KEY (HomeDeliveryID),
    CONSTRAINT HomeDelivery_FK FOREIGN KEY (HomeDeliveryID)
        REFERENCES orderhistory (OrderID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT HomeDeliveryCustomerID_FK FOREIGN KEY (HomeDeliveryCustomerID)
        REFERENCES customer (CustomerID)
        ON DELETE CASCADE ON UPDATE CASCADE
);