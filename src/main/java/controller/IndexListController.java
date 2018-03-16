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
public class IndexListController extends ListController implements Initializable {
  
  @FXML private Button addIndexButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "index";
  }

  public void showIndexPanel(){
    simulatorState.setPanel("index");
  }

  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getStockIndexListProperty().addListener(this::onChanged);
    addIndexButton.disableProperty().bind(simulatorState.getCompanyListProperty().emptyProperty()
            .or(simulatorState.getStockExchangeListProperty().emptyProperty()));
  }
}
