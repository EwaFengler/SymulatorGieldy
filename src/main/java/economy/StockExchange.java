/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa realizująca giełdę.
 */
public class StockExchange extends Market implements Serializable {
  private long serialVersionUID = 1L;
  
  private final String name;
  private final String country;
  private final String city;
  private final String address;
  
  private final Currency currency;
  private List<StockIndex> stockIndexList = new ArrayList<>();
  
  public StockExchange(String name, String country, String city, String address, Currency currency, BigDecimal percentageMarkup) {
    this.name = name;
    this.country = country;
    this.city = city;
    this.address = address;
    this.currency = currency;
    this.percentageMarkup = percentageMarkup;
  }
  
  @Override
  public void realizeBuying(TypeOfInvestor client, BigDecimal proposedSum, Asset asset) {
    
    //ustalenie bezpiecznej kwoty, do której można jeszcze doliczyć marżę
    BigDecimal safeSum = getNetPrice(proposedSum);
    BigDecimal netSum, assetAmount;
    
    Currency c2, c1 = client.getCurrency();
    ForeignExchangeMarket forex = (ForeignExchangeMarket) c1.getMarket();
    
    if (asset instanceof Shareholding) {
      
      Shareholding shareholding = (Shareholding) asset;
      Company company = shareholding.getCompany();
      
      c2 = company.getCurrency();
      
      //Jeżeli klient ma inną walutę niż spółka, następuje przeliczenie pieniędzy inwestora na walutę spółki
      safeSum = safeSum.multiply(forex.getExchangeRate(c1, c2)).setScale(2, RoundingMode.HALF_EVEN);
      
      synchronized (company.getRateMonitor()) {
        synchronized (shareholding.getShareholdingMonitor()) {
          
          //pobranie kursu akcji i wyliczenie ile akcji może kupić inwestor
          BigDecimal currentRate = company.getCurrentRate();
          assetAmount = safeSum.divide(currentRate, 0, BigDecimal.ROUND_FLOOR);
          
          //jeżeli na rynku jest dostępne mniej akcji, może wykupić maksymalnie tyle akcji, ile jest na rynku
          if (shareholding.getNumOfShares() < assetAmount.intValue()) {
            assetAmount = BigDecimal.valueOf(shareholding.getNumOfShares());
          }
          
          //akcje są kupowane tylko jeżeli inwestor może to zrobić
          //if (assetAmount.compareTo(BigDecimal.ZERO) > 0) {
          
          //policzenie ile dostanie spółka
          netSum = assetAmount.multiply(currentRate).setScale(2, BigDecimal.ROUND_HALF_EVEN);
          
          //spółka zyskuje; zwiększa się obrót i wolumen
          company.increaseIncomeAndProfit(netSum);
          company.increaseTurnover(netSum);
          company.increaseTradingVolume(assetAmount.intValue());
          
          //rośnie kurs
          company.actualizeRate(shareholding.getNumOfShares() - assetAmount.intValue());
          
          //zmniejsza się ilość akcji na rynku
          shareholding.subtract(assetAmount.intValue());
        }
      }
    } else {
      
      ParticipationUnit participationUnit = (ParticipationUnit) asset;
      InvestmentFund investmentFund = participationUnit.getInvestmentFund();
      
      c2 = investmentFund.getCurrency();
      
      //Jeżeli klient ma inną walutę niż fundusz, następuje przeliczenie pieniędzy inwestora na walutę funduszu
      safeSum = safeSum.multiply(forex.getExchangeRate(c1, c2)).setScale(2, RoundingMode.HALF_EVEN);
      
      //pobranie wartości jednostki uczestnictwa i wyliczenie ile może kupić inwestor
      BigDecimal currentValue = participationUnit.getCurrentValue();
      assetAmount = safeSum.divide(currentValue, 2, RoundingMode.HALF_EVEN);
      
      //policzenie ile dostanie fundusz
      netSum = assetAmount.multiply(currentValue).setScale(2, BigDecimal.ROUND_HALF_EVEN);
      
      //fundusz zyskuje
      investmentFund.increaseBudget(netSum);
      
      //rośnie kurs
      participationUnit.increaseValue(assetAmount);
    }
    
    //policzenie ile zapłaci inwestor (po dodaniu marży)
    BigDecimal grossSum = getGrossPrice(netSum);
    
    System.out.println(client + " kupuje " + assetAmount + " x " + asset + " za " + netSum);
    
    //inwestor płaci w swojej walucie
    grossSum = grossSum.multiply(forex.getExchangeRate(c2, c1));
    client.decreaseBudget(grossSum);
    
    //inwestor otrzymuje zakupione aktywa
    client.addAsset(asset, assetAmount);
    
  }
  
