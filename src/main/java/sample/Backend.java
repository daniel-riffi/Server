package sample;

import at.orderlibrary.Offer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;

public class Backend {
    public Backend(){}

    public void startServer(){

    }

    public void stopServer(){

    }

    public void saveOffersToFile(ArrayList<Offer> offerList) {
        try(XMLEncoder enc = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("offers.xml")))){
            enc.writeObject(offerList);
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public ObservableList<Offer> readOffersFromFile() {
        ArrayList<Offer> readOffersList = new ArrayList<>();
        try(XMLDecoder dec = new XMLDecoder(new BufferedInputStream(new FileInputStream("offers.xml")))){
            readOffersList = (ArrayList<Offer>) dec.readObject();
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        return FXCollections.observableArrayList(readOffersList);
    }
}
