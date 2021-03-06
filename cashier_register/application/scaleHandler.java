package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;

public class scaleHandler implements EventHandler<ActionEvent> {
	TextArea display;
	String dbAddr = "jdbc:sqlite:stock.db";
	Connector db;
	Connection conn;
	Client client;
	
	public scaleHandler(TextArea display, Client client) {
		this.display = display;
		this.client = client;
		
		// get connection to stock database
		db = new Connector(dbAddr);
		conn = db.getConnection();
	}
	
	@Override
	public void handle(ActionEvent event) {
		// TODO: get product id of product on scale from checkout_system
		this.client.queue.add("get-scale");
		Boolean respReceived = false;
		String resp = null;
		
		while (!respReceived) {
			if (this.client.resp != null) {
				resp = this.client.resp;
				this.client.clearResp();
			}
		}
		
		// response format: id, amount
		String[] result = resp.split(",");
		
		int id = Integer.parseInt(result[0]);
		int numOfItems = Integer.parseInt(result[1]);
		
		// get product record from stock database
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE id = ? LIMIT 1");
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				String name = rs.getString("name");
				int price = rs.getInt("price");
				int amount = rs.getInt("amount");
				int weight = rs.getInt("weight");
				int bulk = rs.getInt("bulk");
				
				Item item = new Item(name, price, amount, weight, bulk, id);
				
				// update register display with item information
				displayInfo(item, numOfItems);
			}
			
			rs.close();
			stmt.close();
			
			//conn.close();
		} catch (Exception e) {
			System.out.println("getUser: " + e.getMessage());
		}
	}
	
	// updates register display with item information
	public void displayInfo(Item item, int scaleAmt) {
		String info = "";
		
		// calculate total weight
		int totalWeight = item.weight * scaleAmt;
		
		// TODO: send total weight to customer_display
		String msg = String.join(",", "customer_display", Integer.toString(scaleAmt));
		this.client.queue.add(msg);
		
		// update register display
		info += "Name: " + item.name + "\n";
		int price = item.price * totalWeight;
		info += "Price: $" + price + "\n";
		info += "Total weight: " + totalWeight + "\n";
		
		// TODO: send price to checkout_system
		msg = String.join(",", "total", Integer.toString(item.price));
		this.client.queue.add(msg);
		
		String existingInfo = this.display.getText();
		
		this.display.setText(existingInfo + "\n" + info);
		
		// send display text to checkout_system to be forwarded to customer_display
		msg = String.join(",", "customer_display", info);
		this.client.queue.add(msg);
	}
}