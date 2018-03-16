/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa reprezentująca inwestora
 */
public class Investor extends TypeOfInvestor implements Serializable {
  private long serialVersionUID = 1L;
  
  private String name,
          surname,
          PESEL;
  
  private static int i = 0;
  
  public Investor() {
    super();
    i++;
  }
  
  public Investor(Currency currency, SimulatorState simulatorState) {
    
    if (i < investorNamesList.length) {
      name = investorNamesList[i];
      surname = investorSurnamesList[i];
      
    } else {
      name = "Nieznany nikomu inwestor";
      surname = "Nr " + (i - investorNamesList.length + 1);
    }
    i++;
    
    String month = "0" + (random.nextInt(12) + 1);
    String day = "0" + (random.nextInt(28) + 1);
    
    //40 - 99 ; 1 - 12 ; 1 - 28 ; 10000 - 99999 ;
    PESEL = (random.nextInt(60) + 40) +
            month.substring(month.length() - 2) +
            day.substring(day.length() - 2) +
            (random.nextInt(89999) + 10000);
    
    this.currency = currency;
    this.investmentBudget = BigDecimal.valueOf((random.nextInt(999000000) + 1000000) / 100)
            .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    this.simulatorState = simulatorState;
    
    start();
  }
  
  /**
   * Zwraca posiadane jednostki uczestnictwa w postaci listy opisów typu String
   *
   * @return Opisowa lista posiadanych jednostek uczestnictwa
   */
  public List<String> getOwnUnits() {
    return ownAssets.keySet().stream()
            .filter(k -> k instanceof ParticipationUnit)
            .map(k -> k + " w ilości " + ownAssets.get(k))
            .collect(Collectors.toList());
  }
  
  public String toString() {
    return name + " " + surname;
  }
  
  public String getName() {
    return name;
  }
  
  public String getSurname() {
    return surname;
  }
  
  public String getPESEL() {
    return PESEL;
  }
  
}
