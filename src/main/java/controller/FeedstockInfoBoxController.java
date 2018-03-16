package controller;

import economy.Feedstock;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class FeedstockInfoBoxController extends InfoBoxController implements Initializable {
  
  private Feedstock feedstock;
  
  @FXML
  private GridPane feedstockInfoBox;
  @FXML private VBox feedstockFields;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    feedstock = (Feedstock) economyElement;
    addValues();
  }
  
  private void addValues(){
    feedstockFields.getChildren().addAll(
            new Label(feedstock.getName()),
            new Label(feedstock.getUnitOfTrading()),
            new Label(feedstock.getCurrency().toString()),
            new Label(feedstock.getCurrentValue().toString()),
            new Label(feedstock.getMinValue().toString()),
            new Label(feedstock.getMaxValue().toString())
    );
  }
  
  public void deleteFeedstock() {
    simulatorState.removeFeedstock(feedstock);
    feedstockInfoBox.getScene().getWindow().hide();
  }
}
