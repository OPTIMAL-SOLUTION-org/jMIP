package org.optsol.jmip.linearsolver.model;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolutionResponse;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import org.optsol.jmip.core.model.AbstractModel;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.model.constraints.IConstraint;
import org.optsol.jmip.core.model.objective.IObjective;
import org.optsol.jmip.core.model.variables.IVariable;
import org.optsol.jmip.linearsolver.solver.solution.LinearSolutionStateMapper;
import org.optsol.jmip.core.solver.solution.SolutionState;

public final class LinearModel<CONSTANTS extends IConstants>
    extends AbstractModel<MPSolver, MPVariable, CONSTANTS> {

  private MPSolver.ResultStatus resultStatus = MPSolver.ResultStatus.NOT_SOLVED;
  private MPSolutionResponse solutionResponse = null;
  private Duration solutionDuration = null;

  public LinearModel(
      MPSolver solver,
      IVariable<? super CONSTANTS, MPSolver, MPVariable> variables,
      IObjective<? super CONSTANTS, MPVariable, MPSolver> objective,
      Collection<IConstraint<? super CONSTANTS, MPVariable, MPSolver>> constraints)
      throws Exception {
    super(
        solver,
        variables,
        objective,
        constraints);
  }

  public boolean solve(MPSolverParameters solverParams) {
    resultStatus = MPSolver.ResultStatus.NOT_SOLVED;
    solutionResponse = null;
    solutionDuration = null;

    // INVOKE SOLVER
    LocalDateTime startTime = LocalDateTime.now();
    resultStatus = getSolver().solve(solverParams);
    solutionDuration = Duration.between(startTime, LocalDateTime.now());

    // EXTRACT SOLUTION RESPONSE
    solutionResponse = getSolver().createSolutionResponseProto();

    // Check that the problem has an optimal solution.
    return resultStatus == MPSolver.ResultStatus.OPTIMAL;
  }

  @Override
  public Double getObjectiveValue() throws Exception {
    if (solutionResponse != null && solutionResponse.hasObjectiveValue()) {
      return solutionResponse.getObjectiveValue();
    }
    return null;
  }

  @Override
  public SolutionState getSolutionState() throws Exception {
    return LinearSolutionStateMapper.convert(resultStatus);
  }

  @Override
  public Double getBestObjectiveBound() throws Exception {
    if (solutionResponse != null && solutionResponse.hasBestObjectiveBound()) {
      return solutionResponse.getBestObjectiveBound();
    }
    return null;
  }

  @Override
  public Duration getSolutionTime() throws Exception {
    return solutionDuration;
  }
}
