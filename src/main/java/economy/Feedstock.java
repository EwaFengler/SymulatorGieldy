package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Random;

/**
 * Funkcja reprezentująca surowiec
 */
public class Feedstock extends Asset implements Serializable {
  private long serialVersionUID = 1L;
  
  private String name,
                  unitOfTrading;
  private Currency currency;
  
  private static int i;
  private static final String[] feedstockNamesList = {
          "Magiczna ruda",
          "Złoto",
          "Internet",
          "Adamantium",
          "Lapis lazuli",
          "Węgiel",
          "Amelinium",
          
  };
  
  private static final String[] feedstockUnitsList = {
          "bryłka",
          "uncja",
          "wiaderko",
          "kilogram",
          "gram",
          "tona",
          "blaszka"
  };
  
  public Feedstock(){ i++; }
  
  public Feedstock(Currency currency, FeedstockMarket feedstockMarket) {
    if (i < feedstockNamesList.length) {
      name = feedstockNamesList[i];
      unitOfTrading = feedstockUnitsList[i];
    
    } else {
      name = "Surowiec Bez Ciekawej Nazwy Nr " + (i - feedstockNamesList.length + 1);
      unitOfTrading = "kilogram";
    }
    i++;
    
    this.currency = currency;
    this.market = feedstockMarket;
    
    Random random = new Random();
    currentValue = BigDecimal.valueOf(random.nextInt(950) + 50);
    minValue = currentValue;
    maxValue = currentValue;
    registerNewValue(currentValue);
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  public String getName() {
    return name;
  }
  
  public String getUnitOfTrading() {
    return unitOfTrading;
  }
  
  public Currency getCurrency() {
    return currency;
  }
  
  /**
   * Zwiększa wartość surowca na podstawie wielkości transakcji.
   * @param amount wielkość transakcji
   */
  public void increaseValue(BigDecimal amount) {
    synchronized (valueMonitor) {
      currentValue = currentValue.add(BigDecimal.valueOf(0.5).multiply(amount));
      registerNewValue(currentValue);
    }
  }
  
  /**
   * Zmniejsza wartość surowca na podstawie wielkości transakcji.
   * @param amount wielkość transakcji
   */
  public void decreaseValue(BigDecimal amount) {
    synchronized (valueMonitor) {
      currentValue = currentValue.subtract(BigDecimal.valueOf(0.5).multiply(amount));
      registerNewValue(currentValue);
    }
  }
  
}
