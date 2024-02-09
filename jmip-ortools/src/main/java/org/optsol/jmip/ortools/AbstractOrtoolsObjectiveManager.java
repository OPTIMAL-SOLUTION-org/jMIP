package org.optsol.jmip.ortools;

import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.optsol.jmip.core.AbstractObjectiveManager;
import org.optsol.jmip.core.IConstants;
import org.optsol.jmip.core.IVariableProvider;

public abstract class AbstractOrtoolsObjectiveManager<CONSTANTS extends IConstants>
    extends AbstractObjectiveManager<MPSolver, CONSTANTS, MPVariable, MPObjective> {

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
