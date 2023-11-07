/*
This script is developed by Geethakrishna Puligundla
mail: gpuligu@clemson.edu

This script includes creation of views for finding reports
All copyrights are Reserved.
*/

-- Database Schema
use Pizzeria;


-- Topping Popularity View
CREATE OR REPLACE VIEW ToppingPopularity AS
SELECT ToppingName, count(pt.PizzaToppingToppingID)+ COALESCE(SUM(PizzaToppingIsDouble), 0) AS ToppingCount
FROM topping t left join pizzatopping pt ON pt.PizzaToppingToppingID=t.ToppingID
group by ToppingName
Order BY 2 DESC;
-- Display View
SELECT * FROM ToppingPopularity;

-- Pizza Profits View
CREATE OR REPLACE VIEW ProfitByPizza AS
SELECT PizzaSize AS Size, PizzaCrustType AS Crust, SUM(PizzaPrice)-SUM(PizzaCTC) AS "Profit", 
CONCAT(month(OrderDateTime), "/", year(OrderDateTime)) AS "Order Month"
FROM pizza p JOIN orderhistory O ON p.PizzaOrderID=O.OrderID
GROUP BY PizzaSize, PizzaCrustType
ORDER BY 3;
-- Display View
SELECT * FROM ProfitByPizza;

-- Order Type Profits View
CREATE OR REPLACE VIEW ProfitByOrderType AS
SELECT OrderType AS CustomerType, CONCAT(month(OrderDateTime), "/", year(OrderDateTime)) AS "Order Month", 
SUM(OrderPrice) AS TotalOrderPrice, SUM(OrderCTC) AS TotalOrderCost, SUM(OrderPrice)-SUM(OrderCTC) AS Profit
FROM orderhistory
GROUP BY OrderType
UNION
SELECT "", "Grand Total", SUM(OrderPrice), SUM(OrderCTC), SUM(OrderPrice)-SUM(OrderCTC)
FROM orderhistory
ORDER BY 5,1;
-- Display View
SELECT * FROM ProfitByOrderType;