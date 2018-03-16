/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa realizująca indeks giełdowy.
 */
public class StockIndex implements Serializable {
  private long serialVersionUID = 1L;
  
  private String name;
  private StockExchange stockExchange;
  private boolean isDynamic;
  
  private List<Company> companyList = new ArrayList<>();
  private int maxNumOfCompanies;
  
  private SimulatorState simulatorState;
  
  public StockIndex() {}
  
  public StockIndex(String name, ObservableList<Company> companyList, StockExchange stockExchange, SimulatorState simulatorState) {
    this.name = name;
    this.stockExchange = stockExchange;
    this.simulatorState = simulatorState;
    this.companyList.addAll(companyList);
    isDynamic = false;
  }
  
  public StockIndex(String name, int maxNumOfCompanies, StockExchange stockExchange, SimulatorState simulatorState) {
    this.name = name;
    this.stockExchange = stockExchange;
    this.simulatorState = simulatorState;
    this.maxNumOfCompanies = maxNumOfCompanies;
    isDynamic = true;
  }
  
  public String toString() {
    return name;
  }
  
  /**
   * @return wynik indeksu
   */
  public BigDecimal getScore() {
    return getCompanyList()
            .stream()
            .map(Company::getCurrentRate)
            .reduce(BigDecimal.ZERO, (a, b) -> a = a.add(b));
  }
  
  public String getName() {
    return name;
  }
  
  public StockExchange getStockExchange() {
    return stockExchange;
  }
  
  public boolean isDynamic() {
    return isDynamic;
  }
  
  /**
   * Zależnie od tego, czy indeks jest oparty na warunku czy nie,
   * zwraca albo do maxNumOfCompanies największych spółek albo
   * zapisane spółki.
   * @return lista spółek
   */
  public List<Company> getCompanyList() {
    if (isDynamic) {
      return simulatorState.getCompanyList().stream()
              .sorted()
              .limit(maxNumOfCompanies)
              .collect(Collectors.toList());
    }
    
    return companyList;
  }
  
  /**
   * Zwraca listę spółek w postaci Stringa
   * @return opisowa lista spółek
   */
  public String getCompanyListString() {
    List<Company> list;
    
    list = getCompanyList();
    
    if (!list.isEmpty()) {
      StringBuilder companyListString = new StringBuilder();
      for (Company company : list) {
        companyListString.append(company.toString()).append(", ");
      }
      companyListString.setLength(companyListString.length() - 2);
      
      return companyListString.toString();
    }
    return "";
  }
}
