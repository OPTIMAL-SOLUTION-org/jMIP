package org.optsol.jmip.core.model;

import java.util.List;
import org.optsol.jmip.core.model.constraints.IConstraint;
import org.optsol.jmip.core.model.objective.IObjective;
import org.optsol.jmip.core.model.variables.IVariable;

public abstract class AbstractModelFactory<CONSTANTS, VARIABLE, SOLVER, MODEL> {
  protected abstract IVariable<? super CONSTANTS, SOLVER, VARIABLE> generateVariables();

  protected abstract IObjective<
      ? super CONSTANTS, VARIABLE, SOLVER> generateObjective();

  protected abstract List<
      IConstraint<
          ? super CONSTANTS, VARIABLE,
          SOLVER>> generateConstraints();

  public abstract MODEL buildModel(
      CONSTANTS constants,
      SOLVER solver) throws Exception;
}
