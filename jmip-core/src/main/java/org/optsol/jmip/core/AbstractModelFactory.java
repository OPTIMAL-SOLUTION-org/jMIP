package org.optsol.jmip.core;

import java.util.List;

public abstract class AbstractModelFactory<CONSTANTS, VARIABLE, SOLVER, MODEL> {
  protected abstract IVariableManager<? super CONSTANTS, SOLVER, VARIABLE> generateVarManager();

  protected abstract IObjectiveManager<
      ? super CONSTANTS, VARIABLE, SOLVER> generateObjective();

  protected abstract List<
      IConstraintManager<
          ? super CONSTANTS, VARIABLE,
          SOLVER>> generateConstraints();

  public abstract MODEL buildModel(CONSTANTS constants) throws Exception;
}
