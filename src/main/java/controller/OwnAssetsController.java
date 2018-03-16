package controller;

import economy.Investor;
import economy.TypeOfInvestor;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class OwnAssetsController implements Initializable {
  
  @FXML private ListView<String> sharesList;
  @FXML private ListView<String> participationUnitsList;
  @FXML private ListView<String> currenciesList;
  @FXML private ListView<String> feedstocksList;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  
  }
  
  public void setTypeOfInvestor(TypeOfInvestor typeOfInvestor) {
    TypeOfInvestor typeOfInvestor1 = typeOfInvestor;
    
    sharesList.itemsProperty().bind(new SimpleListProperty<>
            (FXCollections.observableArrayList(typeOfInvestor.getOwnSharesList())));
   
    if(typeOfInvestor instanceof Investor){
      participationUnitsList.itemsProperty().bind(new SimpleListProperty<>
              (FXCollections.observableArrayList(((Investor) typeOfInvestor).getOwnUnits())));
    }
    
    currenciesList.itemsProperty().bind(new SimpleListProperty<>
            (FXCollections.observableArrayList(typeOfInvestor.getOwnCurrenciesList())));
    
    feedstocksList.itemsProperty().bind(new SimpleListProperty<>
            (FXCollections.observableArrayList(typeOfInvestor.getOwnFeedstocksList())));
  }
}
