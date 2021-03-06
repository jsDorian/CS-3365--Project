package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;

public class totalHandler implements EventHandler<ActionEvent> {
	TextArea display;
	String dbAddr = "jdbc:sqlite:stock.db";
	Connector db;
	Connection conn;
	Client client;
	
	public totalHandler(TextArea display, Client client) {
		this.display = display;
		this.client = client;
	}
	
	@Override
	public void handle(ActionEvent event) {
		// TODO: send message to checkout_system
		this.client.queue.add("get-total");
		Boolean respReceived = false;
		String resp = null;
		
		while (!respReceived) {
			if (this.client.resp != null) {
				resp = this.client.resp;
				this.client.clearResp();
			}
		}
		
		int total = Integer.parseInt(resp);
		
		// update register display
		this.display.setText("Total: $" + Integer.toString(total) + "\n");
		
		// TODO: send message to customer_display with the total amount
		this.client.queue.add("customer_display, " + total);
	}
}
