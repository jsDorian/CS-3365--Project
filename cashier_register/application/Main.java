package application;

import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
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
	Boolean firstUse = true;
	Client client = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// cash register function buttons
			Button itemId = new Button("ITEM-ID");
			Button scale = new Button("SCALE");
			Button total = new Button("TOTAL");
			
			// error label for product ids
			Label error = new Label("Invalid product ID! Product not found!");
			error.setTextFill(Color.RED);
			error.setVisible(false);
			
			// input for product ids
			TextField productId = new TextField();
			productId.setPromptText("Enter a product ID");
			
			// display area for transaction details and results
			TextArea registerDisplay = new TextArea();
			registerDisplay.setText("Register Display");
			registerDisplay.setEditable(false);
			
			// create a websocket connection to checkout_system
			try {
				this.client = new Client("localhost", 4009).activate();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			// event handlers for controls
			itemId.setOnAction(new itemIdHandler(productId, error, registerDisplay, firstUse, this.client));
			scale.setOnAction(new scaleHandler(registerDisplay, this.client));
			total.setOnAction(new totalHandler(registerDisplay, this.client));
			
			// positioning controls
			VBox functionKeys = new VBox(itemId, scale, total);
			functionKeys.setAlignment(Pos.CENTER);
			
			VBox input = new VBox(error, productId, registerDisplay);
			input.setAlignment(Pos.CENTER);
			
			root.setLeft(functionKeys);
			root.setCenter(input);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		launch(args);
	}
}
