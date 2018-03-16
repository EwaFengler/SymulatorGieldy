/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.Company;

import java.net.URL;
import java.util.ResourceBundle;

import economy.Shareholding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class CompanyListController extends ListController implements Initializable {
  
  @FXML private Button addCompanyButton;
  
  @FXML private HBox buttonWrapper;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "company";
  }
  
  @FXML
  public void addCompany(){
    Company company = new Company(simulatorState.getRandomStockExchange(), simulatorState.getRandomCurrency());
    Shareholding shareholding = company.getShareholding();
    simulatorState.addCompany(company);
    simulatorState.addAsset(shareholding);
  }

  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getCompanyListProperty().addListener(this::onChanged);
    addCompanyButton.disableProperty().bind(simulatorState.getStockExchangeListProperty().emptyProperty());
  }
}
