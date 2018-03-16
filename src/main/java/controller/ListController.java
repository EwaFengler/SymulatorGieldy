/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.SimulatorState;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author ewa
 */
public abstract class ListController {
  
  protected SimulatorState simulatorState;
  protected String type;
  
  @FXML
  protected VBox economyElementList;
  
  public void setSimulatorState(SimulatorState s) {
    simulatorState = s;
    getValuesFromSimulatorState();
  }
  
  /**
   * Funkcja dodająca / odejmująca przycisk w zależności od tego,
   * czy obserwowana lista elementów giełdy się zwiększa lub zmniejsza.
   *
   * @param change  zmiana w liście
   */
  protected void onChanged(ListChangeListener.Change change) {
    while (change.next()) {
      if (change.wasAdded()) {
        change.getAddedSubList().forEach(this::addButton);
      }
      else if (change.wasRemoved()) {
        change.getRemoved()
                .forEach(o -> removeButton(String.valueOf(o.hashCode())));
      }
    }
  }
  
  /**
   * Dodaje przycisk do listy danego elementu giełdy
   *
   * @param o   element giełdy
   **/
  protected void addButton(Object o) {
    Button button = new Button(o.toString());
    button.setOnAction(e -> info(o));
    button.setId(String.valueOf(o.hashCode()));
    button.getStyleClass().add("economyElement");
    
    economyElementList.getChildren().add(button);
  }
  
  /**
   * Usuwa przycisk z listowanych elementów giełdy
   *
   * @param s id przycisku
   */
  protected void removeButton(String s) {
    for (Node n : economyElementList.getChildren()) {
      if (s.equals(n.getId())) {
        economyElementList.getChildren().remove(n);
        break;
      }
    }
  }
  /**
   * Otwiera okno z informacją na temat danego elementu giełdy
   *
   * @param o       element giełdy
  **/
  protected void info(Object o) {
    Stage stage = new Stage();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/view/" + type + "InfoBox.fxml"));
    
    try {
      GridPane infoWindow = loader.load();
      
      Scene scene = new Scene(infoWindow);
      stage.setScene(scene);
      stage.setTitle(o.toString());
      stage.show();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    InfoBoxController infoBoxController = loader.getController();
    infoBoxController.setSimulatorState(simulatorState);
    infoBoxController.setEconomyElement(o);
  }
  
  protected abstract void getValuesFromSimulatorState();
}
