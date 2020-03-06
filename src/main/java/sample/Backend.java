package sample;

import at.orderlibrary.Category;
import at.orderlibrary.Offer;
import at.orderlibrary.Order;
import at.orderlibrary.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Backend {

    private static ServerManager manager;
    private static ArrayList<Order> missedOrders;
    private static int orderId=0;
    public Backend(){
        manager=new ServerManager(17054);
        missedOrders= new ArrayList<>();

    }

    public void startServer(){
        manager.startServer(Backend::orderReceived,Backend::barCookConnected);
    }

    private static void barCookConnected(Object o) {
        for (Order order:new ArrayList<>(missedOrders)) {
            if(sendToClient(order,isCook(order)?Type.COOK:Type.BAR)) {
                missedOrders.remove(order);
            }
        }
    }

    private static boolean isCook(Order order){
        List<Category>  categories= Arrays.asList(
                Category.FOOD,
                Category.SMALL_FOOD,
                Category.DESERT
        );
        return categories.contains(order.positions.get(0).product.offer.category);


    }
    private static void orderReceived(Order order) {
        List<Category>  categories= Arrays.asList(
                Category.FOOD,
                Category.SMALL_FOOD,
                Category.DESERT
        );
        order.reinitPositionsWithOrder();
        Order orderForCook=order.copyOrderWithFunction((position)->categories.contains(position.product.offer.category));
        order.orderNumber=getNewOrderId();
        orderForCook.orderNumber=getNewOrderId();

        try {

            if(!sendToClient(orderForCook,Type.COOK)){
                missedOrders.add(orderForCook);
            }
            if(!sendToClient(order,Type.BAR)){
                missedOrders.add(order);
            }
        }catch (NoSuchElementException e){}
    }

    private static boolean sendToClient(Order orderForCook, Type type) {
        if(orderForCook.positions.size()>0) {
            Optional<BarCookClient> clientOptional = manager.getBarCookClients()
                    .stream()
                    .filter((c) -> c.type == type)
                    .min((c1, c2) -> c1.openPositions - c2.openPositions);
            clientOptional.ifPresent(barCookClient -> {
                ArrayList<BarCookClient> clients=manager.getBarCookClients();
                barCookClient.sendOrderToClient(orderForCook);
            });
            return clientOptional.isPresent();
        }
        return true;
    }

    public void stopServer(){
        manager.close();
    }

    public void saveOffersToFile(ArrayList<Offer> offerList) {
        try(XMLEncoder enc = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("offers.xml")))){
            enc.writeObject(offerList);
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public static ObservableList<Offer> readOffersFromFile() {
        ArrayList<Offer> readOffersList = new ArrayList<>();
        try(XMLDecoder dec = new XMLDecoder(new BufferedInputStream(new FileInputStream("offers.xml")))){
            readOffersList = (ArrayList<Offer>) dec.readObject();
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        return FXCollections.observableArrayList(readOffersList);
    }

    private static int getNewOrderId(){
        int id=orderId;
        orderId++;
        return id;
    }
}
