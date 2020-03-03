package sample;

import at.orderlibrary.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class BarCookClient {
    public Type type;
    public int openPositions=0;

    private BufferedReader reader;
    private PrintWriter writer;

    private ServerManager sm;
    private Socket socket;
    private Thread reading;
    public BarCookClient(Socket socket, Type type, ServerManager sm){
        this.socket=socket;
        this.sm=sm;
        this.type=type;
        try {
            this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendOrderToClient(Order order){
        String json=new Gson().toJson(order);
        writer.print(json+"\r\n");
        writer.flush();
    }
    public void startRequestReadingThread(){
        reading=new Thread(()->{
            while(true) {
                try {
                    String read=reader.readLine();
                    NotfiyPositionsFinishedRequest request=new Gson().fromJson(read,NotfiyPositionsFinishedRequest.class);
                    openPositions-=request.count;
                } catch (Exception e) {
                   close();
                    break;
                }
            }
        });
        reading.start();
    }

    public void close() {
        sm.deleteBarCookClient(this);
        writer.close();
        try {
            reader.close();
        } catch (IOException ex) { }
        reading.stop();
    }
}
