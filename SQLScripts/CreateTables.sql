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
    ToppingID INT,
    ToppingName VARCHAR(255),
    ToppingPrice DECIMAL(10,2),
    ToppingCTC DECIMAL(10,2),
    ToppingOnPersonal DECIMAL(10,2),
    ToppingOnMedium DECIMAL(10,2),
    ToppingOnLarge DECIMAL(10,2),
    ToppingOnXLarge DECIMAL(10,2),
    ToppingMinInventory INT,
    TOPPINGCurInventory INT,
    PRIMARY KEY(ToppingID)
);

-- PIZZA_BASE_PRICE Table
CREATE TABLE pizza_base (
    PizzaBasePriceID INT,
    PizzaBaseCrustType VARCHAR(255) NOT NULL,
    PizzaBaseSize  VARCHAR(255) NOT NULL,
    PizzaBasePrice DECIMAL(10,2),
    PizzaBaseCTC DECIMAL(10,2),
    PRIMARY KEY(PizzaBasePriceID)
);

-- CUSTOMERS Table
CREATE TABLE customer (
    CustomerID INT,
    CustomerName VARCHAR(255),
    CustomerPhone DECIMAL(10),
    CustomerAddress VARCHAR(255),
    CustomerCity VARCHAR(255),
    CustomerState VARCHAR(255),
    CustomerZipCode VARCHAR(255),
    PRIMARY KEY(CustomerID)
);

-- ORDERS Table
CREATE TABLE order (
    OrderID INT,
    OrderDateTime DATETIME,
    OrderDeliveryType VARCHAR(255),
    OrderPrice DECIMAL(10,2),
    OrderCTC DECIMAL(10,2),
    OrderState BOOLEAN,
    PRIMARY KEY(OrderID)
);

-- PIZZA Table
CREATE TABLE pizza (
    PizzaID INT,
    PizzaCrustType VARCHAR(255) NOT NULL,
    PizzaSize  VARCHAR(255) NOT NULL,
    PizzaPrice DECIMAL(10,2),
    PizzaCTC DECIMAL(10,2),
    PizzaState BOOLEAN,
    PizzaOrderID INT,
    PizzaBasePriceID INT,
    PRIMARY KEY(PizzaID),
    FOREIGN KEY(PizzaOrderID) REFERENCES order(OrderID),
    FOREIGN KEY(PizzaBasePriceID) REFERENCES pizza_base(PizzaBasePriceID)
);

-- PIZZA_TOPPINGS Table
CREATE TABLE pizza_topping (
    PizzaToppingPizzaID INT,
    PizzaToppingToppingID INT,
    PizzaToppingDouble BOOLEAN,
    PRIMARY KEY(PizzaToppingPizzaID, PizzaToppingToppingID),
    FOREIGN KEY(PizzaToppingPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzaToppingToppingID) REFERENCES topping(ToppingID)
);

-- DISCOUNT Table
CREATE TABLE discount (
    DiscountID INT,
    DiscountName VARCHAR(255),
    DiscountPercentOff DECIMAL(10,2),
    DiscountDollarOff DECIMAL(10,2),
    PRIMARY KEY(DiscountID)
);

-- PIZZA_DISCOUNT Table
CREATE pizza_discount (
    PizzaDiscountPizzaID INT,
    PizzaDiscountDiscountID INT,
    PRIMARY KEY(PizzaDiscountPizzaID, PizzaDiscountDiscountID),
    FOREIGN KEY(PizzaDiscountPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(PizzaDiscountDiscountID) REFERENCES discount(DiscountID)
);

-- ORDERS_DISCOUNT Table
CREATE order_discount (
    OrderDiscountOrderID INT,
    OrderDiscountDiscountID INT,
    PRIMARY KEY(OrderDiscountOrderID, OrderDiscountDiscountID),
    FOREIGN KEY(OrderDiscountOrderID) REFERENCES order(OrderID),
    FOREIGN KEY(OrderDiscountDiscountID) REFERENCES discount(DiscountID)
);

-------- sub type tables from order table --------
-- DINE_IN_CUSTOMER Table

-- PICKUP_CUSTOMER Table

-- DELIVERY_CUSTOMER Table
