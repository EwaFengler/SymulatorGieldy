package controller;

import economy.StockIndex;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class IndexInfoBoxController  extends InfoBoxController implements Initializable {
  
  private StockIndex stockIndex;
  
  @FXML private GridPane indexInfoBox;
  @FXML private VBox indexFields;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    stockIndex = (StockIndex) economyElement;
    addValues();
  }
  
  private void addValues(){
    indexFields.getChildren().addAll(
      new Label(stockIndex.getName()),
      new Label(stockIndex.getStockExchange().toString()),
      new Label(stockIndex.getScore().toString()),
      new Label(stockIndex.getCompanyListString())
    );
  }
  
  public void deleteIndex(){
    simulatorState.removeIndex(stockIndex);
    indexInfoBox.getScene().getWindow().hide();
  }
}
