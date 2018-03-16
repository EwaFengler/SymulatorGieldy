package controller;

import economy.StockExchange;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StockExchangeInfoBoxController extends InfoBoxController implements Initializable {
  
  private StockExchange stockExchange;
  
  @FXML
  private GridPane stockExchangeInfoBox;
  @FXML private VBox stockExchangeFields;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    stockExchange = (StockExchange) economyElement;
    addValues();
  }
  
  private void addValues(){
    Label stockIndexListLabel = new Label(stockExchange.getStockIndexListString());
    stockIndexListLabel.setWrapText(true);
    
    stockExchangeFields.getChildren().addAll(
            new Label(stockExchange.getName()),
            new Label(stockExchange.getCountry()),
            new Label(stockExchange.getCity()),
            new Label(stockExchange.getAddress()),
            new Label(stockExchange.getPercentageMarkup().toString()),
            stockIndexListLabel
    );
  }
  
  public void deleteStockExchange(){
    simulatorState.removeStockExchange(stockExchange);
    stockExchangeInfoBox.getScene().getWindow().hide();
  }
}
