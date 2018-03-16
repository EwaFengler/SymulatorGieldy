/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import javafx.util.Pair;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Klasa reprezentująca aktywo.
 */
public class Asset implements Serializable {
  private long serialVersionUID = 1L;
  
  protected Market market;
  protected BigDecimal currentValue, minValue = BigDecimal.valueOf(Integer.MAX_VALUE), maxValue = BigDecimal.ZERO;
  protected List<Pair<Long, BigDecimal>> valueHistory = new ArrayList<>();
  
  protected final Monitor valueMonitor = new Monitor();
  protected final Monitor historyMonitor = new Monitor();
  
  public Asset(){}
  
  public Market getMarket() {
    return market;
  }
  
  public BigDecimal getMinValue() {
    return minValue;
  }
  
  public BigDecimal getMaxValue() {
    return maxValue;
  }
  
  public BigDecimal getCurrentValue() {
    return currentValue;
  }
  
  /**
   * Dodaje podaną wartość do historii wartości aktywa.
   * W razie potrzeby aktualizuje minValue i maxValue.
   * @param newValue nowo obrana wartość aktywa
   */
  public void registerNewValue(BigDecimal newValue){
    synchronized (historyMonitor) {
      System.out.println("Nowy kurs: " + newValue);
      valueHistory.add(new Pair<>(new Date().getTime(), newValue));
      
      if (newValue.compareTo(maxValue) > 0){
        maxValue = newValue;
      }
      else if(newValue.compareTo(minValue) < 0){
        minValue = newValue;
      }
    }
  }
  
  public List<Pair<Long, BigDecimal>> getValueHistory() {
    return valueHistory;
  }
}
