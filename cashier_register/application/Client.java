package application;

import java.util.LinkedList;
import java.util.Queue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Client{
    private final int port;
    private final String host;
    private  boolean stop;
    public Queue<String> queue = new LinkedList<>();
    public String resp = null;

    Client(String host, int port) {
        this.port = port;
        this.host = host;
    }

    private void runClient() {
        try {
            stop = false;
            Socket socket = new Socket(host, port);
            DataInputStream in = new DataInputStream( socket.getInputStream() );
            DataOutputStream out = new DataOutputStream( socket.getOutputStream() );
            out.writeUTF(getClientMessage());
            this.resp = in.readUTF();

            while (!stop) {
                TimeUnit.SECONDS.sleep(3);
                out.writeUTF(getClientMessage());
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Client activate() {
        new Thread(()->runClient()).start();
        return this;
    }

    public void stop() {
        stop = true;
    }

    private String getClientMessage() {
    	if (queue.size() > 0) {
    		return queue.remove();
    	}
    	
    	return null;
    }
    
    public void clearResp() {
    	this.resp = null;
    }
}