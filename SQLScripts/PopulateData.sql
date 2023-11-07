/*
This script is developed by Geethakrishna Puligundla
mail: gpuligu@clemson.edu

This script includes populating data into tables which are created in CreateTables.sql
All copyrights are Reserved.
*/

-- Database Schema
use Pizzeria;

-- Populating Topping Table
INSERT INTO topping(ToppingName, ToppingPrice, ToppingCTC, ToppingOnPersonal, ToppingOnMedium, ToppingOnLarge, ToppingOnXLarge, ToppingCurInventory, ToppingMinInventory) 
VALUES
  ('Pepperoni', 1.25, 0.2, 2, 2.75, 3.5, 4.5, 100, 50),
  ('Sausage', 1.25, 0.15, 2.5, 3, 3.5, 4.25, 100, 50),
  ('Ham', 1.5, 0.15,  2, 2.5, 3.25, 4, 78, 25),
  ('Chicken', 1.75, 0.25,1.5, 2, 2.25, 3, 56, 25),
  ('Green Pepper', 0.5, 0.02, 1, 1.5, 2, 2.5, 79, 25),
  ('Onion', 0.5, 0.02, 1, 1.5, 2, 2.75, 85, 25),
  ('Roma Tomato', 0.75, 0.03,  2, 3, 3.5, 4.5, 86, 10),
  ('Mushrooms', 0.75, 0.1,1.5, 2, 2.5, 3, 52, 50),
  ('Black Olives', 0.6, 0.1,0.75, 1, 1.5, 2,  39, 25),
  ('Pineapple', 1, 0.25, 1, 1.25, 1.75, 2, 15, 0),
  ('Jalapenos', 0.5, 0.05, 0.5, 0.75, 1.25, 1.75, 64, 0),
  ('Banana Peppers', 0.5, 0.05, 0.6, 1, 1.3, 1.75, 36, 0),
  ('Regular Cheese', 0.5, 0.12,  2, 3.5, 5, 7, 250, 50),
  ('Four Cheese Blend', 1, 0.15, 2, 3.5, 5, 7, 150, 25),
  ('Feta Cheese', 1.5, 0.18, 1.75, 3, 4, 5.5, 75, 0),
  ('Goat Cheese', 1.5, 0.2, 1.6, 2.75, 4, 5.5, 54, 0),
  ('Bacon', 1.5, 0.25, 1, 1.5, 2, 3, 89, 0);

-- Populating Discount Table
INSERT INTO discount (DiscountName, DiscountPercentOff, DiscountDollarOff) 
VALUES
('Employee', 15, NULL),
('Lunch Special Medium', NULL, 1.00),
('Lunch Special Large', NULL, 2.00),
('Specialty Pizza', NULL, 1.50),
('Happy Hour', 10, NULL),
('Gameday Special', 20, NULL);

-- Populating pizzabase Table
INSERT INTO pizzabase (PizzaBaseCrustType, PizzaBaseSize, PizzaBasePrice, PizzaBaseCTC) 
VALUES
('Thin', 'Small', 3, 0.5),
('Original', 'Small',  3, 0.75),
('Pan', 'Small', 3.5, 1),
('Gluten-Free', 'Small', 4, 2),
('Thin', 'Medium', 5, 1),
('Original', 'Medium', 5, 1.5),
('Pan', 'Medium', 6, 2.25),
('Gluten-Free', 'Medium', 6.25, 3),
('Thin', 'Large', 8, 1.25),
('Original', 'Large', 8, 2),
('Pan', 'Large', 9, 3),
('Gluten-Free', 'Large', 9.5, 4),
('Thin', 'XLarge', 10, 2),
('Original', 'XLarge', 10, 3),
('Pan', 'XLarge', 11.5, 4.5),
('Gluten-Free', 'XLarge', 12.5, 6);

-- Populating customer Table
INSERT INTO customer(CustomerName, CustomerPhone, CustomerAddress, CustomerCity, CustomerState, CustomerZipCode) 
VALUES 
('Andrew Wilkes-Krier', 8642545861, '115 Part Blvd', 'Anderson', 'SC', 29621),
('Matt Engers', 8644749953, NULL, NULL, NULL, NULL),
('Frank Turner', 8642328944, '6745 Wessex St', 'Anderson', 'SC', 29621),
('Milo Auckerman', 8648785679, '8879 Suburban Home', 'Anderson', 'SC', 29621);


