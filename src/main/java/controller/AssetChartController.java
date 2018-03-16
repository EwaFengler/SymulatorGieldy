package controller;

import economy.Asset;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class AssetChartController implements Initializable {
  
  @FXML
  private AnchorPane assetChartContainer;
  
  private NumberAxis xAxis;
  private NumberAxis yAxis;
  private LineChart<Number, Number> assetChart;
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }
  
  /**
   * ustawia aktywa, dostosowuje osie i generuje wykres.
   * @param assetsList aktywo
   */
  public void setAssets(List<Asset> assetsList) {
    long firstMoment = 0;
    
    if(assetsList.size() == 1){
      setAsset(assetsList.get(0));
    }
    else {
      long lastMoment = assetsList.stream()
              .map(Asset::getValueHistory)
              .map(h -> h.get(h.size() - 1).getKey())
              .reduce((a, b) -> a > b ? a : b)
              .get();
  
      firstMoment = assetsList.stream()
              .map(Asset::getValueHistory)
              .map(h -> h.get(0).getKey())
              .reduce((a, b) -> a < b ? a : b)
              .get();
  
  
      xAxis = new NumberAxis(
              0,
              lastMoment - firstMoment,
              5000
      );
      xAxis.setLabel("Milisekundy");
  
      yAxis = new NumberAxis(10, 1000, 10);
      yAxis.setLabel("% wartości początkowej");
    }
  
    assetChart = new LineChart<>(xAxis, yAxis);
  
    AnchorPane.setTopAnchor(assetChart, 20.0);
    AnchorPane.setRightAnchor(assetChart, 20.0);
    AnchorPane.setBottomAnchor(assetChart, 20.0);
    AnchorPane.setLeftAnchor(assetChart, 20.0);
  
    assetChartContainer.getChildren().add(assetChart);
  
    if(assetsList.size() == 1){
      addSeries(assetsList.get(0));
    }
    else {
      long finalFirstMoment = firstMoment;
      assetsList.stream()
              .filter(a -> a.getValueHistory().size() > 0)
              .forEach(a -> addSeries(a, finalFirstMoment));
    }
  }
  
  
  public void setAsset(Asset asset) {
    
    List<Pair<Long, BigDecimal>> valueHistory = asset.getValueHistory();

    xAxis = new NumberAxis(
            0,
            valueHistory.get(valueHistory.size() - 1).getKey() - asset.getValueHistory().get(0).getKey(),
            5000
    );
    xAxis.setLabel("Milisekundy");

    yAxis = new NumberAxis(asset.getMinValue().doubleValue(), asset.getMaxValue().doubleValue(), 0.5);
    yAxis.setLabel("Wartość");
    
  }
  
  /**
   * Dodaje serię danych do wykresu
   * @param asset aktywo, którego seria jest dodawana
   */
  private void addSeries(Asset asset) {
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName(asset.toString());
    
    asset.getValueHistory().forEach(
            p -> series.getData().add(new XYChart
                    .Data<Number, Number>(p.getKey() - asset.getValueHistory().get(0).getKey(), p.getValue()))
    );
    
    assetChart.getData().add(series);
  }
  
  /**
   * Dodaje serię danych do wykresu
   * @param asset aktywo, którego seria jest dodawana
   * @param firstMoment początek osi X
   */
  private void addSeries(Asset asset, long firstMoment) {
    
    System.out.println(asset);
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName(asset.toString());
    
    asset.getValueHistory().forEach(
            p -> series.getData().add(new XYChart
                    .Data<Number, Number>(
                     p.getKey() - firstMoment,
                    p.getValue()
                            .divide(asset.getValueHistory().get(1).getValue(), 2, RoundingMode.HALF_EVEN)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(0, RoundingMode.HALF_EVEN)))
    );
    
    assetChart.getData().add(series);
  }
}
