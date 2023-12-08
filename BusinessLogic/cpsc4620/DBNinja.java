package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "home-delivery";
	public final static String dine_in = "dine-in";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		String orderQuery = "INSERT INTO orderhistory(OrderDateTime, OrderPrice, OrderCTC, OrderType, OrderIsReady, customerID) VALUES(?, ?, ?, ?, ?, ?);";
		PreparedStatement orderPrepStatement = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
		orderPrepStatement.setString(1, o.getDate());
		orderPrepStatement.setDouble(2, o.getCustPrice());
		orderPrepStatement.setDouble(3, o.getBusPrice());
		orderPrepStatement.setString(4, o.getOrderType());
		orderPrepStatement.setInt(5, o.getIsComplete());
		orderPrepStatement.setInt(6, o.getCustID());
		orderPrepStatement.executeUpdate();
		ResultSet generatedKeys = orderPrepStatement.getGeneratedKeys();
		if (generatedKeys.next()) {
			int generatedOrderKey = generatedKeys.getInt(1);
			o.setOrderID(generatedOrderKey);
		}
		if(o instanceof PickupOrder){
			String pickupOrderQuery = "INSERT INTO pickup(PickupID, PickupCustomerID, isPickedUp) VALUES(?, ?, ?);";
			PreparedStatement pickupPrepStatement = conn.prepareStatement(pickupOrderQuery);
			pickupPrepStatement.setInt(1, o.getOrderID());
			pickupPrepStatement.setInt(2, o.getCustID());
			pickupPrepStatement.setInt(3, 0);
			pickupPrepStatement.execute();
		}
		else if (o instanceof DeliveryOrder) {
			String homeDelOrderQuery = "INSERT INTO homedelivery(HomeDeliveryID, HomeDeliveryCustomerID) VALUES(?, ?);";
			PreparedStatement homeDelPrepStatement = conn.prepareStatement(homeDelOrderQuery);
			homeDelPrepStatement.setInt(1, o.getOrderID());
			homeDelPrepStatement.setInt(2, o.getCustID());
			homeDelPrepStatement.execute();
		}
		else{
			String dineinOrderQuery = "INSERT INTO dinein(DineInID, DineInTableNum) VALUES(?, ?);";
			PreparedStatement dineinPrepStatement = conn.prepareStatement(dineinOrderQuery);
			dineinPrepStatement.setInt(1, o.getOrderID());
			dineinPrepStatement.setInt(2, ((DineinOrder) o).getTableNum());
			dineinPrepStatement.execute();
		}
		conn.close();
	}
	public static void updateOrderPrice(Order o, double custPrice, double busPrice) throws SQLException, IOException{
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		String query = "UPDATE orderhistory SET OrderPrice=?, OrderCTC=? WHERE OrderID=?;";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setDouble(1, custPrice);
		prepStatement.setDouble(2, busPrice);
		prepStatement.setInt(3, o.getOrderID());
		prepStatement.executeUpdate();
		conn.close();

	}
	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		int pizzaBasePriceID=-1;
		String basePriceQuery = "SELECT PizzaBasePriceID FROM pizzabase WHERE PizzaBaseCrustType=? AND PizzaBaseSize=?;";
		PreparedStatement basePricePrepStatement = conn.prepareStatement(basePriceQuery);
		basePricePrepStatement.setString(1, p.getCrustType());
		basePricePrepStatement.setString(2, p.getSize());
		ResultSet resultSet = basePricePrepStatement.executeQuery();
		while(resultSet.next())
		{
			pizzaBasePriceID = resultSet.getInt("PizzaBasePriceID");
		}
		int flag=1;
		if(p.getPizzaState().equals("preparing")){
			flag=0;
		}
		String query = "INSERT INTO pizza(PizzaCrustType, PizzaSize, PizzaPrice, PizzaCTC, PizzaIsReady, PizzaOrderID, PizzaBasePriceID, PizzaDateTime) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setString(1, p.getCrustType());
		prepStatement.setString(2, p.getSize());
		prepStatement.setDouble(3, p.getCustPrice());
		prepStatement.setDouble(4, p.getBusPrice());
		prepStatement.setInt(5, flag);
		prepStatement.setInt(6, p.getOrderID());
		prepStatement.setInt(7, pizzaBasePriceID);
		prepStatement.setString(8, p.getPizzaDate());
		prepStatement.execute();
		conn.close();
	}
	
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your yimplementatinon.
		 * 
		 * Ideally, you should't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		int count=1;
		int flag = 0;
		if(isDoubled){
			count++;
			flag = 1;
		}
		String updateQuery = "UPDATE topping SET ToppingCurInventory=ToppingCurInventory-? WHERE ToppingID=?";
		PreparedStatement updatePrepStatement = conn.prepareStatement(updateQuery);
		updatePrepStatement.setInt(1, count);
		updatePrepStatement.setInt(2, t.getTopID());
		updatePrepStatement.executeUpdate();

		String insertQuery = "INSERT INTO pizzatopping(PizzaToppingPizzaID, PizzaToppingToppingID, PizzaToppingIsDouble) VALUES(?,?,?);";
		PreparedStatement insertPrepStatement = conn.prepareStatement(insertQuery);
		insertPrepStatement.setInt(1, p.getPizzaID());
		insertPrepStatement.setInt(2, t.getTopID());
		insertPrepStatement.setInt(3, flag);
		insertPrepStatement.execute();
		conn.close();
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		/*
		 * This method connects a discount with a Pizza in the database.
		 * 
		 * What that means will be specific to your implementatinon.
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		String query = "INSERT INTO pizzadiscount(PizzaDiscountPizzaID, PizzaDiscountDiscountID) VALUES(?, ?);";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setInt(1, p.getPizzaID());
		prepStatement.setLong(2, d.getDiscountID());
		prepStatement.execute();
		conn.close();
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		/*
		 * This method connects a discount with an order in the database
		 * 
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the dabast
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		String query = "INSERT INTO orderdiscount(OrderDiscountOrderID, OrderDiscountDiscountID) VALUES(?, ?);";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setInt(1, o.getOrderID());
		prepStatement.setLong(2, d.getDiscountID());
		prepStatement.execute();
		conn.close();
	}
	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		/*
		 * This method adds a new customer to the database.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		String query = "INSERT INTO customer(CustomerName, CustomerPhone) VALUES(?, ?);";

		PreparedStatement prepStatement = conn.prepareStatement(query);
		String name = c.getFName() + " " + c.getLName();
		prepStatement.setString(1, name);
		prepStatement.setLong(2, Long.parseLong(c.getPhone()));
		prepStatement.execute();
		conn.close();
	}
	public static void updateCustomerAdd(Customer c) throws SQLException, IOException {
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
		}
		String query = "UPDATE customer SET CustomerAddress=?, CustomerCity=?, CustomerState=?, CustomerZipCode=? WHERE CustomerID=?";

		PreparedStatement prepStatement = conn.prepareStatement(query);

		String[] address = c.getAddress().split("/n");
		String street = address[0];
		String city = address[1];
		String state = address[2];
		String zip = address[3];
		prepStatement.setString(1, street);
		prepStatement.setString(2, city);
		prepStatement.setString(3, state);
		prepStatement.setString(4, zip);
		prepStatement.setInt(5, c.getCustID());

		prepStatement.execute();
		conn.close();
	}
	public static Pizza getLastPizza() throws SQLException, IOException {
		Pizza item = null;
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return null;
		}
		String query = "SELECT * FROM pizza ORDER BY PizzaID DESC LIMIT 1;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		String pizzaState = "preparing";
		while (resultSet.next()) {
			if(resultSet.getInt("PizzaIsReady")==1){
				pizzaState = "done";
			}
			item = new Pizza(resultSet.getInt("PizzaID"), resultSet.getString("PizzaCrustType"),
					resultSet.getString("PizzaSize"), resultSet.getInt("PizzaOrderID"),
					pizzaState, resultSet.getString("PizzaDateTime"),
					resultSet.getDouble("PizzaPrice"), resultSet.getDouble("PizzaCTC"));
		}
		return item;
	}
	public static void completeOrder(Order o) throws SQLException, IOException {
		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return;
		}
		int orderID = o.getOrderID();

		String pizzaQuery = "UPDATE pizza SET PizzaIsReady = 1 WHERE PizzaOrderID=?;";
		PreparedStatement pizzaPrepStatement = conn.prepareStatement(pizzaQuery);
		pizzaPrepStatement.setInt(1, orderID);
		pizzaPrepStatement.executeUpdate();


		String orderQuery = "UPDATE orderhistory SET OrderIsReady = 1 WHERE OrderID=?;";
		PreparedStatement orderPrepStatement = conn.prepareStatement(orderQuery);
		orderPrepStatement.setInt(1, orderID);
		orderPrepStatement.executeUpdate();

		conn.close();
	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Order> ordersList = new ArrayList<>();
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return null;
		}
		String query = "SELECT * FROM orderhistory o LEFT JOIN dinein d ON o.OrderID=d.DineInID LEFT JOIN pickup p " +
				"ON o.OrderID=p.PickupID LEFT JOIN homedelivery h ON o.OrderID=h.HomeDeliveryID ORDER BY o.OrderID;";
		if(openOnly){
			query = "SELECT * FROM orderhistory o LEFT JOIN dinein d ON o.OrderID=d.DineInID LEFT JOIN pickup p ON " +
					"o.OrderID=p.PickupID LEFT JOIN homedelivery h ON o.OrderID=h.HomeDeliveryID WHERE " +
					"OrderIsReady=0 ORDER BY o.OrderID";
		}
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()){
			int orderID = resultSet.getInt("OrderID");
			int custID = resultSet.getInt("customerID");
			String orderType = resultSet.getString("OrderType");
			String orderDateTime = resultSet.getString("OrderDateTime");
			double orderPrice = resultSet.getDouble("OrderPrice");
			double orderCTC = resultSet.getDouble("OrderCTC");
			int orderIsReady = resultSet.getInt("OrderIsReady");
			if(orderType.equals(dine_in)){
				int tableNum = resultSet.getInt("DineInTableNum");
				DineinOrder item = new DineinOrder(orderID, custID, orderDateTime, orderPrice, orderCTC, orderIsReady, tableNum);
				ordersList.add(item);
			}
			else if (orderType.equals(pickup)) {
				int pickupStatus = resultSet.getInt("isPickedUp");
				PickupOrder item = new PickupOrder(orderID, custID, orderDateTime, orderPrice, orderCTC, pickupStatus, orderIsReady);
				ordersList.add(item);
			}
			else{
				Customer cust = findCustomerByID(custID);
				DeliveryOrder item = new DeliveryOrder(orderID, custID, orderDateTime, orderPrice, orderCTC, orderIsReady, cust.getAddress());
				ordersList.add(item);
			}
		}
		conn.close();
		return ordersList;
	}
	
	public static Order getLastOrder() throws SQLException, IOException {
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */
		Order item = null;
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return null;
		}
		String query = "SELECT * FROM orderhistory ORDER BY OrderID DESC LIMIT 1;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			item = new Order(resultSet.getInt("OrderID"), resultSet.getInt("customerID"),
					resultSet.getString("OrderType"), resultSet.getString("OrderDateTime"),
					resultSet.getDouble("OrderPrice"), resultSet.getDouble("OrderCTC"),
					resultSet.getInt("OrderIsReady"));
		}
		 return item;
	}

	public static ArrayList<Order> getOrdersByDate(String date)throws SQLException, IOException{
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *  
		 */
		ArrayList<Order> ordersList = getOrders(false);
		ArrayList<Order> specificOrderList = new ArrayList<>();
		DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dtfOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate givenOrderDate = LocalDate.parse(date, dtfOutput);
		for(Order item: ordersList){
			String datetime = item.getDate();
			LocalDate OrderDate = LocalDate.parse(datetime, dtfInput);
			if(OrderDate.equals(givenOrderDate)){
				specificOrderList.add(item);
			}
		}
		return specificOrderList;
	}
	public static ArrayList<Order> getOrdersSinceDate(String date) throws SQLException, IOException{
		ArrayList<Order> ordersList = getOrders(false);
		ArrayList<Order> specificOrderList = new ArrayList<>();
		DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dtfOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate givenOrderDate = LocalDate.parse(date, dtfOutput);
		for(Order item: ordersList){
			String datetime = item.getDate();
			LocalDate OrderDateTime = LocalDate.parse(datetime, dtfInput);
			if((OrderDateTime.isAfter(givenOrderDate)) || (OrderDateTime.isEqual(givenOrderDate))){
				specificOrderList.add(item);
			}
		}
		return specificOrderList;
	}

	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/
		ArrayList<Discount> discountList = new ArrayList<>();
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return null;
		}
		String query = "SELECT * FROM discount;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()){
			double amount;
			boolean percent=false;
			if(resultSet.getString("DiscountPercentOff")!=null) {
				percent=true;
				amount = resultSet.getDouble("DiscountPercentOff");
			}
			else {
				amount = resultSet.getDouble("DiscountDollarOff");
			}
			Discount item = new Discount(
					resultSet.getInt("DiscountID"),
					resultSet.getString(("DiscountName")),
					amount, percent);
			discountList.add(item);
		}
		conn.close();
		return discountList;
	}

	public static Discount findDiscountByName(String name){
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */
		try {
			ArrayList<Discount> discountList = getDiscountList();
			for(Discount item: discountList){
				if(item.getDiscountName().equals(name)){
					return item;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/
		ArrayList<Customer> customerList = new ArrayList<>();
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return null;
		}
		String query = "SELECT * FROM customer;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()){
			String fullName = resultSet.getString("CustomerName");
			String[] name = fullName.split(" ");
			Customer item = new Customer(
					resultSet.getInt("CustomerID"), name[0], name[1],
					resultSet.getString("CustomerPhone"));
			String fullStreet = resultSet.getString("CustomerAddress");
			String city = resultSet.getString("CustomerCity");
			String state = resultSet.getString("CustomerState");
			String zipcode = resultSet.getString("CustomerZipCode");
			item.setAddress(fullStreet, city, state, zipcode);
			customerList.add(item);
		}
		conn.close();
		return customerList;
	}

	public static Customer findCustomerByPhone(String phoneNumber){
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */
		try {
			ArrayList<Customer> customerList = getCustomerList();
			for(Customer item: customerList){
				if(item.getPhone().equals(phoneNumber)){
					return item;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Customer findCustomerByID(int id){
		try {
			ArrayList<Customer> customerList = getCustomerList();
			for(Customer item: customerList){
				if(item.getCustID()==id){
					return item;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		/*
		 * Query the database for the aviable toppings and 
		 * return an arrayList of all the available toppings. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Topping> toppingsList = new ArrayList<>();
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return null;
		}
		String query = "SELECT * FROM topping;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()){
			Topping item = new Topping(
					resultSet.getInt("ToppingID"), resultSet.getString("ToppingName"),
					resultSet.getDouble("ToppingOnPersonal"),
					resultSet.getDouble("ToppingOnMedium"), resultSet.getDouble("ToppingOnLarge"),
					resultSet.getDouble("ToppingOnXLarge"),
					resultSet.getDouble("ToppingPrice"), resultSet.getDouble("ToppingCTC"),
					resultSet.getInt("ToppingMinInventory"),
					resultSet.getInt("ToppingCurInventory"));

			toppingsList.add(item);
		}
		conn.close();
		return toppingsList;
	}

	public static Topping findToppingByName(String name){
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */
		try {
			ArrayList<Topping> toppingsList = getToppingList();
			for(Topping item: toppingsList){
				if(item.getTopName().equals(name)){
					return item;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Topping getToppingFromId(int id) {
		try {
			ArrayList<Topping> toppingsList = getToppingList();
			for(Topping item: toppingsList){
				if(item.getTopID()==id){
					return item;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return;
		}
		int toppingID = t.getTopID();
		String query = "UPDATE topping SET ToppingCurInventory = ToppingCurInventory+? WHERE ToppingID=?;";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setDouble(1, quantity);
		prepStatement.setInt(2, toppingID);
		prepStatement.executeUpdate();
		conn.close();
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		double PizzaBasePrice = 0.0;
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return PizzaBasePrice;
		}
		String query = "SELECT PizzaBasePrice FROM pizzabase WHERE PizzaBaseCrustType=? AND PizzaBaseSize=?;";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setString(1, crust);
		prepStatement.setString(2, size);
		ResultSet resultSet = prepStatement.executeQuery();
		while(resultSet.next())
		{
			PizzaBasePrice = resultSet.getDouble("PizzaBasePrice");
		}
		conn.close();
		return PizzaBasePrice;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		/* 
		 * Query the database fro the base business price for that size and crust pizza.
		 * 
		*/

		double PizzaBaseCTC = 0.0;
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return PizzaBaseCTC;
		}
		String query = "SELECT PizzaBaseCTC FROM pizzabase WHERE PizzaBaseCrustType=? AND PizzaBaseSize=?;";
		PreparedStatement prepStatement = conn.prepareStatement(query);
		prepStatement.setString(1, crust);
		prepStatement.setString(2, size);
		ResultSet resultSet = prepStatement.executeQuery();
		while(resultSet.next())
		{
			PizzaBaseCTC = resultSet.getDouble("PizzaBaseCTC");
		}
		conn.close();
		return PizzaBaseCTC;
	}

	public static void printInventory() throws SQLException, IOException {
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *  
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return;
		}
		String query = "SELECT ToppingID, ToppingName, ToppingCurInventory FROM topping;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			System.out.println("ToppingID: " + resultSet.getString("ToppingID") +
					"ToppingName: " + resultSet.getString("ToppingName") +
					"ToppingCurInventory" + resultSet.getString("ToppingCurInventory"));
		}

		conn.close();
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return;
		}
		String query = "SELECT * FROM ToppingPopularity;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		System.out.printf("%-17s  %-5s %n", "Topping", "ToppingCount");
		while (resultSet.next()) {
			String topping = resultSet.getString("ToppingName");
			Integer toppingCount = resultSet.getInt("ToppingCount");
			System.out.printf("%-17s  %-5s %n", topping, toppingCount);
		}
		conn.close();
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		if(!connect_to_db()) {
			System.out.println("Refused to connect to DB");
			return;
		}
		String query = "SELECT * FROM ProfitByPizza;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		System.out.printf("%-15s %-15s %-17s %-17s %n", "Size", "Crust", "Profit", "OrderMonth");
		while (resultSet.next()){
			String size = resultSet.getString("Size");
			String crust = resultSet.getString("Crust");
			Double profit = resultSet.getDouble("Profit");
			String orderMonth = resultSet.getString("Order Month");

			System.out.printf("%-15s  %-15s  %-17s %-17s %n", size, crust, profit, orderMonth);
		}
		conn.close();
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		if(!connect_to_db()){
			System.out.println("Refused to connect to DB");
			return;
		}
		String query = "SELECT * FROM ProfitByOrderType;";
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		System.out.printf("%-15s %-15s %-17s %-17s %-17s  %n", "CustomerType", "OrderMonth", "TotalOrderPrice",
				"TotalOrderCost", "Profit");
		while(resultSet.next()){
			String customerType = resultSet.getString("CustomerType");
			String orderMonth = resultSet.getString("Order Month");
			Double totalOrderPrice = resultSet.getDouble("TotalOrderPrice");
			Double totalOrderCost = resultSet.getDouble("TotalOrderCost");
			Double profit = resultSet.getDouble("Profit");

			System.out.printf("%-15s  %-15s  %-17s %-17s %-17s %n", customerType, orderMonth, totalOrderPrice,
					totalOrderCost, profit);
		}
		conn.close();
	}
	
	
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
	/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with 
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 * 
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		 if(!connect_to_db()){
			 System.out.println("Refused to connect to DB");
			 return "";
		 }

		String cname2 = "";
		PreparedStatement os;
		ResultSet rset2;
		String query2;
		query2 = "Select CustomerName From customer WHERE CustomerID=?;";
		os = conn.prepareStatement(query2);
		os.setInt(1, CustID);
		rset2 = os.executeQuery();
		while(rset2.next())
		{
			cname2 = rset2.getString("CustomerName"); // note the use of field names in the getSting methods
		}

		conn.close();
		return cname2;
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}