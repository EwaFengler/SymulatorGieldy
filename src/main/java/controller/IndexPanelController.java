/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.StockExchange;
import economy.StockIndex;
import economy.Company;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

import static java.util.Objects.isNull;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class IndexPanelController extends PanelController implements Initializable {

  @FXML private AnchorPane indexPanel;
  
  @FXML private TextField name;
  @FXML private ComboBox<StockExchange> stockExchange;
  @FXML private ToggleButton selected, top;
  @FXML private Spinner<Integer> numOfCompanies;

  @FXML private Button createIndexButton;
  
  @FXML private VBox companySelectionColumn;
  private CheckComboBox<Company> companyList;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    indexPanel.setVisible(false);
    numOfCompanies.disableProperty().bind(top.selectedProperty().not());
   }

  @Override
  protected void getValuesFromSimulatorState() {
    stockExchange.itemsProperty().bind(simulatorState.getStockExchangeListProperty());

    stockExchange.setCellFactory((param) -> new ListCell<StockExchange>() {

      @Override
      protected void updateItem(StockExchange stockExchange, boolean empty) {
        super.updateItem(stockExchange, empty);

        if (isNull(stockExchange) || empty) {
          setText("");
        } else {
          setText(stockExchange.getName());
        }
      }
    });
    
    companyList = new CheckComboBox<>(simulatorState.getCompanyListProperty());
    
    
    companyList.setDisable(true);
    companyList.disableProperty().bind(selected.selectedProperty().not());
    companySelectionColumn.getChildren().add(0, companyList);
  
    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1);
  
    simulatorState.getCompanyListProperty().sizeProperty()
            .addListener((observable, oldValue, newValue) -> valueFactory.maxProperty().setValue(newValue));
    
    numOfCompanies.setValueFactory(valueFactory);
    
    createIndexButton.disableProperty().bind(name.textProperty().isEmpty().
            or(stockExchange.valueProperty().isNull()).
            or(numOfCompanies.valueProperty().isNull().
                    and(companyList.checkModelProperty().isNull())));
  }

  public void createIndex() {
    StockIndex stockIndex;
    
    if(selected.isSelected()){
      stockIndex = new StockIndex(
              name.getText(),
              companyList.getCheckModel().getCheckedItems(),
              stockExchange.getValue(),
              simulatorState
      );
    }
    else {
      stockIndex = new StockIndex(name.getText(), numOfCompanies.getValue(), stockExchange.getValue(), simulatorState);
    }
    simulatorState.addIndex(stockIndex);
    stockExchange.getValue().addIndex(stockIndex);
    resetFields();
  }

  private void resetFields(){
    name.setText("");
    stockExchange.setValue(null);
    companyList.getCheckModel().clearChecks();
  }
}
