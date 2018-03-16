/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca walutę.
 */
public class Currency extends Asset implements Serializable {
  private long serialVersionUID = 1L;
  
  private String name;
  private List<String> countryList;

  private static int i = 0;
  private static final String[] currencyNames = {
    "Mushroom Coins",
    "Rupees",
    "Czaszkonety",
    "Jeny",
    "Ziarna Kakao",
    "Simoleony"
  };
  
  public Currency(){
    i++;
  }
  
  public Currency(Market market) {
    countryList = new ArrayList<>();
    String countryName;
    
    this.market = market;
    
    if (i < currencyNames.length) {
      name = currencyNames[i];
      
      //waluta pochodzi z kraju z którego została zaczerpnięta nazwa
      countryList.add(Country.getCountryName(i));
      
    } else {
      name = "Lokalna Waluta Nr " + (i - currencyNames.length + 1);
      countryList.add(Country.getCountryName());
    }
    
    //losujemy do dwóch dodatkowych krajów
    countryName = Country.getCountryName();
    if (!countryList.contains(countryName)){
      countryList.add(countryName);
    }
    
    countryName = Country.getCountryName();
    if (!countryList.contains(countryName)){
      countryList.add(countryName);
    }
    
    i++;
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  public String getName(){
    return name;
  }
  
  /**
   * @return lista krajów w postaci Stringa
   */
  public String getCountryListString() {
    StringBuilder countryListString = new StringBuilder();
    
    for (String s : countryList) {
      countryListString.append(s).append(", ");
    }
    countryListString.setLength(countryListString.length() - 2);
    
    return countryListString.toString();
  }
  
  @Override
  public BigDecimal getCurrentValue() {
    return ((ForeignExchangeMarket) market).getAverageRate(this);
  }
}
