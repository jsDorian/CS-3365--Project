package application;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class ServerThread extends Thread {

    private Socket socket = null;
    private  boolean stop;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try{
            stop = false;
            DataInputStream in = new DataInputStream( socket.getInputStream() );
            String fromClient;
            while(!stop){
                if((fromClient = in.readUTF()) != null) {
                    System.out.println("SERVER: recieved message - " + fromClient);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();;
        }
    }

    void stopServerTread(){
        stop = true;
    }
}