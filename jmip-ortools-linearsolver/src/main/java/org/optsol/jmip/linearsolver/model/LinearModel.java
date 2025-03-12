package org.optsol.jmip.linearsolver.model;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolutionResponse;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;
import java.time.Duration;
import java.time.LocalDateTime;
import org.optsol.jmip.core.model.AbstractModel;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.linearsolver.solver.solution.LinearSolutionStateMapper;
import org.optsol.jmip.core.solver.solution.SolutionState;

public abstract class LinearModel<CONSTANTS extends IConstants>
    extends AbstractModel<MPSolver, MPVariable, CONSTANTS> {
  static {
    Loader.loadNativeLibraries();
  }

  private MPSolver.ResultStatus resultStatus = MPSolver.ResultStatus.NOT_SOLVED;
  private MPSolutionResponse solutionResponse = null;
  private Duration solutionDuration = null;

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
