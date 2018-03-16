/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.Investor;

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
public class InvestorListController extends ListController implements Initializable {
  
  @FXML private Button addInvestorButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "investor";
  }

  public void addInvestor() {
    Investor investor = new Investor(simulatorState.getRandomCurrency(), simulatorState);
    simulatorState.addInvestor(investor);
  }

  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getInvestorListProperty().addListener(this::onChanged);
    addInvestorButton.disableProperty().bind(simulatorState.getCurrencyListProperty().emptyProperty());
  }
}
