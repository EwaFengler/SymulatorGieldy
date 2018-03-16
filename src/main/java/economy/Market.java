/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Klasa reprezentująca rynek
 */
public abstract class Market implements Serializable {
  private long serialVersionUID = 1L;
  
  //wartość z zakresu 0.01 - 0.20, dokładność 3 msca po przecinku
  protected BigDecimal percentageMarkup = new BigDecimal(new Random().nextFloat() % 0.19 + 0.01)
          .setScale(3, RoundingMode.HALF_EVEN);
  
  public Market (){}
  
  /**
   * Funkcja realizująca kupno aktywa z rynku, wraz z konsekwencjami dla
   * kupującego, aktywa, oraz ewentualnie właściciela akcji / jednostki uczestnictwa.
   *
   * @param client klient
   * @param proposedSum proponowana przez klienta suma. Zostanie mu wydana reszta.
   * @param asset kupowane aktywo
   */
  public abstract void realizeBuying(TypeOfInvestor client, BigDecimal proposedSum, Asset asset);
  
  /**
   * Funkcja realizująca sprzedaż aktywa rynkowi, wraz z konsekwencjami dla
   * sprzedającego, aktywa, oraz ewentualnie właściciela akcji / jednostki uczestnictwa.
   * @param seller sprzedajęcy
   * @param amount ilość spredawanego aktywa
   * @param asset sprzedawane aktywo
   */
  public abstract void realizeSelling(TypeOfInvestor seller, BigDecimal amount, Asset asset);
  
  
  //kupno
  //zadania: 1.obliczenie kwoty (marża + kurs) 2. zabranie 3. dodanie 4. względem obiektu
  
  //sprzedaz
  //1. odejmij produkty 2. oblicz zysk (marża) 3. dodaj piniądze 4. względem obiektu

  
  
  //po zmniejszeniu
  public BigDecimal getNetPrice(BigDecimal gross) {
    return gross.subtract(gross.multiply(percentageMarkup)).setScale(2, RoundingMode.HALF_EVEN);
  }
  
  public BigDecimal getNetPrice(int gross) {
    return BigDecimal.valueOf(gross).subtract(BigDecimal.valueOf(gross).multiply(percentageMarkup)).setScale(2, RoundingMode.HALF_EVEN);
  }
  
  //przed zmniejszeniem
  public BigDecimal getGrossPrice(BigDecimal net) {
    return net.divide(BigDecimal.valueOf(1).subtract(percentageMarkup), 2, RoundingMode.HALF_EVEN);
  }
}
