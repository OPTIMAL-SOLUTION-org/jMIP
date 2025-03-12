package org.optsol.jmip.core.model.objective;


import org.optsol.jmip.core.model.variables.IVariableProvider;

public interface IObjective<CONSTANTS, VARCLASS, SOLVER> {
  void createAndAddOrUpdateObjective(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables) throws Exception;
}
