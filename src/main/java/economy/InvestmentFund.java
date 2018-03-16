/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Klasa reprezentująca fundusz inwestycyjny
 */
public class InvestmentFund extends TypeOfInvestor implements Serializable {
  private long serialVersionUID = 1L;
  
  private String investmentManagerName,
                 investmentManagerSurname;

  private ParticipationUnit participationUnit;
  
  private int j;
  private static int i = 0;
  
  public InvestmentFund(){
    super();
    i++;
  }
  
  public InvestmentFund(Currency currency, SimulatorState simulatorState) {
    j = ++i;
    
    int idx = random.nextInt(investorNamesList.length);
    investmentManagerName = investorNamesList[idx];
    investmentManagerSurname = investorSurnamesList[idx];
  
    this.currency = currency;
    this.investmentBudget = BigDecimal.valueOf((random.nextInt(999000000)+ 1000000)/100)
            .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    this.simulatorState = simulatorState;
  
    participationUnit = new ParticipationUnit(this, simulatorState.getRandomStockExchange());
    
    start();
  }
  
  public String toString(){
    return "Fundusz nr " + j + " zarządzany przez: " + investmentManagerName + " " + investmentManagerSurname;
  }
  
  public String getInvestmentManagerName() {
    return investmentManagerName;
  }
  
  public String getInvestmentManagerSurname() {
    return investmentManagerSurname;
  }
  
  public ParticipationUnit getParticipationUnit() {
    return participationUnit;
  }
}
