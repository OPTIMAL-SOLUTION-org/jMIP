package org.optsol.jmip.core.model;

import java.time.Duration;
import java.util.Set;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.model.constraints.IConstraint;
import org.optsol.jmip.core.model.objective.IObjective;
import org.optsol.jmip.core.model.variables.IVariable;
import org.optsol.jmip.core.solver.solution.SolutionState;

public interface IModel<SOLVER, VARCLASS, CONSTANTS extends IConstants> {
  void initModel(SOLVER solver) throws Exception;

  void buildOrUpdate(CONSTANTS constants) throws Exception;

  Double getObjectiveValue() throws Exception;

  Double getBestObjectiveBound() throws Exception;

  SolutionState getSolutionState() throws Exception;

  Duration getSolutionTime() throws Exception;

  SOLVER getSolver();

  IVariable<? super CONSTANTS, SOLVER, VARCLASS> getVariables();

  IObjective<? super CONSTANTS, VARCLASS, SOLVER> getObjective();

  Set<IConstraint<? super CONSTANTS, VARCLASS,
        SOLVER>> getConstraints();
}
