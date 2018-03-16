/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.math.BigDecimal;

/**
 * Klasa reprezentująca akcje.
 */
public class Shareholding extends Asset {

  private final Company company;
  private volatile int numOfShares;
  private final Monitor shareholdingMonitor = new Monitor();
  
  public Shareholding(Company company, int numOfShares, Market market) {
    this.company = company;
    this.numOfShares = numOfShares;
    this.market = market;
    
    registerNewValue(company.getCurrentRate());
  }
  
  @Override
  public String toString() {
    return "Akcje spółki " + company;
  }
  
  /**
   * Zwiększa o podaną liczbę ilość akcji na rynku
   * @param num ile akcji więcej
   */
  public void add(int num){
    numOfShares += num;
  }
  
  /**
   * Zmniejsza o podaną liczbę ilość akcji na rynku
   * @param num ile akcji mniej
   */
  public void subtract(int num){
    numOfShares -= num;
  }
  
  public Company getCompany() {
    return company;
  }
  
  public int getNumOfShares() {
    return numOfShares;
  }
  
  public Monitor getShareholdingMonitor() {
    return shareholdingMonitor;
  }
  
  @Override
  public BigDecimal getCurrentValue() {
    return company.getCurrentRate();
  }
}
