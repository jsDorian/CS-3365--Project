package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;

public class itemIdHandler implements EventHandler<ActionEvent> {
	TextField field;
	Label error;
	TextArea display;
	String dbAddr = "jdbc:sqlite:stock.db";
	Connector db;
	Connection conn;
	Boolean firstUse;
	Client client;
	
	public itemIdHandler(TextField field, Label error, TextArea display, Boolean firstUse, Client client) {
		this.field = field;
		this.error = error;
		this.display = display;
		this.firstUse = firstUse;
		this.client = client;
		
		// get connection to stock database
		db = new Connector(dbAddr);
		conn = db.getConnection();
	}
	
	@Override
	public void handle(ActionEvent event) {
		if (firstUse) {
			// TODO: send message to customer_display
			
			firstUse = false;
		}
		
		// get product id from control
		int id = Integer.parseInt(field.getText());
		
		// get product record from stock database and display error if item not found
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE id = ? LIMIT 1");
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			
			int rsLength = 0;
			
			this.error.setVisible(false);
			
			while(rs.next()) {
				String name = rs.getString("name");
				int price = rs.getInt("price");
				int amount = rs.getInt("amount");
				int weight = rs.getInt("weight");
				int bulk = rs.getInt("bulk");
				
				Item item = new Item(name, price, amount, weight, bulk, id);
				
				// update register display with item information
				displayInfo(item);
				
				rsLength = rs.getRow();
			}
			
			if (rsLength == 0) {
				this.error.setVisible(true);
			}
			
			rs.close();
			stmt.close();
			
			//conn.close();
		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}
	}
	
	// updates register display with item information
	public void displayInfo(Item item) {
		String info = "";
		
		// instruct cashier to weigh items if the item is a bulk item
		if (item.bulk == 1) {
			info += "Bulk item! Please weigh the item!\n";
			
			// TODO: send message to customer_display to weigh items
			String msg = "scale, " + item.id;
			this.client.queue.add(msg);
		} else {
			info += "Name: " + item.name + "\n";
			info += "Price: $" + item.price + "\n";
			
			// TODO: send price to checkout_system
			String msg = String.join(",", "total", Integer.toString(item.price));
			this.client.queue.add(msg);
		}
		
		String existingInfo = this.display.getText();
		
		this.display.setText(existingInfo + "\n" + info);
		
		// TODO: send display text to checkout_system to be forwarded to customer_display
		String msg = String.join(",", "customer_display", info);
		this.client.queue.add(msg);
	}
}
