package controller;

import economy.SimulatorState;

public abstract class InfoBoxController {
  
  protected SimulatorState simulatorState;
  
  public void setSimulatorState(SimulatorState simulatorState) {
    this.simulatorState = simulatorState;
  }
  
  public abstract void setEconomyElement(Object economyElement);
}

