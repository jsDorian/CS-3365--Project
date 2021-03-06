package application;

import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.event.*;

public class Main extends Application {
	Client client = null;
	//error, cardNumber, pin, cart, amount, display, submit
	Label error = null;
	TextField cardNumber = null;
	TextField pin = null;
	TextField cart = null;
	TextField amount = null;
	TextArea display = null;
	Button submit = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// customer input submit button
			submit = new Button("Submit");
			
			// error label for wrong inputs
			error = new Label("Invalid entry! Try again!");
			error.setTextFill(Color.RED);
			error.setVisible(false);
			
			// inputs for payment ids
			cardNumber = new TextField();
			cardNumber.setPromptText("Enter card number...");
			cardNumber.setDisable(true);
			
			pin = new TextField();
			pin.setPromptText("Enter card pin...");
			pin.setDisable(true);
			
			// input for items
			cart = new TextField();
			cart.setPromptText("Enter cart items separated by commas...");
			
			amount = new TextField();
			amount.setPromptText("Enter number of items...");
			amount.setDisable(true);
			
			// display area for transaction details and results
			display = new TextArea();
			display.setText("Customer Display");
			display.setEditable(false);
			
			// create a websocket connection to checkout_system
			try {
				this.client = new Client("localhost", 4009).activate();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			// event handler for customer input submission
			submit.setOnAction(new submitHandler(error, cardNumber, pin, cart, amount, display, this.client));
			
			// positioning controls
			VBox textControls = new VBox(error, cardNumber, pin, cart, amount, display, submit);
			textControls.setAlignment(Pos.CENTER);
			
			root.setCenter(textControls);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		socketListener();
	}
	
	public void socketListener() {
		Boolean remove = false;
		
		while (!remove) {
			String value = null;
			
			if (this.client.eventHandled) {
				try {
					this.client.eventHandled = false;
					
					value = this.client.resp.remove();
		    		
		    		String[] result = value.split(",");
		    		
		    		onResponse(result);
				} catch (Exception e) {
					this.client.eventHandled = true;
				}
			}
		}
	}
	
	public void onResponse(String[] values) {
		String type = values[0];
		
		if (type == "member") {
			// enable membership controls
			cart.setPromptText("Enter your membership phone number...");
			cart.setDisable(false);
		} else if (type == "pin") {
			// enable membership controls
			cart.setPromptText("Enter your membership pin...");
			cart.setDisable(false);
		} else if (type == "error") {
			// enable error controls
			String errType = values[1];
			
			if (errType == "payment-error") {
				String errArgs = values[2];
				
				if (errArgs == "card-number") {
					error.setText("Invalid card number! Try again!");
				} else if (errArgs == "pin-number") {
					error.setText("Invalid pin! Try again!");
				} else {
					error.setText("Invalid input! Try again!");
				}
			}
			
			if (errType == "member-err") {
				String errArgs = values[2];
				
				if (errArgs == "phone-number") {
					error.setText("Invalid phone number! Try again!");
				} else if (errArgs == "pin") {
					error.setText("Invalid pin! Try again!");
				} else {
					error.setText("Invalid input! Try again!");
				}
			}
			
			if (errType == "cart-err") {
				String errArgs = values[2];
				
				if (errArgs == "item") {
					error.setText("Invalid item " + errArgs + " was not found! Try again!");
				} else {
					error.setText("Invalid input! Try again!");
				}
			}
			
			error.setDisable(false);
		} else if (type == "scale") {
			// enable scale controls
			String itemName = values[1];
			
			amount.setPromptText("Enter the number of " + itemName + "'s in the cart...");
			amount.setDisable(false);
		} else if (type == "payment") {
			// enable payment controls
			cardNumber.setPromptText("Enter card number...");
			cardNumber.setDisable(false);
		}
		
		this.client.eventHandled = true;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
