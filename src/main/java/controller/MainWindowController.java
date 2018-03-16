/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.SimulatorState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author ewa
 */
public class MainWindowController implements Initializable {
  
  private Map<String, AnchorPane> panels = new HashMap<>();
  private SimulatorState simulatorState;
  
  @FXML
  private StockExchangeListController stockExchangeListController;
  @FXML
  private IndexListController indexListController;
  @FXML
  private CompanyListController companyListController;
  @FXML
  private CurrencyListController currencyListController;
  @FXML
  private FeedstockListController feedstockListController;
  @FXML
  private InvestorListController investorListController;
  @FXML
  private FundListController fundListController;
  @FXML
  private AssetListController assetListController;
  
  @FXML
  private StockExchangePanelController stockExchangePanelController;
  @FXML
  private IndexPanelController indexPanelController;
  
  @FXML
  private AnchorPane stockExchangePanel;
  @FXML
  private AnchorPane indexPanel;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    panels.put("stockExchange", stockExchangePanel);
    panels.put("index", indexPanel);
  }
  
  public void setSimulatorState(SimulatorState simulatorState) {
    this.simulatorState = simulatorState;
    
    passOnSimulatorState();
    
    simulatorState.getPanel().addListener((observable, oldPanel, newPanel) -> {
      hidePanel(oldPanel);
      showPanel(newPanel);
    });
  }
  
  private void hidePanel(String panel) {
    AnchorPane prevPanel = panels.get(panel);
    if (prevPanel != null) {
      prevPanel.setVisible(false);
    }
  }
  
  private void showPanel(String panel) {
    panels.get(panel).setVisible(true);
  }
  
  private void passOnSimulatorState() {
    stockExchangeListController.setSimulatorState(simulatorState);
    indexListController.setSimulatorState(simulatorState);
    companyListController.setSimulatorState(simulatorState);
    currencyListController.setSimulatorState(simulatorState);
    feedstockListController.setSimulatorState(simulatorState);
    investorListController.setSimulatorState(simulatorState);
    fundListController.setSimulatorState(simulatorState);
    assetListController.setSimulatorState(simulatorState);
    
    stockExchangePanelController.setSimulatorState(simulatorState);
    indexPanelController.setSimulatorState(simulatorState);
  }
  
  /**
   * Fukcja zapisująca stan symulatora do pliku.
   */
  public void serialize() {
    String nazwaPliku = "lista.ser";
    
    try {
      ObjectOutputStream out = new ObjectOutputStream(
              new BufferedOutputStream(
                      new FileOutputStream(nazwaPliku)));
      
      out.writeObject(simulatorState);
      out.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Funkcja wczytująca stan symulatora z pliku.
   */
  public void deserialize() {
    String nazwaPliku = "lista.ser";//TODO zmienić ścieżki
    
    try {
      ObjectInputStream in = new ObjectInputStream(
              new BufferedInputStream(
                      new FileInputStream(nazwaPliku)));
      
      SimulatorState newSimulatorState = (SimulatorState) in.readObject();
      simulatorState.copySimulatorState(newSimulatorState);
      
      in.close();
      
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
