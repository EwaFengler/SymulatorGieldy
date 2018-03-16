/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class StockExchangeListController extends ListController implements Initializable {

  @FXML private Button addStockExchangeButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "stockExchange";
  }

  @Override
  protected void getValuesFromSimulatorState(){
    simulatorState.getStockExchangeListProperty().addListener(this::onChanged);
    addStockExchangeButton.disableProperty().bind(simulatorState.getCurrencyListProperty().emptyProperty());
  }
  
  public void showStockExchangePanel() {
    simulatorState.setPanel("stockExchange");
  }
}
