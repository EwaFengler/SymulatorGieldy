/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

/**
 * Klasa nadrzędna dla inwestora i funduszu.
 */
public class TypeOfInvestor implements Runnable, Serializable {
  private long serialVersionUID = 1L;
  
  protected static final Random random = new Random();
  protected BigDecimal investmentBudget;
  protected Currency currency;
  protected SimulatorState simulatorState;
  
  protected Map<Asset, BigDecimal> ownAssets = new HashMap<>();
  
  protected volatile boolean running;
  protected final Monitor budgetMonitor = new Monitor();
  
  protected static final String[] investorNamesList = {
          "Sknerus",
          "Lex",
          "Tony",
          "Eddie",
          "Giovanni",
          "Don Vito",
          "Doktor"
  };
  
  protected static final String[] investorSurnamesList = {
          "McKwacz",
          "Luthor",
          "Stark",
          "Moora",
          "",
          "Corleone",
          "Eggman"
  };
  
  public TypeOfInvestor() {
    start();
  }
  
  public static TypeOfInvestor randomTypeOfInvestor(Currency currency, SimulatorState simulatorState) {
    if (random.nextBoolean()) {
      return new Investor(currency, simulatorState);
    }
    return new InvestmentFund(currency, simulatorState);
  }
  
  @Override
  public void run() {
    running = true;
    
    while (running) {
      int i = random.nextInt(1000) + 1000;
      
      randomAction();
      
      try {
        sleep(i);
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
   * Funkcja kończąca działanie procesu.
   */
  public void stop() {
    running = false;
    Thread.currentThread().interrupt();
  }
  
  /**
   * Losuje akcję do wykonania przez inwestora/fundusz spośród:
   * zwiększenia budżetu, kupienia aktywów i sprzedania aktywów.
   */
  private void randomAction() {
    switch (random.nextInt(3)) {
      case (0):
        increaseBudget();
        break;
      case (1):
        buyAssets();
        break;
      case (2):
        sellAssets();
        break;
    }
  }
  
  /**
   * Zwiększa budżet o losową wartość.
   */
  private void increaseBudget() {
    synchronized (budgetMonitor) {
      System.out.println(this + " zwiększa budżet");
      investmentBudget = investmentBudget.add(BigDecimal.valueOf((random.nextInt(10000000) + 5000000) / 100.0));
    }
  }
  
  /**
   * Zwiększa budżet o określoną wartość
   * @param sum wartość, o którą powiększany jest budżet
   */
  public void increaseBudget(BigDecimal sum) {
    synchronized (budgetMonitor) {
      investmentBudget = investmentBudget.add(sum);
    }
  }
  
  /**
   * Zmniejsza budżet o określoną wartość
   * @param sum wartość, o którą zmniejszany jest budżet
   */
  public void decreaseBudget(BigDecimal sum) {
    synchronized (budgetMonitor) {
      investmentBudget = investmentBudget.subtract(sum);
    }
  }
  
  /**
   * Dodaje aktywo do posiadanych.
   * @param asset aktywo
   * @param amount ilość
   */
  public void addAsset(Asset asset, BigDecimal amount) {
    synchronized (ownAssets) {
      ownAssets.computeIfPresent(asset, (k, v) -> v = v.add(amount));
      ownAssets.putIfAbsent(asset, amount);
    }
  }
  
  /**
   * Zmniejsza ilość aktywa. Gdy osiąga zero, aktywo jest usuwane z listy posiadanych.
   * @param asset aktywo
   * @param amount odejmowana ilość
   */
  public void removeAsset(Asset asset, BigDecimal amount) {
    synchronized (ownAssets) {
      ownAssets.compute(asset, (k, v) -> v = v.subtract(amount));
      
      if (ownAssets.get(asset).equals(BigDecimal.ZERO)) {
        ownAssets.remove(asset);
      }
    }
  }
  
  /**
   * Funkcja losująca aktywo z rynku, jego ilość i wywołująca kupno na rynku.
   */
  private void buyAssets() {
    synchronized (budgetMonitor) {
      BigDecimal sum = new BigDecimal(
              (random.nextInt(Math.min(10000000, investmentBudget.intValue())) + 5000000) / 100).
              setScale(2, RoundingMode.HALF_EVEN);
      
      Asset asset = simulatorState.getRandomAsset();
      
      if (Objects.nonNull(asset) && !(this instanceof InvestmentFund && asset instanceof ParticipationUnit)) {
        asset.getMarket().realizeBuying(this, sum, asset);
      }
    }
  }
  
  /**
   * Funkcja losująca aktywo z posiadanych, jego ilość i wywołująca sprzedaż do rynku.
   */
  private void sellAssets() {
    synchronized (budgetMonitor) {
      synchronized (ownAssets) {
        List<Asset> keysAsArray = new ArrayList<>(ownAssets.keySet());
        
        //nie sprzeda, gdy nic nie ma
        if (ownAssets.size() > 0) {
          Asset asset = keysAsArray.get(random.nextInt(keysAsArray.size()));
          BigDecimal maxAmount = ownAssets.get(asset);
          
          BigDecimal amount = maxAmount.multiply(BigDecimal.valueOf(random.nextFloat()));
          
          if(asset instanceof Shareholding){
            amount = amount.setScale(0, RoundingMode.FLOOR);
          }
          else {
            amount = amount.setScale(2, RoundingMode.FLOOR);
          }
  
          if (amount.compareTo(BigDecimal.ZERO) > 0) {
            asset.getMarket().realizeSelling(this, amount, asset);
          }
        }
      }
    }
  }
  
  public BigDecimal getInvestmentBudget() {
    return investmentBudget;
  }
  
  public Currency getCurrency() {
    return currency;
  }
  
  /**
   * @return posiadane akcje
   */
  public List<String> getOwnSharesList() {
    return ownAssets.keySet().stream()
            .filter(k -> k instanceof Shareholding)
            .map(k -> k + " w ilości " + ownAssets.get(k))
            .collect(Collectors.toList());
  }
  
  /**
   * @return posiadane surowce
   */
  public List<String> getOwnFeedstocksList() {
    return ownAssets.keySet().stream()
            .filter(k -> k instanceof Feedstock)
            .map(k -> k + " w ilości " + ownAssets.get(k))
            .collect(Collectors.toList());
  }
  
  /**
   * @return posiadane waluty
   */
  public List<String> getOwnCurrenciesList() {
    return ownAssets.keySet().stream()
            .filter(k -> k instanceof Currency)
            .map(k -> k + " w ilości " + ownAssets.get(k))
            .collect(Collectors.toList());
  }
}
