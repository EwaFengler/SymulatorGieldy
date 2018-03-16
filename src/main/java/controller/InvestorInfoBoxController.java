package controller;

import economy.Investor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InvestorInfoBoxController extends InfoBoxController implements Initializable {
  
  private Investor investor;
  
  @FXML
  private GridPane investorInfoBox;
  @FXML private VBox investorFields;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    investor = (Investor) economyElement;
    addValues();
  }
  
  private void addValues(){
    investorFields.getChildren().addAll(
            new Label(investor.getName()),
            new Label(investor.getSurname()),
            new Label(investor.getPESEL()),
            new Label(investor.getInvestmentBudget().toString()),
            new Label(investor.getCurrency().toString())
    );
  }
  
  public void showAssets(){
    Stage stage = new Stage();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/view/investorsAssets.fxml"));
  
    try {
      AnchorPane assetsWindow = loader.load();
    
      Scene scene = new Scene(assetsWindow);
      stage.setScene(scene);
      stage.setTitle(investor + ": portfel inwestycyjny");
      stage.show();
    
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    OwnAssetsController ownAssetsController = loader.getController();
    ownAssetsController.setTypeOfInvestor(investor);
  }
  
  public void deleteInvestor(){
    simulatorState.removeInvestor(investor);
    investorInfoBox.getScene().getWindow().hide();
  }
}
