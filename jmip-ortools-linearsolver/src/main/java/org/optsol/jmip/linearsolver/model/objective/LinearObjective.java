package org.optsol.jmip.linearsolver.model.objective;

import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.optsol.jmip.core.model.objective.AbstractObjective;
import org.optsol.jmip.core.model.variables.IVariableProvider;
import org.optsol.jmip.core.model.constants.IConstants;

public abstract class LinearObjective<CONSTANTS extends IConstants>
    extends AbstractObjective<MPSolver, CONSTANTS, MPVariable, MPObjective> {

  @Override
  protected final MPObjective generateObjective(
      MPSolver solver,
      CONSTANTS constants,
      IVariableProvider<MPVariable> variables) throws Exception {

    MPObjective objective = solver.objective();//cannot set name

    configureObjective(
        objective,
        constants,
        variables);

    return objective;
  }

  protected abstract void configureObjective(
      MPObjective objective,
      CONSTANTS constants,
      IVariableProvider<MPVariable> variables) throws Exception;

  @Override
  protected final void removeObjective(
      MPSolver solver,
      MPObjective objective) throws Exception {
    objective.delete();
  }
}
