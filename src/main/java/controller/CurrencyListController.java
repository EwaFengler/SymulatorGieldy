/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.Currency;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class CurrencyListController extends ListController implements Initializable {

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "currency";
  }

  @FXML
  public void addCurrency() {
    Currency currency = new Currency(simulatorState.getForeignExchangeMarket());
    simulatorState.addCurrency(currency);
  }

  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getCurrencyListProperty().addListener(this::onChanged);
  }
}
