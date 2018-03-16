/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.InvestmentFund;

import java.net.URL;
import java.util.ResourceBundle;

import economy.ParticipationUnit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class FundListController extends ListController implements Initializable {
  
  @FXML private Button addFundButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "fund";
  }  

  public void addFund(){
    InvestmentFund investmentFund = new InvestmentFund(simulatorState.getRandomCurrency(), simulatorState);
    simulatorState.addFund(investmentFund);
  }
  
  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getInvestmentFundListProperty().addListener(this::onChanged);
    addFundButton.disableProperty().bind(simulatorState.getCurrencyListProperty().emptyProperty()
            .or(simulatorState.getStockExchangeListProperty().emptyProperty()));
  }
}
