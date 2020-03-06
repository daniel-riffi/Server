package sample;

import at.orderlibrary.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class WaiterClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ServerManager sm;
    private Thread reading;

    public WaiterClient(Socket socket, ServerManager sm){
        this.socket=socket;
        try {
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sm=sm;

    }
    public void startReadingThread(Consumer<Order> callback){
        reading=new Thread(()->{
            while(true){
                try {
                    String read=reader.readLine();
                    if(read==null){
                        close();
                        break;
                    }
                    Order order=new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE).create()
                            .fromJson(read,Order.class);
                    callback.accept(order);

                } catch (Exception e) {
                    close();
                    break;
                }
            }
        });
        reading.start();
    }
    public void close(){
        sm.deleteWaiterCookClient(this);
        writer.close();
        try {
            reader.close();
        } catch (IOException ex) { }
        reading.stop();
    }

    public void sendOffers(List<Offer> offers){
        String json=new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE).create()
                .toJson(offers);
        writer.print(json+"\r\n");
        writer.flush();
    }
}
