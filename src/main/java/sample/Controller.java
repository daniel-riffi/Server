package sample;

import at.orderlibrary.Category;
import at.orderlibrary.Offer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Label lblIPAdress;
    @FXML
    public Label lblPort;
    @FXML
    public ListView<Offer> lvOffers;
    @FXML
    public TextField lblOfferName;
    @FXML
    public Spinner<Double> spOfferPrice;
    @FXML
    public ComboBox<Category> cbOfferCategory;
    @FXML
    public Button btnSwitchServerMode;
    @FXML
    public Button btnEditOffer;
    @FXML
    public Button btnDeleteOffer;
    @FXML
    public Button btnAddOffer;

    public boolean isRunning;
    public Backend backend;
    public Offer selectedOffer;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isRunning = false;
        backend = new Backend();
        initializeFields();
        lvOffers.setItems(backend.readOffersFromFile());
    }

    private void initializeFields() {
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.01, 100, 5, 0.5);
        spOfferPrice.setValueFactory(valueFactory);
        spOfferPrice.setEditable(true);

        cbOfferCategory.setItems(FXCollections.observableArrayList(Category.values()));
        cbOfferCategory.getSelectionModel().select(0);

        lvOffers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Offer>() {
            @Override
            public void changed(ObservableValue<? extends Offer> observableValue, Offer oldValue, Offer newValue) {
                if(newValue == null){
                    btnEditOffer.setDisable(true);
                    btnDeleteOffer.setDisable(true);
                    selectedOffer = null;
                    return;
                }
                lblOfferName.setText(newValue.name);
                spOfferPrice.getValueFactory().setValue(newValue.price);
                cbOfferCategory.setValue(newValue.category);
                selectedOffer = newValue;
                btnEditOffer.setDisable(false);
                btnDeleteOffer.setDisable(false);
                btnAddOffer.setDisable(false);
            }
        });

        lblOfferName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                boolean state = newValue.equals("");
                btnEditOffer.setDisable(state || selectedOffer == null);
                btnAddOffer.setDisable(state);
            }
        });

        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        lblIPAdress.setText(String.format("IP-Adresse: %s", localhost.getHostAddress().trim()));

        btnEditOffer.setDisable(true);
        btnDeleteOffer.setDisable(true);
        btnAddOffer.setDisable(true);
    }

    public void menuSaveClicked(ActionEvent actionEvent) {
        backend.saveOffersToFile(new ArrayList<>(lvOffers.getItems()));
    }

    public void btnSwitchServerModeClicked(ActionEvent actionEvent) {
        if(isRunning) {
            btnSwitchServerMode.setText("START SERVER");
            backend.stopServer();
        }
        else {
            btnSwitchServerMode.setText("STOP SERVER");
            backend.startServer();
        }
        isRunning = !isRunning;
    }

    public void btnEditOfferClicked(ActionEvent actionEvent) {
        selectedOffer.name = lblOfferName.getText();
        selectedOffer.price = spOfferPrice.getValue();
        selectedOffer.category = cbOfferCategory.getValue();
        lvOffers.refresh();
    }

    public void btnDeleteOfferClicked(ActionEvent actionEvent) {
        lvOffers.getItems().remove(selectedOffer);
        lvOffers.refresh();
    }

    public void btnAddOfferClicked(ActionEvent actionEvent) {
        int nextId;
        if(lvOffers.getItems().size() == 0) nextId = 1;
        else nextId = lvOffers.getItems().stream().mapToInt(x -> x.id).max().getAsInt() + 1;
        Offer offer = new Offer(nextId, lblOfferName.getText(), spOfferPrice.getValue(), cbOfferCategory.getValue());
        lvOffers.getItems().add(offer);
    }
}
