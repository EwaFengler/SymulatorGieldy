/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import economy.SimulatorState;

/**
 *
 * @author ewa
 */
public abstract class PanelController {
  protected SimulatorState simulatorState;
  
  public void setSimulatorState(SimulatorState s) {
    simulatorState = s;
    getValuesFromSimulatorState();
  }

  protected abstract void getValuesFromSimulatorState();
}
