package economy;

import controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author ewa
 */
public class Economy extends Application {
  
  private static SimulatorState simulatorState = new SimulatorState();
  
  public static void main(String[] args) {
    launch(args);
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/view/mainWindow.fxml"));
    AnchorPane mainWindow = loader.load();
    
    Scene scene = new Scene(mainWindow);
    
    primaryStage.setScene(scene);
    primaryStage.setTitle("Sim Exchange Alpha");
    primaryStage.show();
    
    
    MainWindowController mainWindowController = loader.getController();
    mainWindowController.setSimulatorState(simulatorState);
  }
  
  @Override
  public void stop() {
    simulatorState.getCompanyListProperty().get().forEach(Company::stop);
    simulatorState.getInvestorListProperty().get().forEach(Investor::stop);
    simulatorState.getInvestmentFundListProperty().get().forEach(InvestmentFund::stop);
  }
}
