package controller;

import economy.Company;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CompanyInfoBoxController extends InfoBoxController implements Initializable {
  
  private Company company;
  
  @FXML private GridPane companyInfoBox;
  @FXML private VBox companyFields;
  @FXML private TextField sharesNum;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  @Override
  public void setEconomyElement(Object economyElement) {
    company = (Company) economyElement;
    addValues();
  }
  
  private void addValues(){
      
      companyFields.getChildren().addAll(
            new Label(company.getName()),
            new Label(company.getDateOfFirstRating().toString()),
            new Label(company.getOpeningRate().toString()),
            new Label(company.getCurrentRate().toString()),
            new Label(company.getMinimumRate().toString()),
            new Label(company.getMaximumRate().toString()),
            new Label(String.valueOf(company.getNumOfShares())),
            new Label(company.getProfit().toString()),
            new Label(company.getIncome().toString()),
            new Label(company.getEquity().toString()),
            new Label(company.getOpeningCapital().toString()),
            new Label(String.valueOf(company.getTradingVolume())),
            new Label(String.valueOf(company.getTurnover()))
    );
  }
  
  public void buyShares(){
    company.buyShares(Integer.parseInt(sharesNum.getText()));
    sharesNum.setText("");
  }
  
  public void deleteCompany(){
    simulatorState.removeCompany(company);
    companyInfoBox.getScene().getWindow().hide();
  }
}
