/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa reprezentująca stan symulatora.
 * Klasa zarządza listami elementów giełdy.
 */
public class SimulatorState implements Serializable {
  private long serialVersionUID = 1L;
  
  private transient StringProperty panel;
  
  private transient ObservableList<InvestmentFund> investmentFundList;
  private transient ListProperty<InvestmentFund> investmentFundListProperty;
  
  private transient ObservableList<StockExchange> stockExchangeList;
  private transient ListProperty<StockExchange> stockExchangeListProperty;
  
  private transient ObservableList<StockIndex> stockIndexList;
  private transient ListProperty<StockIndex> stockIndexListProperty;
  
  private transient ObservableList<Investor> investorList;
  private transient ListProperty<Investor> investorListProperty;
  
  private transient ObservableList<Company> companyList;
  private transient ListProperty<Company> companyListProperty;
  
  private transient ObservableList<Feedstock> feedstockList;
  private transient ListProperty<Feedstock> feedstockListProperty;
  
  private transient ObservableList<Currency> currencyList;
  private transient ListProperty<Currency> currencyListProperty;
  
  private ForeignExchangeMarket foreignExchangeMarket;
  private FeedstockMarket feedstockMarket;
  
  private transient ObservableList<Asset> assetList;
  private transient ListProperty<Asset> assetListProperty;
  
  private static final Random random = new Random();
  
  public SimulatorState() {
    panel = new SimpleStringProperty("");
    
    investmentFundListProperty = new SimpleListProperty<>();
    investmentFundList = FXCollections.observableArrayList(new ArrayList<>());
    investmentFundListProperty.set(investmentFundList);
    
    stockExchangeListProperty = new SimpleListProperty<>();
    stockExchangeList = FXCollections.observableArrayList(new ArrayList<>());
    stockExchangeListProperty.set(stockExchangeList);
    
    stockIndexListProperty = new SimpleListProperty<>();
    stockIndexList = FXCollections.observableArrayList(new ArrayList<>());
    stockIndexListProperty.set(stockIndexList);
    
    investorListProperty = new SimpleListProperty<>();
    investorList = FXCollections.observableArrayList(new ArrayList<>());
    investorListProperty.set(investorList);
    
    companyListProperty = new SimpleListProperty<>();
    companyList = FXCollections.observableArrayList(new ArrayList<>());
    companyListProperty.set(companyList);
    
    feedstockListProperty = new SimpleListProperty<>();
    feedstockList = FXCollections.observableArrayList(new ArrayList<>());
    feedstockListProperty.set(feedstockList);
    
    currencyListProperty = new SimpleListProperty<>();
    currencyList = FXCollections.observableArrayList(new ArrayList<>());
    currencyListProperty.set(currencyList);
    
    assetListProperty = new SimpleListProperty<>();
    assetList = FXCollections.observableArrayList(new ArrayList<>());
    assetListProperty.set(assetList);
    
    foreignExchangeMarket = new ForeignExchangeMarket(this);
    feedstockMarket = new FeedstockMarket();
  }
  
  /**
   * Ustawia panel reprezentowany daną nazwą.
   *
   * @param s nazwa panelu
   */
  public void setPanel(String s) {
    panel.set(s);
  }
  
  public StringProperty getPanel() {
    return panel;
  }
  
  public void addAsset(Asset asset) {
    synchronized (assetList) {
      assetList.add(asset);
    }
  }
  
  private void removeAsset(Asset asset) {
    synchronized (assetList) {
      assetList.remove(asset);
    }
  }
  
  public void addFund(InvestmentFund investmentFund) {
    investmentFundList.add(investmentFund);
    addAsset(investmentFund.getParticipationUnit());
  }
  
  public void removeFund(InvestmentFund investmentFund) {
    investmentFundList.remove(investmentFund);
    removeAsset(investmentFund.getParticipationUnit());
    investmentFund.stop();
  }
  
  public void addStockExchange(StockExchange stockExchange) {
    stockExchangeList.add(stockExchange);
  }
  
  /**
   * Usuwa podaną giełdę oraz przynależne do niej spółki.
   *
   * @param stockExchange giełda
   */
  public void removeStockExchange(StockExchange stockExchange) {
    stockExchangeList.remove(stockExchange);
    companyList.stream()
            .filter(c -> c.getStockExchange() == stockExchange)
            .forEach(company -> {
              company.stop();
              assetList.remove(company.getShareholding());
            });
    
    companyList.removeIf(c -> c.getStockExchange() == stockExchange);
  }
  
