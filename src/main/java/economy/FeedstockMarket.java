/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Klasa reprezentująca rynek surowców
 */
public class FeedstockMarket extends Market implements Serializable{
  private long serialVersionUID = 1L;
  
  public FeedstockMarket() {  }
  
  @Override
  public void realizeBuying(TypeOfInvestor client, BigDecimal proposedSum, Asset asset) {
  
    Feedstock feedstock = (Feedstock) asset;
  
    Currency c1 = client.getCurrency();
    Currency c2 = feedstock.getCurrency();
    ForeignExchangeMarket forex = (ForeignExchangeMarket) c1.getMarket();
    
    //inwestor płaci
    client.decreaseBudget(proposedSum);
  
    //odliczenie marży
    BigDecimal netSum = getNetPrice(proposedSum);
  
    //przewalutowanie
    netSum = netSum.multiply(forex.getExchangeRate(c1, c2)).setScale(2, RoundingMode.HALF_EVEN);
    
    //policzenie ile surowca zostanie kupione
    BigDecimal amount = netSum.divide(feedstock.getCurrentValue(), 2, RoundingMode.HALF_EVEN);
    
    //aktualizacja wartości
    feedstock.increaseValue(amount);
    
    //inwestor otrzymuje zakupiony surowiec
    client.addAsset(feedstock, amount);
  
    System.out.println(client + " kupuje " + amount + " x " + asset + " za " + proposedSum);
  }
  
  @Override
  public void realizeSelling(TypeOfInvestor seller, BigDecimal amount, Asset asset) {
  
    Feedstock feedstock = (Feedstock) asset;
    
    //sprzedawca traci produkt
    seller.removeAsset(asset, amount);
  
    //aktualizacja wartości
    feedstock.decreaseValue(amount);
  
    Currency c1 = seller.getCurrency();
    Currency c2 = feedstock.getCurrency();
    ForeignExchangeMarket forex = (ForeignExchangeMarket) c1.getMarket();
  
    //policzenie kwoty względem ceny
    BigDecimal sum = feedstock.getCurrentValue().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
  
    //przewalutowanie
    sum = sum.multiply(forex.getExchangeRate(c2, c1)).setScale(2, RoundingMode.HALF_EVEN);
  
    //potrącenie marży
    sum = getNetPrice(sum);
  
    //zapłacenie sprzedawcy
    seller.increaseBudget(sum);
  
    System.out.println(seller + " sprzedaje " + amount + " x " + asset + " za " + sum);
  }
}
