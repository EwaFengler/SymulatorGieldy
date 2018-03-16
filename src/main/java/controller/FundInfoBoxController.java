package controller;

import economy.InvestmentFund;
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

public class FundInfoBoxController  extends InfoBoxController implements Initializable {
  
  private InvestmentFund investmentFund;
  
  @FXML
  private GridPane fundInfoBox;
  @FXML private VBox fundFields;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    investmentFund = (InvestmentFund) economyElement;
    addValues();
  }
  
  private void addValues(){
    fundFields.getChildren().addAll(
            new Label(investmentFund.getInvestmentManagerName()),
            new Label(investmentFund.getInvestmentManagerSurname()),
            new Label(investmentFund.getInvestmentBudget().toString()),
            new Label(investmentFund.getCurrency().toString())
    );
  }
  
  public void showAssets(){
    Stage stage = new Stage();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/view/fundsAssets.fxml"));
  
    try {
      AnchorPane assetsWindow = loader.load();
    
      Scene scene = new Scene(assetsWindow);
      stage.setScene(scene);
      stage.setTitle(investmentFund + ": portfel inwestycyjny");
      stage.show();
    
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    OwnAssetsController ownAssetsController = loader.getController();
    ownAssetsController.setTypeOfInvestor(investmentFund);
  }
  
  public void deleteFund(){
    simulatorState.removeFund(investmentFund);
    fundInfoBox.getScene().getWindow().hide();
  }
}