  public void addIndex(StockIndex stockIndex) {
    stockIndexList.add(stockIndex);
  }
  
  public void removeIndex(StockIndex stockIndex) {
    stockIndexList.remove(stockIndex);
  }
  
  public void addInvestor(Investor investor) {
    investorList.add(investor);
  }
  
  public void removeInvestor(Investor investor) {
    investorList.remove(investor);
    investor.stop();
  }
  
  public void addCompany(Company company) {
    companyList.add(company);
    addRandomTypeOfInvestor(3);
    
    if (companyList.size() == 1) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      //alert.setTitle("Information Dialog");
      alert.setHeaderText(null);
      alert.setContentText("Astrologowie ogłaszają tydzień akcji!\nPopulacja inwestorów zwiększa się.");
      
      alert.showAndWait();
    }
  }
  
  /**
   * Usuwa spółkę z rynku.
   * Usuwa spółkę z listy spółek indeksów.
   * Jeżeli była to ostatnia spółka i indeks nie był oparty na warunku,
   * indeks jest usuwany.
   *
   * @param company usuwana spółka
   */
  public void removeCompany(Company company) {
    company.stop();
    removeAsset(company.getShareholding());
    companyList.remove(company);
    
    stockIndexList.stream()
            .filter(i -> !i.isDynamic() && i.getCompanyList().contains(company))
            .forEach(i -> i.getCompanyList().remove(company));
    
    stockIndexList.removeIf(i -> !i.isDynamic() && i.getCompanyList().isEmpty());
  }
  
  public void addFeedstock(Feedstock feedstock) {
    feedstockList.add(feedstock);
    assetList.add(feedstock);
    addRandomTypeOfInvestor(1);
  }
  
  public void removeFeedstock(Feedstock feedstock) {
    feedstockList.remove(feedstock);
    removeAsset(feedstock);
  }
  
  public void addCurrency(Currency currency) {
    synchronized (currencyList) {
      currencyList.add(currency);
      assetList.add(currency);
      foreignExchangeMarket.addCurrencyToMarket(currency);
    }
    addRandomTypeOfInvestor(1);
  }
  
  public void removeCurrency(Currency currency) {
    synchronized (currencyList) {
      currencyList.remove(currency);
      removeAsset(currency);
      //usunięcie informacji o kursie waluty spowoduje błąd gdy inwestor będzie próbował sprzedać nieistniejącą już walutę
      //foreignExchangeMarket.removeCurrencyFromMarket(currency);
    }
  }
  
  public ListProperty<InvestmentFund> getInvestmentFundListProperty() {
    return investmentFundListProperty;
  }
  
  public ListProperty<StockExchange> getStockExchangeListProperty() {
    return stockExchangeListProperty;
  }
  
  public ListProperty<StockIndex> getStockIndexListProperty() {
    return stockIndexListProperty;
  }
  
  public ListProperty<Investor> getInvestorListProperty() {
    return investorListProperty;
  }
  
  public ListProperty<Company> getCompanyListProperty() {
    return companyListProperty;
  }
  
  public ListProperty<Feedstock> getFeedstockListProperty() {
    return feedstockListProperty;
  }
  
  public ListProperty<Currency> getCurrencyListProperty() {
    return currencyListProperty;
  }
  
  public ListProperty<Asset> getAssetListPropertyProperty() {
    return assetListProperty;
  }
  
  public ObservableList<Company> getCompanyList() {
    return companyList;
  }
  
  /**
   * @return losowa waluta
   */
  public Currency getRandomCurrency() {
    synchronized (currencyList) {
      return currencyList.get(random.nextInt(currencyList.size()));
    }
  }
  
  /**
   * @return losowa giełda
   */
  public StockExchange getRandomStockExchange() {
    return stockExchangeList.get(random.nextInt((stockExchangeList.size())));
  }
  
  /**
   * @return losowe aktywo
   */
  public Asset getRandomAsset() {
    synchronized (assetList) {
      if (assetList.size() > 0) {
        return assetList.get(random.nextInt((assetList.size())));
      }
      return null;
    }
  }
  
  public FeedstockMarket getFeedstockMarket() {
    return feedstockMarket;
  }
  
  public ForeignExchangeMarket getForeignExchangeMarket() {
    return foreignExchangeMarket;
  }
  
  /**
   * Dodaje losowego inwestora / fundusz podaną ilość razy.
   * Jeżeli nie istnieje giełda, losuje tylko inwestorów.
   *
   * @param amount ilu inwestorów i funduszy
   */
  private void addRandomTypeOfInvestor(int amount) {
    for (int i = 0; i < amount; i++) {
      if (stockExchangeList.isEmpty()) {
        addInvestor(new Investor(getRandomCurrency(), this));
      } else {
        TypeOfInvestor typeOfInvestor = TypeOfInvestor.randomTypeOfInvestor(getRandomCurrency(), this);
        if (typeOfInvestor instanceof Investor) {
          addInvestor((Investor) typeOfInvestor);
        } else {
          addFund((InvestmentFund) typeOfInvestor);
        }
      }
    }
  }
  
  public void copySimulatorState(SimulatorState newSimulatorState) {
    investmentFundList.forEach(TypeOfInvestor::stop);
    investmentFundList.clear();
    investmentFundList.addAll(newSimulatorState.getInvestmentFundListProperty().get());
    investmentFundList.forEach(TypeOfInvestor::start);
    
    stockExchangeList.clear();
    stockExchangeList.addAll(newSimulatorState.getStockExchangeListProperty().get());
    
    stockIndexList.clear();
    stockIndexList.addAll(newSimulatorState.getStockIndexListProperty().get());
    
    investorList.forEach(TypeOfInvestor::stop);
    investorList.clear();
    investorList.addAll(newSimulatorState.getInvestorListProperty().get());
    investorList.forEach(TypeOfInvestor::start);
    
    feedstockList.clear();
    feedstockList.addAll(newSimulatorState.getFeedstockListProperty().get());
    
    currencyList.clear();
    currencyList.addAll(newSimulatorState.getCurrencyListProperty().get());
    
    assetList.clear();
    assetListProperty.addAll(newSimulatorState.getAssetListPropertyProperty().get());
    
    feedstockMarket = newSimulatorState.getFeedstockMarket();
    foreignExchangeMarket = newSimulatorState.getForeignExchangeMarket();
    
    companyList.forEach(Company::stop);
    companyList.clear();
    companyList.addAll(newSimulatorState.getCompanyListProperty().get());
    
    companyList.forEach(c -> c.setShareholding(
            assetList.stream()
                    .filter(a -> a instanceof Shareholding)
                    .map(a -> ((Shareholding) a))
                    .filter(s -> s.getCompany() == c)
                    .findFirst()
                    .get()
            )
    );
    
    companyList.forEach(Company::start);
  }
  
  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
    
    oos.writeObject(new ArrayList<>(investmentFundList));
    oos.writeObject(new ArrayList<>(stockExchangeList));
    oos.writeObject(new ArrayList<>(stockIndexList));
    oos.writeObject(new ArrayList<>(investorList));
    oos.writeObject(new ArrayList<>(companyList));
    oos.writeObject(new ArrayList<>(feedstockList));
    oos.writeObject(new ArrayList<>(currencyList));
    oos.writeObject(new ArrayList<>(assetList));
  }
  
  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    panel = new SimpleStringProperty("");
    
    investmentFundListProperty = new SimpleListProperty<>();
    investmentFundList = FXCollections.observableArrayList((List<InvestmentFund>) ois.readObject());
    investmentFundListProperty.set(investmentFundList);
    
    stockExchangeListProperty = new SimpleListProperty<>();
    stockExchangeList = FXCollections.observableArrayList((List<StockExchange>) ois.readObject());
    stockExchangeListProperty.set(stockExchangeList);
    
    stockIndexListProperty = new SimpleListProperty<>();
    stockIndexList = FXCollections.observableArrayList((List<StockIndex>) ois.readObject());
    stockIndexListProperty.set(stockIndexList);
    
    investorListProperty = new SimpleListProperty<>();
    investorList = FXCollections.observableArrayList((List<Investor>) ois.readObject());
    investorListProperty.set(investorList);
    
    companyListProperty = new SimpleListProperty<>();
    companyList = FXCollections.observableArrayList((List<Company>) ois.readObject());
    companyListProperty.set(companyList);
    
    feedstockListProperty = new SimpleListProperty<>();
    feedstockList = FXCollections.observableArrayList((List<Feedstock>) ois.readObject());
    feedstockListProperty.set(feedstockList);
    
    currencyListProperty = new SimpleListProperty<>();
    currencyList = FXCollections.observableArrayList((List<Currency>) ois.readObject());
    currencyListProperty.set(currencyList);
    
    assetListProperty = new SimpleListProperty<>();
    assetList = FXCollections.observableArrayList((List<Asset>) ois.readObject());
    assetListProperty.set(assetList);
  }
}
