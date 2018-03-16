package economy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Klasa reprezentująca jednostkę uczestnictwa.
 */
public class ParticipationUnit extends Asset {
  
  private final InvestmentFund investmentFund;
  
  public ParticipationUnit(InvestmentFund investmentFund, StockExchange stockExchange) {
    this.investmentFund = investmentFund;
    this.market = stockExchange;
    minValue = maxValue = currentValue = BigDecimal.valueOf(new Random().nextInt(2500000) + 5000000 / 100.0)
            .setScale(2, RoundingMode.HALF_EVEN);
    
    registerNewValue(currentValue);
  }
  
  public InvestmentFund getInvestmentFund() {
    return investmentFund;
  }
  
  @Override
  public String toString() {
    return "Jednostka uczestnictwa: " + investmentFund;
  }
  
  /**
   * Zwiększa wartość jednostki.
   * @param amount liczba zakupionych jednostek
   */
  public void increaseValue(BigDecimal amount) {
    synchronized (valueMonitor) {
      currentValue = currentValue.add(BigDecimal.valueOf(100).multiply(amount));
      registerNewValue(currentValue);
    }
  }
  
  /**
   * Zmniejsza wartość jednostki.
   * @param amount liczba sprzedanych jednostek
   */
  public void decreaseValue(BigDecimal amount) {
    synchronized (valueMonitor) {
      currentValue = currentValue.subtract(BigDecimal.valueOf(100).multiply(amount));
      registerNewValue(currentValue);
    }
  }
}
