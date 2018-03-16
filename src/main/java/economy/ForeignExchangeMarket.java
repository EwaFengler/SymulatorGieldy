/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import javafx.collections.ObservableList;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Klasa reprezentująca rynek walut. Walut nie można wymieniać pomiędzy rynkami.
 */
public class ForeignExchangeMarket extends Market implements Serializable{
  private long serialVersionUID = 1L;
  
  private SimulatorState simulatorState;

  /**
   * Lista kursów. Trójkę (L, M, R) należy czytać
   * R jest ceną waluty M w walucie L.
   * Lista zawiera również odwrotnie ułożone pary walut z odwrotnością kursu.
   */
  private List<Triple<Currency, Currency, BigDecimal>> exchangeRates = new ArrayList<>();
  private final Monitor exchangeRatesMonitor = new Monitor();
  
  public ForeignExchangeMarket() {
  }
  
  public ForeignExchangeMarket(SimulatorState simulatorState) {
    this.simulatorState = simulatorState;
  }
  
  /**
   * dla nowej waluty generuje cenę kupnasprzedaży względen istniejących walut.
   * @param currency dodawana waluta
   */
  public void addCurrencyToMarket(Currency currency) {
    ObservableList<Currency> currencyList = simulatorState.getCurrencyListProperty().get();
    
    if (currencyList.size() > 0) {
      Random rand = new Random();
      BigDecimal exchangeRate1, exchangeRate2;
      
      for (Currency c : currencyList) {
        if (!currency.equals(c)) {
          exchangeRate1 = BigDecimal.valueOf((rand.nextInt(450) + 50) / 100.0)
                  .setScale(2, BigDecimal.ROUND_HALF_EVEN);
          
          exchangeRate2 = BigDecimal.ONE.divide(exchangeRate1, 2, RoundingMode.HALF_EVEN);
      
          synchronized (exchangeRatesMonitor) {
            exchangeRates.add(new ImmutableTriple<>(currency, c, exchangeRate1));
            exchangeRates.add(new ImmutableTriple<>(c, currency, exchangeRate2));
          }
        }
      }
      synchronized (exchangeRatesMonitor) {
        exchangeRates.add(new ImmutableTriple<>(currency, currency, BigDecimal.ONE));
      }
      
      currency.registerNewValue(getAverageRate(currency));
    }
  }
  
  /**
   * Usuwa walutę z rynku poprzez zlikwidowanie każdej informacji o jej kursie
   * @param currency usuwana waluta
   */
  public void removeCurrencyFromMarket(Currency currency) {
    synchronized (exchangeRatesMonitor) {
      exchangeRates = exchangeRates.stream()
              .filter(t -> !(t.getLeft() == currency) && !(t.getMiddle() == currency))
              .collect(Collectors.toList());
    }
  }
  
  /**
   * Zwraca kurs pary walut.
   */
  public BigDecimal getExchangeRate(Currency c1, Currency c2) {
    synchronized (exchangeRatesMonitor) {
      return exchangeRates.stream()
              .filter(t -> (t.getLeft() == c1 && t.getMiddle() == c2))
              .findFirst()
              .get()
              .getRight();
    }
  }
  
  /**
   * Zwraca średni kurs waluty.
   * @param currency waluta
   * @return średni kurs
   */
  public BigDecimal getAverageRate(Currency currency) {
    synchronized (exchangeRatesMonitor) {
      return exchangeRates.stream()
              .filter(t -> (t.getLeft() == currency) && (t.getMiddle() != currency))
              .map(Triple::getRight)
              .reduce(BigDecimal.ZERO, (a, b) ->
                      a = a.add(b.divide(BigDecimal.valueOf(exchangeRates.size()), 2, RoundingMode.HALF_EVEN)));
    }
  }
  
  /**
   * Podwyższa kurs pierwszej waluty względem drugiej na bazie wielkości transakcji.
   * @param c1 pierwsza waluta
   * @param c2 druga waluta
   * @param amount wielkość transakcji
   */
  private void increaseRate(Currency c1, Currency c2, BigDecimal amount) {
    synchronized (exchangeRatesMonitor) {
      
      Triple<Currency, Currency, BigDecimal> triple = exchangeRates.stream()
              .filter(t -> (t.getLeft() == c1 && t.getMiddle() == c2))
              .findFirst()
              .get();
      
      exchangeRates.remove(triple);
      
      exchangeRates.add(new ImmutableTriple<>(c1, c2, triple.getRight()
              .add(amount.divide(BigDecimal.valueOf(500000), 2, RoundingMode.HALF_EVEN))));
    }
  }
  
  /**
   * Obniża kurs pierwszej waluty względem drugiej na bazie wielkości transakcji.
   * @param c1 pierwsza waluta
   * @param c2 druga waluta
   * @param amount wielkość transakcji
   */
  private void decreaseRate(Currency c1, Currency c2, BigDecimal amount) {
    synchronized (exchangeRatesMonitor) {
      Triple<Currency, Currency, BigDecimal> triple = exchangeRates.stream()
              .filter(t -> (t.getLeft() == c1 && t.getMiddle() == c2))
              .findFirst()
              .get();
  
      exchangeRates.remove(triple);
      
      exchangeRates.add(new ImmutableTriple<>(c1, c2, triple.getRight()
              .subtract(amount.divide(BigDecimal.valueOf(500000), 2, RoundingMode.HALF_EVEN))));
    }
  }
  
  @Override
  public void realizeBuying(TypeOfInvestor client, BigDecimal proposedSum, Asset asset) {
    
    Currency currency = (Currency) asset;
    Currency c1 = client.getCurrency();
    
    //inwestor nie kupuje waluty, której używa
    if (currency != c1) {
      
      //inwestor płaci
      client.decreaseBudget(proposedSum);
      
      //odliczenie marży
      BigDecimal netSum = getNetPrice(proposedSum);
      
      //przewalutowanie
      BigDecimal currencyAmount = netSum.multiply(getExchangeRate(c1, currency))
              .setScale(2, RoundingMode.HALF_EVEN);
      
      //rośnie kurs waluty
      increaseRate(currency, c1, currencyAmount);
      currency.registerNewValue(getAverageRate(currency));
      decreaseRate(c1, currency, currencyAmount);
      c1.registerNewValue(getAverageRate(c1));
      
      //inwestor otrzymuje zakupioną walutę
      client.addAsset(currency, currencyAmount);
      
      System.out.println(client + " kupuje " + currencyAmount + " x " + asset + " za " + proposedSum);
    }
  }
  
  @Override
  public void realizeSelling(TypeOfInvestor seller, BigDecimal amount, Asset asset) {
    
    Currency currency = (Currency) asset;
    Currency c1 = seller.getCurrency();
    
    //sprzedawca traci produkt
    seller.removeAsset(asset, amount);
    
    //przewalutowanie
    BigDecimal sum = amount.multiply(getExchangeRate(currency, c1)).setScale(2, RoundingMode.HALF_EVEN);
    
    //spada kurs waluty
    decreaseRate(currency, c1, amount);
    currency.registerNewValue(getAverageRate(currency));
    increaseRate(c1, currency, amount);
    c1.registerNewValue(getAverageRate(c1));
    
    //potrącenie marży
    BigDecimal netSum = getNetPrice(sum);
    
    //sprzedawca otrzymuje pieniądze
    seller.increaseBudget(netSum);
    
    System.out.println(seller + " sprzedaje " + amount + " x " + asset + " za " + netSum);
  }
}
