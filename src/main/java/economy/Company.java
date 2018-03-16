/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Klasa reprezentująca spółkę.
 */
public class Company implements Runnable, Comparable<Company>, Serializable {
  private long serialVersionUID = 1L;
  
  private String name;
  private Date dateOfFirstRating;
  private BigDecimal openingRate,
          minimumRate,
          maximumRate,
          income,
          equity,
          openingCapital,
          turnover;
  
  private int numOfShares;
  private int tradingVolume;
  
  private Shareholding shareholding;
  private StockExchange stockExchange;
  private Currency currency;
  
  private static final Random random = new Random();
  private volatile boolean running;
  
  private volatile BigDecimal profit,
          currentRate;
  
  private final Monitor profitMonitor = new Monitor();
  private final Monitor rateMonitor = new Monitor();
  private final Monitor turnoverMonitor = new Monitor();
  private final Monitor volumeMonitor = new Monitor();
  
  private static int i = 0;
  private static final String[] companyNamesList = {
          "Mario & Luigi Hydraulic Systems",
          "Spółka Z.U.O. Dundersztyca",
          "LexCorp",
          "Stark Industries",
          "Zespół R"
  };
  
  @Override
  public void run() {
    running = true;
    int interval = random.nextInt(2000) + 5000;
    
    while (running) {
      int i = random.nextInt(2000) + 2000;
      try {
        sleep(i);
        increaseIncomeAndProfit();
        
        sleep(interval - i);
        issueShares();
        
      } catch (InterruptedException e) {
        System.out.println(this + " zakończył działanie.");
      }
    }
  }
  
  /**
   * Funkcja rozpoczynająca działanie wątku.
   */
  public void start(){
    Thread t = new Thread(this);
    t.start();
  }
  
  /**
   * Funkcja kończąca działanie wątku.
   */
  public void stop() {
    running = false;
    Thread.currentThread().interrupt();
  }
  
  public Company() {
    i++;
    start();
  }
  
  public Company(StockExchange stockExchange, Currency currency) {
    if (i < companyNamesList.length) {
      name = companyNamesList[i];
      
    } else {
      name = "Spolka Bez Ciekawej Nazwy Nr " + (i - companyNamesList.length + 1);
    }
    i++;
    
    dateOfFirstRating = randomDate();
    
    openingRate = BigDecimal.valueOf((random.nextInt(295000) + 5000) / 100).setScale(2, RoundingMode.HALF_EVEN);
    maximumRate = minimumRate = currentRate = openingRate;
    
    equity = BigDecimal.valueOf((random.nextInt(999000000) + 10000000) / 100).setScale(2, RoundingMode.HALF_EVEN);
    openingCapital = BigDecimal.valueOf((random.nextInt(999000000) + 10000000) / 100).setScale(2, RoundingMode.HALF_EVEN);
    profit = BigDecimal.valueOf(0);
    income = BigDecimal.valueOf(0);
    turnover = BigDecimal.valueOf(0);
    
    numOfShares = random.nextInt((equity.intValue() + openingCapital.intValue()) / openingRate.intValue()) + 500;
    tradingVolume = 0;
    
    shareholding = new Shareholding(this, numOfShares, stockExchange);
    this.stockExchange = stockExchange;
    this.currency = currency;
    
    start();
  }
  
  /**
   * Fukcja zwiększająca przychód i zysk o losową wartość
   */
  private void increaseIncomeAndProfit() {
    int val = random.nextInt(2000000) + 1000000;
    System.out.println(this + " zwiększa przychód i zysk o " + val);
    
    synchronized (profitMonitor) {
      income = income.add(BigDecimal.valueOf(val / 100));
      profit = profit.add(BigDecimal.valueOf(val / 100));
    }
  }
  
  /**
   * Fukcja zwiększająca przychód i zysk o podaną wartość
   */
  public void increaseIncomeAndProfit(BigDecimal val) {
    synchronized (profitMonitor) {
      income = income.add(val);
      profit = profit.add(val);
    }
  }
  
  public String toString() {
    return name;
  }
  
  public String getName() {
    return name;
  }
  
