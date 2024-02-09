package org.optsol.jmip.core;


public interface IObjectiveManager<CONSTANTS, VARCLASS, SOLVER> {
  void createAndAddOrUpdateObjective(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables) throws Exception;
}
