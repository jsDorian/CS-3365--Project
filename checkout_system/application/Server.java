package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.control.*;

public class Server {

    private final ExecutorService pool;
    private final List<ServerThread> clients;
    private final int port;
    private boolean stop;
    private TextArea display;

    Server(int port, TextArea display) {
        this.port = port;
        this.display = display;
        pool = Executors.newFixedThreadPool(3);
        clients = new ArrayList<>();
    }

    private void runServer(){

        writeToDisplay("Server enabled: Waiting for client...");
        
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            stop = false;

            while(! stop){
                Socket clientSocket = serverSocket.accept();
                writeToDisplay("client connected!");
                ServerThread st1 = new ServerThread(clientSocket);
                pool.execute(st1);
            }
            
            if (stop) {
            	serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void writeToDisplay(String msg) {
    	String text = this.display.getText();
    	
    	text += "\n" + msg;
    	
    	this.display.setText(text);
    }

    public void stop(){
        for( ServerThread st : clients) {
            st.stopServerTread();
        }
        stop = true;
        pool.shutdown();
    }

    public void activate(){
        new Thread(()->runServer()).start();
    }
}