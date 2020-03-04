package sample;

import at.orderlibrary.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends Application {
    ReentrantLock lock;
    int port = 17774;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


    public static ArrayList<Offer> readAllOffers() {
        ArrayList<Offer> offers = new ArrayList<>();
        offers.add(new Offer(1, "Schnitzel deluxe premium", 8.6, Category.FOOD));
        offers.add(new Offer(2, "Hendl", 5.6, Category.FOOD));
        offers.add(new Offer(3, "Bier", 4.5, Category.ALCOHOLIC_DRINK));
        offers.add(new Offer(4, "Apfelsaft", 2.5, Category.NON_ALCOHOLIC_DRINK));
        offers.add(new Offer(2, "Würstel", 5.6, Category.FOOD));

        return offers;
    }

}