  public void realizeSelling(TypeOfInvestor seller, BigDecimal amount, Asset asset) {
    
    //inwestor przestaje posiadać akcje
    seller.removeAsset(asset, amount);
    
    Currency c2, c1 = seller.getCurrency();
    ForeignExchangeMarket forex = (ForeignExchangeMarket) c1.getMarket();
    
    BigDecimal sum;
    
    if (asset instanceof Shareholding) {
      
      Shareholding shareholding = (Shareholding) asset;
      Company company = shareholding.getCompany();
      
      synchronized (shareholding.getShareholdingMonitor()) {
  
  
        //spada kurs
        company.actualizeRate(shareholding.getNumOfShares() + amount.intValue());
        
        //zwiększa się ilość akcji na rynku
        shareholding.add(amount.intValue());
        
        synchronized (company.getRateMonitor()) {
          //policzenie kwoty względem kursu
          sum = company.getCurrentRate().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
          
          c2 = company.getCurrency();
          
          //przewalutowanie
          sum = sum.multiply(forex.getExchangeRate(c2, c1)).setScale(2, RoundingMode.HALF_EVEN);
          
          //zwiększa się obrót i wolumen
          company.increaseTurnover(sum);
          company.increaseTradingVolume(amount.intValue());
        }
      }
      
    } else {
      
      ParticipationUnit participationUnit = (ParticipationUnit) asset;
      InvestmentFund investmentFund = participationUnit.getInvestmentFund();
  
      //spada kurs
      participationUnit.decreaseValue(amount);
      
      //policzenie kwoty względem kursu
      sum = participationUnit.getCurrentValue().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
      
      c2 = investmentFund.getCurrency();
      
      //przewalutowanie
      sum = sum.multiply(forex.getExchangeRate(c2, c1)).setScale(2, RoundingMode.HALF_EVEN);
    }
    
    //potrącenie marży
    sum = getNetPrice(sum);
    
    //zapłacenie sprzedawcy
    seller.increaseBudget(sum);
    
    System.out.println(seller + " sprzedaje " + amount + " x " + asset + " za " + sum);
  }
  
  /**
   * Dodaje indeks do giełdy.
   * @param stockIndex indeks
   */
  public void addIndex(StockIndex stockIndex) {
    stockIndexList.add(stockIndex);
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  public String getName() {
    return name;
  }
  
  public String getCountry() {
    return country;
  }
  
  public String getCity() {
    return city;
  }
  
  public String getAddress() {
    return address;
  }
  
  public Currency getCurrency() {
    return currency;
  }
  
  public BigDecimal getPercentageMarkup() {
    return percentageMarkup;
  }
  
  /**
   * Zwraca listę indeksów w postaci String
   * @return opisowa lista indeksów
   */
  public String getStockIndexListString() {
    StringBuilder stockIndexListString = new StringBuilder();
    if (!stockIndexList.isEmpty()) {
      for (StockIndex stockIndex : stockIndexList) {
        stockIndexListString.append(stockIndex.toString()).append(", ");
      }
      stockIndexListString.setLength(stockIndexListString.length() - 2);
      
      return stockIndexListString.toString();
    }
    
    return "";
  }
}