-- Populating orderhistory Table
INSERT INTO orderhistory(OrderDateTime, OrderPrice, OrderCTC, OrderType, OrderIsReady) 
VALUES 
('2023-03-05 12:03:15', 20.75 ,3.68, "dine-in" ,1),
('2023-04-03 12:05:11', 19.78, 4.63, "dine-in",  1),
('2023-03-03 09:30:00', 89.28, 19.8, "pickup", 1),
('2023-04-20 19:11:22', 86.19, 23.62, "home-delivery", 1),
('2023-03-02 17:30:00', 27.45, 7.88, "pickup", 1),
('2023-03-02 18:17:00', 25.81, 4.24, "home-delivery", 1),
('2023-04-13 20:32:11', 37.25, 6, "home-delivery", 1);

-- Populating pizza Table
INSERT INTO pizza(PizzaCrustType, PizzaSize, PizzaPrice, PizzaCTC, PizzaIsReady, PizzaOrderID, PizzaBasePriceID) 
VALUES 
('Thin', 'Large', 20.75,3.68, 1, 1, 9),
('Pan', 'Medium', 12.85, 3.23, 1, 2, 7),
('Original', 'Small', 6.93, 1.40, 1, 2, 2),
('Original', 'Large', 14.88, 3.30, 1, 3, 10),
('Original', 'Large', 14.88, 3.30, 1, 3, 10),
('Original', 'Large', 14.88, 3.30, 1, 3, 10),
('Original', 'Large', 14.88, 3.30, 1, 3, 10),
('Original', 'Large', 14.88, 3.30, 1, 3, 10),
('Original', 'Large', 14.88, 3.30, 1, 3, 10),
('Original', 'XLarge', 27.94, 9.19, 1, 4, 14),
('Original', 'XLarge', 31.50, 6.25, 1, 4, 14),
('Original', 'XLarge', 26.75, 8.18, 1, 4, 14),
('Gluten-Free', 'XLarge', 27.45, 7.88, 1, 5, 16),
('Thin', 'Large', 25.81, 4.24, 1, 6, 9),
('Thin', 'Large', 18.00, 2.75, 1, 7, 9),
('Thin', 'Large', 19.25, 3.25, 1, 7, 9);


-- Populating pizzatopping Table
INSERT INTO pizzatopping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingIsDouble)
VALUES 
(1, 13, 1), (1, 1, 0), (1, 2, 0), (2, 9, 0), (2, 15,0),
(2, 7, 0), (2, 8, 0), (2, 12, 0), (3, 4, 0), (3, 12, 0),
(3, 13,0), (4, 1, 0), (4, 13, 0), (5, 1, 0), (5, 13, 0),
(6, 1, 0), (6, 13, 0), (7, 1, 0), (7, 13, 0), (8, 1, 0),
(8, 13, 0), (9, 1, 0), (9, 13, 0), (10, 1, 0), (10, 2, 0),
(10, 14, 0), (11, 3, 1), (11, 10, 1), (11, 14, 0), (12, 4, 0),
(12, 17, 0), (12, 14, 0), (13, 5, 0), (13, 6, 0), (13, 7, 0),
(13, 8, 0), (13, 9, 0), (13, 16, 0), (14, 4, 0), (14, 5, 0),
(14, 6, 0), (14, 8, 0), (14, 14, 1), (15, 14, 1), (16, 1, 1),
(16, 13, 0);

-- Populating orderdiscount Table
INSERT INTO orderdiscount(OrderDiscountOrderID, OrderDiscountDiscountID)
VALUES 
(4, 6), 
(7,1);

-- Populating pizzadiscount Table
INSERT INTO pizzadiscount(PizzaDiscountPizzaID, PizzaDiscountDiscountID) 
VALUES 
(1, 3),
(2, 2),
(2, 4),
(11, 4),
(13, 4);

-- Populating dinein Table
INSERT INTO dinein(DineInID, DineInTableNum) 
VALUES 
(1, 21),
(2, 4);

-- Populating pickup Table
INSERT INTO pickup(PickupID, PickupCustomerID) 
VALUES 
(3, 1), (5, 2);

-- Populating homedelivery Table
INSERT INTO homedelivery(HomeDeliveryID, HomeDeliveryCustomerID) 
VALUES
(4, 1), 
(6, 3), 
(7, 4);