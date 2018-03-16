/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.util.Random;

/**
 * Pomocnicza klasa do losowania / wybierania nazwy kraji
 */
public class Country {

  private static final String[] countryNamesList = {
    "Mushroom Kingdom",
    "Hyrule",
    "Rozdroża Marzeń",
    "Kanto",
    "Tenochtitlán",
    "Kraj Simów",
    "Japonia",
    "USA",
    "Kaczogród",
    "Polska"
  };

  public static String[] getCountryNamesList() {
    return countryNamesList;
  }
  
  /**
   * @return losowa nazwa kraji
   */
  public static String getCountryName() {
    Random rand = new Random();
    return countryNamesList[rand.nextInt(countryNamesList.length)];
  }
  
  /**
   * @param i pozycja w tablicy
   * @return wybrana nazwa kraju
   */
  public static String getCountryName(int i) {
    if (i < countryNamesList.length) {
      return countryNamesList[i];
      
    } else {
      return Country.getCountryName();
    }
  }
}