  /**
   * Skupuje własne akcje z rynku
   * @param gross wartość brutto kupna
   */
  public void buyShares(int gross) {
    synchronized (rateMonitor) {
      BigDecimal net = stockExchange.getNetPrice(gross);
      int num = net.divide(currentRate, 0, RoundingMode.FLOOR).intValue();
      
      synchronized (shareholding.getShareholdingMonitor()) {
        if (num > shareholding.getNumOfShares()) {
          num = shareholding.getNumOfShares();
        }
        
        synchronized (profitMonitor) {
          profit = profit.subtract(currentRate.multiply(BigDecimal.valueOf(num)));
        }
        
        actualizeRate(numOfShares - num);
        shareholding.subtract(num);
      }
      numOfShares -= num;
    }
  }
  
  /**
   * Wypuszcza losową ilość nowych akcji.
   */
  private void issueShares() {
    int val = random.nextInt(50) + 50;
    System.out.println(this + " wypuszcza " + val + " nowych akcji");
    
    synchronized (rateMonitor) {
      actualizeRate(numOfShares + val);
    }
    
    synchronized (shareholding.getShareholdingMonitor()) {
      shareholding.add(val);
    }
    
    numOfShares += val;
  }
  
  /**
   * Funkcja aktualizująca kurs akcji.
   * @param newNumOfShares nowa ilość akcji na rynku
   */
  public void actualizeRate(int newNumOfShares){
    synchronized (shareholding.getShareholdingMonitor()){
      BigDecimal temp = currentRate.multiply(BigDecimal.valueOf(shareholding.getNumOfShares()))
              .divide(BigDecimal.valueOf(newNumOfShares), 2, RoundingMode.HALF_EVEN);
      currentRate = temp;
      
      shareholding.registerNewValue(currentRate);
      
      if (currentRate.compareTo(minimumRate) < 0) {
        minimumRate = currentRate;
      }
      
      if (currentRate.compareTo(maximumRate) > 0) {
        maximumRate = currentRate;
      }
    }
  }
  
  private Date randomDate() {
    Random rnd;
    long ms;
    
    rnd = new Random();
    
    // Get an Epoch value roughly between 1940 and 2010
    // -946771200000L = January 1, 1940
    // Add up to 70 years to it (using modulus on the next long)
    ms = -946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));
    
    return new Date(ms);
  }
  
  public Date getDateOfFirstRating() {
    return dateOfFirstRating;
  }
  
  public BigDecimal getOpeningRate() {
    return openingRate;
  }
  
  public BigDecimal getCurrentRate() {
    return currentRate;
  }
  
  public BigDecimal getMinimumRate() {
    return minimumRate;
  }
  
  public BigDecimal getMaximumRate() {
    return maximumRate;
  }
  
  public BigDecimal getProfit() {
    return profit;
  }
  
  public BigDecimal getIncome() {
    return income;
  }
  
  public BigDecimal getEquity() {
    return equity;
  }
  
  public BigDecimal getOpeningCapital() {
    return openingCapital;
  }
  
  public BigDecimal getTurnover() {
    return turnover;
  }
  
  public int getNumOfShares() {
    return numOfShares;
  }
  
  public int getTradingVolume() {
    return tradingVolume;
  }
  
  public Shareholding getShareholding() {
    return shareholding;
  }

  public void setShareholding(Shareholding shareholding){
    this.shareholding = shareholding;
  }
  
  public StockExchange getStockExchange() {
    return stockExchange;
  }
  
  public Monitor getRateMonitor() {
    return rateMonitor;
  }
  
  public Currency getCurrency() {
    return currency;
  }
  
  /**
   * Metoda zwiększająca wolumen.
   * @param num wartość, o którą zwiększany jest wolumen
   */
  public void increaseTradingVolume(int num) {
    synchronized (volumeMonitor) {
      tradingVolume += num;
    }
  }
  
  /**
   * Metoda zwiększająca obroty.
   * @param sum wartość, o którą zwiększane są obroty
   */
  public void increaseTurnover(BigDecimal sum) {
    synchronized (turnoverMonitor) {
      turnover = turnover.add(sum);
    }
  }
  
  @Override
  public int compareTo(Company o) {
    return (this.numOfShares * this.currentRate.intValue()) - (o.numOfShares * o.currentRate.intValue());
  }
}
