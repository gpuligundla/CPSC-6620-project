package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println("Welcome to Pizzas-R-Us!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}
	public static boolean checkValidInput(String regex, String input) {
		if (input.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		return matcher.matches();
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{

		/*
		 * EnterOrder should do the following:
		 * 
		 * Ask if the order is delivery, pickup, or dinein
		 *   if dine in....ask for table number
		 *   if pickup...
		 *   if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 *  until there are no more pizzas for the order
		 *  add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */
		String inputRegex = "^[1-3]$";
		String choiceRegex = "^(y|n)$";
		String userInput;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String curTimestamp=dtf.format(now);
		Order newOrder;
		String choice;
		while (true){
			System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
			userInput = reader.readLine();
			if(checkValidInput(inputRegex, userInput)){
				break;
			}
			else{
				System.out.println("Wrong Input!");
			}
		}
		if(userInput.equals("1")){
			System.out.println("What is the table number for this order?");
			int tableNum = Integer.parseInt(reader.readLine());
			newOrder = new DineinOrder(0, 1, curTimestamp,0.0, 0.0, 0, tableNum);
			DBNinja.addOrder(newOrder);
			newOrder = DBNinja.getLastOrder();
			while(true){
				System.out.println("Let's build a pizza!");
				Pizza p = buildPizza(newOrder.getOrderID());
				newOrder.addPizza(p);
				System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
				int input = Integer.parseInt(reader.readLine());
				if(input==-1){
					break;
				}
			}

		}
		else if (userInput.equals("2")) {
			int customerID;
			while (true) {
				System.out.println("Is this order for an existing customer? Answer y/n: ");
				choice = reader.readLine();
				if (checkValidInput(choiceRegex, choice)) {
					break;
				} else {
					System.out.println("Wrong input!...Try again");
				}
			}
			if (choice.equals("y")) {
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number:");
				customerID = Integer.parseInt(reader.readLine());
			} else {
				EnterCustomer();
				ArrayList<Customer> customerList = DBNinja.getCustomerList();
				Customer lastCustomer = customerList.get(customerList.size() - 1);
				customerID = lastCustomer.getCustID();
			}
			newOrder = new PickupOrder(0, customerID, curTimestamp, 0.0, 0.0, 0, 0);
			DBNinja.addOrder(newOrder);
			newOrder = DBNinja.getLastOrder();
			while (true) {
				Pizza p = buildPizza(newOrder.getOrderID());
				newOrder.addPizza(p);
				System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
				int input = Integer.parseInt(reader.readLine());
				if (input == -1) {
					break;
				}
			}
		}
		else {
			Customer deliveryCustomer;
			while (true) {
				System.out.println("Is this order for an existing customer? Answer y/n: ");
				choice = reader.readLine();
				if (checkValidInput(choiceRegex, choice)) {
					break;
				} else {
					System.out.println("Wrong input!...Try again");
				}
			}
			if (choice.equals("y")) {
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number:");
				int customerID = Integer.parseInt(reader.readLine());
				deliveryCustomer = DBNinja.findCustomerByID(customerID);
				System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
				int houseNum = Integer.parseInt(reader.readLine());
				System.out.println("What is the Street for this order? (e.g., Smile Street)");
				String street = reader.readLine();
				String fullStreet = houseNum +  " " + street;
				System.out.println("What is the City for this order? (e.g., Greenville)");
				String city = reader.readLine();
				System.out.println("What is the State for this order? (e.g., SC)");
				String state = reader.readLine();
				System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				String zipcode = reader.readLine();
				deliveryCustomer.setAddress(fullStreet, city, state, zipcode);
				DBNinja.updateCustomerAdd(deliveryCustomer);
			}
			else {
				System.out.println("What is this customer's name (first <space> last)");
				String name = reader.readLine();
				String[] nameArr = name.split(" ");
				System.out.println("What is this customer's phone number (##########) (No dash/space)");
				String phoneNum = reader.readLine();
				Customer newCustomer = new Customer(0, nameArr[0], nameArr[1], phoneNum);
				System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
				int houseNum = Integer.parseInt(reader.readLine());
				System.out.println("What is the Street for this order? (e.g., Smile Street)");
				String street = reader.readLine();
				String fullStreet = houseNum +  " " + street;
				System.out.println("What is the City for this order? (e.g., Greenville)");
				String city = reader.readLine();
				System.out.println("What is the State for this order? (e.g., SC)");
				String state = reader.readLine();
				System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				String zipcode = reader.readLine();
				newCustomer.setAddress(fullStreet, city, state, zipcode);
				DBNinja.addCustomer(newCustomer);
				ArrayList<Customer> customerList = DBNinja.getCustomerList();
				deliveryCustomer = customerList.get(customerList.size() - 1);
				newCustomer.setCustID(deliveryCustomer.getCustID());
				DBNinja.updateCustomerAdd(newCustomer);
			}
			newOrder = new DeliveryOrder(0, deliveryCustomer.getCustID(), curTimestamp, 0.0, 0.0, 0, deliveryCustomer.getAddress());
			DBNinja.addOrder(newOrder);
			newOrder = DBNinja.getLastOrder();
			while (true) {
				Pizza p = buildPizza(newOrder.getOrderID());
				newOrder.addPizza(p);
				System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
				int input = Integer.parseInt(reader.readLine());
				if (input == -1) {
					break;
				}
			}

		}
		double orderBasePrice = 0.0;
		double orderCTC = 0.0;
		for(Pizza item: newOrder.getPizzaList()){
			orderBasePrice += item.getCustPrice();
			orderCTC += item.getBusPrice();
		}
		DBNinja.updateOrderPrice(newOrder, orderBasePrice, orderCTC);
		while (true) {
			System.out.println("Do you want to add discounts to this order? Enter y/n?");
			choice = reader.readLine();
			if (checkValidInput(choiceRegex, choice)) {
				break;
			}
			else {
				System.out.println("Wrong input!...Try again");
			}
		}
		if(choice.equals("y")){
			ArrayList<Discount> discountList = DBNinja.getDiscountList();
			ArrayList<Integer> discountIDs = new ArrayList<>();
			for(Discount item: discountList){
				System.out.println(item);
				discountIDs.add(item.getDiscountID());
			}
			while(true) {
				Discount selectedDiscount = null;
				System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				int discountInput = Integer.parseInt(reader.readLine());
				if (discountInput==-1){
					break;
				}
				else if(discountIDs.contains(discountInput)){
					for(Discount item: discountList){
						if(item.getDiscountID()==discountInput){
							selectedDiscount = item;
							break;
						}
					}
					if(selectedDiscount.isPercent()){
						newOrder.setCustPrice(orderBasePrice-((orderBasePrice*selectedDiscount.getAmount())/100));
					}
					else{
						newOrder.setCustPrice(orderBasePrice-selectedDiscount.getAmount());
					}
					DBNinja.useOrderDiscount(newOrder, selectedDiscount);
				}
				else{
					System.out.println("Wrong input!...Try again");
				}
			}
		}
		System.out.println("Finished adding order...Returning to menu...");
	}
	
	
	public static void viewCustomers() throws SQLException, IOException 
	{
		/*
		 * Simply print out all of the customers from the database. 
		 */
		ArrayList<Customer> customers = DBNinja.getCustomerList();
		if (customers != null) {
			for (Customer cus_item : customers) {
				System.out.println(cus_item.toString());
			}
		}
	}
	

	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException 
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 * 
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */
		
		// User Input Prompts...
		 System.out.println("What is this customer's name (first <space> last)");
		 String name = reader.readLine();
		 String[] nameArr = name.split(" ");
		 System.out.println("What is this customer's phone number (##########) (No dash/space)");
		 String phoneNum = reader.readLine();
		 Customer newCustomer = new Customer(0, nameArr[0], nameArr[1], phoneNum);
		 DBNinja.addCustomer(newCustomer);
 

	}

	// View any orders that are not marked as completed ######################### display detail order
	public static void ViewOrders() throws SQLException, IOException 
	{
		/*  
		* This method allows the user to select between three different views of the Order history:
		* The program must display:
		* a.	all open orders
		* b.	all completed orders 
		* c.	all the orders (open and completed) since a specific date (inclusive)
		* 
		* After displaying the list of orders (in a condensed format) must allow the user to select a specific order for viewing its details.  
		* The details include the full order type information, the pizza information (including pizza discounts), and the order discounts.
		* 
		*/
		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		String regex = "^[abcd]$";
		String option = reader.readLine();
		if(checkValidInput(regex, option)){
			if(option.equals("a")){
				ArrayList<Order> allOrderList = DBNinja.getOrders(false);
				List<Integer> allOrderIds = new ArrayList<>();
				allOrderIds.add(-1);
				if(allOrderList.size()==0){
					System.out.println("No orders to display, returning to menu.");
				}
				else {
					for (Order order : allOrderList) {
						System.out.println(order.toSimplePrint());
						allOrderIds.add(order.getOrderID());
					}
					System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
					int detailedInput = Integer.parseInt(reader.readLine());
					Order detailedOrder=null;
					if (allOrderIds.contains(detailedInput) && detailedInput!=-1) {
						for (Order order : allOrderList) {
							if (order.getOrderID() == detailedInput) {
								detailedOrder=order;
								break;
							}
						}
						if (detailedOrder instanceof DineinOrder) {
							System.out.println((DineinOrder)detailedOrder);
						}
						else if(detailedOrder instanceof PickupOrder){
							System.out.println((PickupOrder) detailedOrder);
						}
						else {
							System.out.println((DeliveryOrder)detailedOrder);
						}
					} else if (detailedInput==-1) {
						System.out.println("Returning to menu");
					} else {
						System.out.println("I don't understand that input, returning to menu");
					}
				}
			}
			else if(option.equals("b")){
				ArrayList<Order> openOrderList = DBNinja.getOrders(true);
				List<Integer> openOrderIds = new ArrayList<>();
				openOrderIds.add(-1);
				if(openOrderList.size()==0){
					System.out.println("No orders to display, returning to menu.");
				}
				else {
					for (Order order : openOrderList) {
						System.out.println(order.toSimplePrint());
						openOrderIds.add(order.getOrderID());
					}
					System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
					int detailedInput = Integer.parseInt(reader.readLine());
					Order detailedOrder=null;
					if (openOrderIds.contains(detailedInput) && detailedInput!=-1) {
						for (Order order : openOrderList) {
							if (order.getOrderID() == detailedInput) {
								detailedOrder=order;
								break;
							}
						}
						if (detailedOrder instanceof DineinOrder) {
							System.out.println((DineinOrder)detailedOrder);
						}
						else if(detailedOrder instanceof PickupOrder){
							System.out.println((PickupOrder) detailedOrder);
						}
						else {
							System.out.println((DeliveryOrder)detailedOrder);
						}
					}
					else if (detailedInput==-1) {
						System.out.println("Returning to menu");
					}
					else {
						System.out.println("I don't understand that input, returning to menu");
					}
				}
			}
			else if (option.equals("c")) {
				ArrayList<Order> allOrderList = DBNinja.getOrders(false);
				ArrayList<Order> completedOrderList = new ArrayList<>();
				List<Integer> completedOrderIds = new ArrayList<>();
				completedOrderIds.add(-1);
				for(Order item: allOrderList){
					if(item.getIsComplete()==1){
						completedOrderList.add(item);
					}
				}
				if(completedOrderList.size()==0){
					System.out.println("No orders to display, returning to menu.");
				}
				else {
					for (Order order : completedOrderList) {
						System.out.println(order.toSimplePrint());
						completedOrderIds.add(order.getOrderID());
					}
					System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
					int detailedInput = Integer.parseInt(reader.readLine());
					Order detailedOrder=null;
					if (completedOrderIds.contains(detailedInput) && detailedInput!=-1) {
						for (Order order : completedOrderList) {
							if (order.getOrderID() == detailedInput) {
								detailedOrder=order;
								break;
							}
						}
						if (detailedOrder instanceof DineinOrder) {
							System.out.println((DineinOrder)detailedOrder);
						}
						else if(detailedOrder instanceof PickupOrder){
							System.out.println((PickupOrder) detailedOrder);
						}
						else {
							System.out.println((DeliveryOrder)detailedOrder);
						}
					}
					else if (detailedInput==-1) {
						System.out.println("Returning to menu");
					}
					else {
						System.out.println("I don't understand that input, returning to menu");
					}
				}
			}
			else {
				System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");
				String dateInput = reader.readLine();
				String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
				if (checkValidInput(dateRegex, dateInput)) {
					ArrayList<Order> specificOrderList = DBNinja.getOrdersSinceDate(dateInput);
					List<Integer> specificOrderIds = new ArrayList<>();
					specificOrderIds.add(-1);
					if(specificOrderList.size()==0){
						System.out.println("No orders to display, returning to menu.");
					}
					else {
						for (Order order : specificOrderList) {
							System.out.println(order.toSimplePrint());
							specificOrderIds.add(order.getOrderID());
						}
						System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
						int detailedInput = Integer.parseInt(reader.readLine());
						Order detailedOrder=null;
						if (specificOrderIds.contains(detailedInput)&& detailedInput!=-1) {
							for (Order order : specificOrderList) {
								if (order.getOrderID() == detailedInput) {
									detailedOrder=order;
									break;
								}
							}
							if (detailedOrder instanceof DineinOrder) {
								System.out.println((DineinOrder)detailedOrder);
							}
							else if(detailedOrder instanceof PickupOrder){
								System.out.println((PickupOrder) detailedOrder);
							}
							else {
								System.out.println((DeliveryOrder)detailedOrder);
							}
						}
						else if (detailedInput==-1) {
							System.out.println("Returning to menu");
						}
						else {
							System.out.println("I don't understand that input, returning to menu");
						}
					}
				}
				else {
					System.out.println("Incorrect entry, returning to menu.");
				}
			}
		}
		else{
			System.out.println("I don't understand that input, returning to menu");
		}
	}

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		ArrayList<Order> orderList = DBNinja.getOrders(true);
		List<Integer> orderIds = new ArrayList<>();
		if(orderList.size()!=0) {
			for (Order order : orderList) {
				System.out.println(order.toSimplePrint());
				orderIds.add(order.getOrderID());
			}
			System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
			while(true) {
				int input = Integer.parseInt(reader.readLine());
				if (orderIds.contains(input)) {
					Order order = null;
					for (Order currentOrder : orderList) {
						if (currentOrder.getOrderID()==input) {
							order = currentOrder;
							break;
						}
					}
					DBNinja.completeOrder(order);
					break;
				}
				else {
					System.out.println("Incorrect entry, not an option");
				}
			}
		}
		else{
			System.out.println("There are no open orders currently... returning to menu...");
		}
	}

	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		*/
		ArrayList<Topping> toppingList = DBNinja.getToppingList();
		System.out.println("ID\tName\t\t\tCurINVT");
		for (Topping t : toppingList) {
			System.out.printf("%-6s %-21s %6s %n", t.getTopID(), t.getTopName(), t.getCurINVT());
		}
	}


	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */

		ViewInventoryLevels();
		System.out.println("Which topping do you want to add inventory to? Enter the number: ");
		String input = reader.readLine();
		System.out.println("How many units would you like to add? ");
		double count = Double.parseDouble(reader.readLine());
		String toppingRegex =  "([1-9]|1[0-7]|-1)";
		if(checkValidInput(toppingRegex, input)){
			Topping t= DBNinja.getToppingFromId(Integer.parseInt(input));
			DBNinja.addToInventory(t, count);
		}
		else{
			System.out.println("Incorrect entry, not an option. Returning to menu...");
		}
	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */

		 Pizza ret = null;
		
		// User Input Prompts...
		System.out.println("What size is the pizza?");
		System.out.println("1."+DBNinja.size_s);
		System.out.println("2."+DBNinja.size_m);
		System.out.println("3."+DBNinja.size_l);
		System.out.println("4."+DBNinja.size_xl);
		System.out.println("Enter the corresponding number: ");
		String regex = "^[1-4]$";
		String sizeOption;
		String userSizeOption;
		while(true){
			sizeOption=reader.readLine();
			if(checkValidInput(regex, sizeOption)){
				break;
			}
			else{
				System.out.println("Wrong input!");
			}
		}
		if(sizeOption.equals("1")){
			userSizeOption = DBNinja.size_s;
		}
		else if(sizeOption.equals("2")){
			userSizeOption = DBNinja.size_m;
		}
		else if(sizeOption.equals("3")){
			userSizeOption = DBNinja.size_l;
		}
		else{
			userSizeOption = DBNinja.size_xl;
		}
		System.out.println("What crust for this pizza?");
		System.out.println("1."+DBNinja.crust_thin);
		System.out.println("2."+DBNinja.crust_orig);
		System.out.println("3."+DBNinja.crust_pan);
		System.out.println("4."+DBNinja.crust_gf);
		System.out.println("Enter the corresponding number: ");
		String crustOption;
		String userCrustOption;
		while(true){
			crustOption=reader.readLine();
			if(checkValidInput(regex, crustOption)){
				break;
			}
			else{
				System.out.println("Wrong input!");
			}
		}
		if(crustOption.equals("1")){
			userCrustOption=DBNinja.crust_thin;
		}
		else if(crustOption.equals("2")){
			userCrustOption=DBNinja.crust_orig;
		}
		else if(crustOption.equals("3")){
			userCrustOption=DBNinja.crust_pan;
		}
		else{
			userCrustOption = DBNinja.crust_gf;
		}
		double pizzaBasePrice = DBNinja.getBaseCustPrice(userSizeOption, userCrustOption);
		double pizzaBaseCTC = DBNinja.getBaseBusPrice(userSizeOption, userCrustOption);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String curTimestamp=dtf.format(now);

		ret = new Pizza(1,userSizeOption, userCrustOption, orderID,"preparing", curTimestamp, pizzaBasePrice, pizzaBaseCTC);
		DBNinja.addPizza(ret);
		ret = DBNinja.getLastPizza();
		String toppingRegex = "([1-9]|1[0-7]|-1)";
		String choiceRegex = "^(y|n)$";
		while(true) {
			ArrayList<Topping> curToppingList = DBNinja.getToppingList();
			System.out.println("Available Toppings:");
			ViewInventoryLevels();
			System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
			String toppingOption = reader.readLine();
			if (!checkValidInput(toppingRegex, toppingOption)) {
				System.out.println("Wrong input!");
				continue;
			}
			if(toppingOption.equals("-1")){
				break;
			}
			Topping top = curToppingList.get(Integer.parseInt(toppingOption)-1);
			String choice;
			while(true) {
				System.out.println("Do you want to add extra topping? Enter y/n");
				choice = reader.readLine();
				if (checkValidInput(choiceRegex, choice)) {
					break;
				}
				else {
					System.out.println("Wrong input!...Try again");
				}
			}
			double topSizeAmt;
			if (userSizeOption.equals(DBNinja.size_s)) {
				topSizeAmt = top.getPerAMT();
			}
			else if (userSizeOption.equals(DBNinja.size_m)) {
				topSizeAmt = top.getMedAMT();
			}
			else if (userSizeOption.equals(DBNinja.size_l)) {
				topSizeAmt = top.getLgAMT();
			}
			else {
				topSizeAmt = top.getXLAMT();
			}
			if(choice.equals("y")){
				if((top.getCurINVT()-(2*topSizeAmt)) > 0){
					DBNinja.useTopping(ret, top, true);
					ret.addToppings(top, true);
				}
				else{
					System.out.println("We don't have enough of that topping to add it...");
				}
			}
			else {
				if(top.getCurINVT()-topSizeAmt > 0){
					DBNinja.useTopping(ret, top, false);
					ret.addToppings(top, false);
				}
				else{
					System.out.println("We don't have enough of that topping to add it...");
				}
			}
		}
		String discountChoice;
		while(true) {
			System.out.println("Do you want to add discounts to this pizza? Enter y/n?");
			discountChoice = reader.readLine();
			if (checkValidInput(choiceRegex, discountChoice)) {
				break;
			}
			else {
				System.out.println("Wrong input!...Try again");
			}
		}
		if(discountChoice.equals("y")){
			ArrayList<Discount> discountList = DBNinja.getDiscountList();
			ArrayList<Integer> discountIDs = new ArrayList<>();
			while(true) {
				Discount selectedDiscount = null;
				for(Discount item: discountList){
					System.out.println(item);
					discountIDs.add(item.getDiscountID());
				}
				System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				int discountInput = Integer.parseInt(reader.readLine());
				if (discountInput==-1){
					break;
				}
				else if(discountIDs.contains(discountInput)){
					for(Discount item: discountList){
						if(item.getDiscountID()==discountInput){
							selectedDiscount = item;
							break;
						}
					}
					if(selectedDiscount.isPercent()){
						ret.setCustPrice(pizzaBasePrice-((pizzaBasePrice*selectedDiscount.getAmount())/100));
					}
					else{
						ret.setCustPrice(pizzaBasePrice-selectedDiscount.getAmount());
					}
					DBNinja.usePizzaDiscount(ret, selectedDiscount);
				}
				else{
					System.out.println("Wrong input!...Try again");
				}
			}
		}
		return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This method asks the use which report they want to see and calls the DBNinja method to print the appropriate report.
		 * 
		 */
		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		String option = reader.readLine();
		if(option.equals("a")){
			DBNinja.printToppingPopReport();
		}
		else if(option.equals("b")){
			DBNinja.printProfitByPizzaReport();
		}
		else if (option.equals("c")) {
			DBNinja.printProfitByOrderType();
		}
		else {
			System.out.println("I don't understand that input... returning to menu...");
		}
	}

	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}


