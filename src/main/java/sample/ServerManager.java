package sample;

import at.orderlibrary.TypeRequest;
import at.orderlibrary.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ServerManager {
    private ArrayList<BarCookClient> barCookClients;
    private ArrayList<WaiterClient> waiterClients;
    private Thread listenThread;
    ServerSocket server;

    private int port;
    private ReentrantLock lock;

    public ServerManager(int port) {
        this.port = port;
        lock = new ReentrantLock();

        barCookClients = new ArrayList<>();
        waiterClients = new ArrayList<>();
    }

    public void startServer(Consumer<Order> callback) {
        UnitTestVariables.ResetVariables();
        Gson gsonLoad = new Gson();
        String orderString = gsonLoad.toJson(UnitTestVariables.order1);
        Order sd = gsonLoad.fromJson(orderString, Order.class);

        TypeRequest tr = new TypeRequest();
        tr.type = Type.WAITER;
        String trJson = gsonLoad.toJson(tr);
        TypeRequest newTr = gsonLoad.fromJson(trJson, TypeRequest.class);

        NotfiyPositionsFinishedRequest nr = new NotfiyPositionsFinishedRequest();
        nr.count = 2;
        String nrJson = gsonLoad.toJson(nr);
        NotfiyPositionsFinishedRequest newNr = gsonLoad.fromJson(nrJson, NotfiyPositionsFinishedRequest.class);

        listenThread= new Thread(() -> {
            try {
                server = new ServerSocket(port);
                while (true) {
                    Socket client = server.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter pr = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

                    String req = br.readLine();
                    Gson gson = new Gson();
                    TypeRequest request = gson.fromJson(req, TypeRequest.class);

                    if (request.type == Type.WAITER) {
                        WaiterClient waiterClient = new WaiterClient(client, this);

                        lock.lock();
                        waiterClients.add(waiterClient);
                        lock.unlock();

                        waiterClient.startReadingThread(callback);
                        waiterClient.sendOffers(Main.readAllOffers());

                    } else {
                        BarCookClient barCookClient = new BarCookClient(client, request.type, this);

                        lock.lock();
                        barCookClients.add(barCookClient);
                        lock.unlock();
                        barCookClient.startRequestReadingThread();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        listenThread.start();
    }


    public ArrayList<BarCookClient> getBarCookClients() {
        lock.lock();
        ArrayList<BarCookClient> ret = new ArrayList<BarCookClient>(barCookClients);
        lock.unlock();
        return ret;
    }

    public ArrayList<WaiterClient> getWaiterClients() {
        lock.lock();
        ArrayList<WaiterClient> ret = new ArrayList<WaiterClient>(waiterClients);
        lock.unlock();
        return ret;
    }

    public void deleteBarCookClient(BarCookClient client) {
        lock.lock();
        barCookClients.remove(client);
        lock.unlock();
    }

    public void deleteWaiterCookClient(WaiterClient client) {
        lock.lock();
        waiterClients.remove(client);
        lock.unlock();
    }

    public void close(){
        listenThread.stop();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (BarCookClient client:barCookClients) {
            client.close();
        }
        for (WaiterClient client:waiterClients) {
            client.close();
        }
    }
}
