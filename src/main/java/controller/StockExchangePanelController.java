/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.Country;
import economy.Currency;
import economy.StockExchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class StockExchangePanelController extends PanelController implements Initializable {
  
  @FXML
  private AnchorPane stockExchangePanel;
  
  @FXML
  private TextField name;
  @FXML
  private ComboBox<String> country;
  @FXML
  private ComboBox<Currency> currency;
  @FXML
  private TextField city;
  @FXML
  private TextArea address;
  @FXML
  private TextField percentageMarkup;
  @FXML
  private Button createStockExchangeButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    stockExchangePanel.setVisible(false);
    
    ObservableList<String> countryList = FXCollections.observableArrayList(Country.getCountryNamesList());
    country.setItems(countryList);
    
  }
  
  @Override
  protected void getValuesFromSimulatorState() {
    currency.itemsProperty().bind(simulatorState.getCurrencyListProperty());
    
    currency.setCellFactory((param) -> new ListCell<Currency>() {
      
      @Override
      protected void updateItem(Currency currency, boolean empty) {
        super.updateItem(currency, empty);
        
        if (currency == null || empty) {
          setText("");
        } else {
          setText(currency.getName());
        }
      }
    });
    
    currency.setButtonCell(currency.getCellFactory().call(null));
    
    createStockExchangeButton.disableProperty().bind(
            name.textProperty().isEmpty().
                    or(country.valueProperty().isNull()).
                    or(currency.valueProperty().isNull()).
                    or(city.textProperty().isEmpty()).
                    or(address.textProperty().isEmpty()).
                    or(percentageMarkup.textProperty().isEmpty())
    );
  }
  
  public void createStockExchange() {
    StockExchange stockExchange = new StockExchange(
            name.getText(),
            country.getValue(),
            city.getText(),
            address.getText(),
            currency.getValue(),
            new BigDecimal(percentageMarkup.getText()).setScale(3, RoundingMode.HALF_EVEN));
    simulatorState.addStockExchange(stockExchange);
    resetFields();
  }
  
  private void resetFields() {
    name.setText("");
    country.setValue(null);
    city.setText("");
    address.setText("");
    currency.setValue(null);
    percentageMarkup.setText("");
  }
  
}
