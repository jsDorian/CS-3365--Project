package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;

public class submitHandler implements EventHandler<ActionEvent> {
	public Label error;
	public TextField cardNumber;
	public TextField pin;
	public TextField cart;
	public TextField amount;
	public TextArea display;
	public Client client;
	public Boolean memberFirstUse = true;
	
	public submitHandler(Label error, TextField cardNumber, TextField pin, TextField cart, TextField amount, TextArea display, Client client) {
		this.error = error;
		this.cardNumber = cardNumber;
		this.pin = pin;
		this.amount = amount;
		this.display = display;
		this.client = client;
		this.cart = cart;
	}
	
	@Override
	public void handle(ActionEvent event) {
		String action = getAction();
		
		if (action == "payment") {
			paymentAction();
		} else if (action == "scale") {
			scaleAction();
		} else if (action == "cart") {
			cartAction();
		} else {
			memberAction();
		}
		
		emptyControls();
	}
	
	public void memberAction() {
		String cart = this.cart.getText();
		String msg = String.join(",", "member", cart);
				
		// TODO: sanitize input
		// send member info to checkout_system
		this.client.queue.add(msg);
		
		if (memberFirstUse) {
			memberFirstUse = false;
		}
		
		if (!memberFirstUse) {
			this.cart.setDisable(true);
		}
	}
	
	public void cartAction() {
		String cart = this.cart.getText();
		
		// send cart to cashier_register
		String msg = String.join(",", "cart", cart);
		this.client.queue.add(cart);
		
		this.cart.setDisable(true);
	}
	
	public void paymentAction() {
		int cardNumber = Integer.parseInt(this.cardNumber.getText());
		int pin = Integer.parseInt(this.pin.getText());
		
		// TODO: send card info to payment_authorization_system
		String msg = String.join(",", "payment", Integer.toString(cardNumber), Integer.toString(pin));
		this.client.queue.add(msg);
		
		this.cardNumber.setDisable(true);
		this.pin.setDisable(true);
	}
	
	public void scaleAction() {
		int amount = Integer.parseInt(this.amount.getText());
		
		// TODO: send amount to checkout_system
		String msg = String.join(",", "amount", Integer.toString(amount));
		this.client.queue.add(msg);
		
		this.amount.setDisable(true);
	}
	
	public void emptyControls() {
		this.error.setText(null);
		this.cardNumber.setText(null);
		this.pin.setText(null);
		this.amount.setText(null);
	}
	
	public String getAction() {
		var cardNumber = this.cardNumber.getText();
		var pin = this.pin.getText();
		var amount = this.amount.getText();
		var cart = this.cart.getText();
		
		if (amount == "" && cart == "") {
			return "payment";
		} else {
			if (cart == "") {
				return "scale";
			} else {
				String[] result = cart.split(",");
				
				if (result.length == 1) {
					return "member";
				}
				
				return "cart";
			}
		}
	}
}
