/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.Feedstock;
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
public class FeedstockListController extends ListController implements Initializable {
  
  @FXML Button addFeedstockButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "feedstock";
  }

  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getFeedstockListProperty().addListener(this::onChanged);
    addFeedstockButton.disableProperty().bind(simulatorState.getCurrencyListProperty().emptyProperty());
  }
  
  public void addFeedstock(){
    Feedstock feedstock = new Feedstock(simulatorState.getRandomCurrency(), simulatorState.getFeedstockMarket());
    simulatorState.addFeedstock(feedstock);
  }
}
