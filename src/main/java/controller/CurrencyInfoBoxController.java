package controller;

import economy.Currency;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CurrencyInfoBoxController extends InfoBoxController implements Initializable {
  
  private Currency currency;
  
  @FXML private GridPane currencyInfoBox;
  @FXML private VBox currencyFields;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    currency = (Currency) economyElement;
    addValues();
  }
  
  private void addValues(){
    Label countryLabel = new Label(currency.getCountryListString());
    countryLabel.setWrapText(true);
    countryLabel.prefWidthProperty().bind(currencyFields.widthProperty());
    
    currencyFields.getChildren().addAll(
            new Label(currency.getName()),
            countryLabel
    );
  }
  
  public void deleteCurrency(){
    simulatorState.removeCurrency(currency);
    currencyInfoBox.getScene().getWindow().hide();
  }
}
