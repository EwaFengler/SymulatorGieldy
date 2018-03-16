/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.Asset;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class AssetListController extends ListController implements Initializable {
  
  private List<Asset> assetsList = new ArrayList<>();
  
  @FXML Button showChartButton;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    type = "asset";
    //showChartButton.disableProperty().bind(assetsList.);
  }
  
  @Override
  protected void getValuesFromSimulatorState() {
    simulatorState.getAssetListPropertyProperty().addListener(this::onChanged);
  }
  
  @Override
  protected void addButton(Object o) {
    CheckBox checkBox = new CheckBox(o.toString());
    checkBox.setId(String.valueOf(o.hashCode()));
    checkBox.getStyleClass().add("economyElement");
    
    checkBox.setOnAction(event -> {
      if(assetsList.contains(o)){
        assetsList.remove(o);
      }
      else {
        assetsList.add(((Asset) o));
      }
    });
  
    economyElementList.getChildren().add(checkBox);
  }
  
  @Override
  protected void removeButton(String s) {
    for (Node n : economyElementList.getChildren()) {
      if (s.equals(n.getId())) {
        economyElementList.getChildren().remove(n);
        assetsList.removeIf(a -> String.valueOf(a.hashCode()).compareTo(s) == 0);
        break;
      }
    }
  }
  
  public void showChart(){
    Stage stage = new Stage();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/view/singleAssetChart.fxml"));
  
    try {
      AnchorPane chartWindow = loader.load();
    
      Scene scene = new Scene(chartWindow);
      stage.setScene(scene);
      stage.setTitle("Wykres");
      stage.show();
    
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    AssetChartController controller = loader.getController();
    controller.setAssets(assetsList);
  }
  
}