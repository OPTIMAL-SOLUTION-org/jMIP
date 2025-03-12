package org.optsol.jmip.core.model;

import java.time.Duration;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.solver.solution.SolutionState;

public interface IModel<SOLVER, VARCLASS, CONSTANTS extends IConstants> {
  void initModel(SOLVER solver) throws Exception;

  void buildOrUpdate(CONSTANTS constants) throws Exception;

  Double getObjectiveValue() throws Exception;

  Double getBestObjectiveBound() throws Exception;

  SolutionState getSolutionState() throws Exception;

  Duration getSolutionTime() throws Exception;

  SOLVER getSolver();

  org.optsol.jmip.core.model.variables.IVariable<? super CONSTANTS, SOLVER, VARCLASS> getVariables();

  org.optsol.jmip.core.model.objective.IObjective<? super CONSTANTS, VARCLASS, SOLVER> getObjective();

  java.util.Set<org.optsol.jmip.core.model.constraints.IConstraint<? super CONSTANTS, VARCLASS,
      SOLVER>> getConstraints();
}
