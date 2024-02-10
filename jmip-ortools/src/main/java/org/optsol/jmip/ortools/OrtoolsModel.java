package org.optsol.jmip.ortools;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolutionResponse;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import org.optsol.jmip.core.AbstractModel;
import org.optsol.jmip.core.IConstants;
import org.optsol.jmip.core.IConstraintManager;
import org.optsol.jmip.core.IObjectiveManager;
import org.optsol.jmip.core.IVariableManager;
import org.optsol.jmip.core.SolutionState;

public abstract class OrtoolsModel<CONSTANTS extends IConstants>
    extends AbstractModel<MPSolver, MPVariable, CONSTANTS> {

  static {
    Loader.loadNativeLibraries();
  }

  private MPSolver.ResultStatus resultStatus = MPSolver.ResultStatus.NOT_SOLVED;
  private MPSolutionResponse solutionResponse = null;
  private Duration solutionDuration = null;

  public OrtoolsModel(
      String solverName,
      IVariableManager<? super CONSTANTS, MPSolver, MPVariable> variables,
      IObjectiveManager<? super CONSTANTS, MPVariable, MPSolver> objective,
      Collection<IConstraintManager<? super CONSTANTS, MPVariable, MPSolver>> constraints)
      throws Exception {
    super(
        MPSolver.createSolver(solverName),
        variables,
        objective,
        constraints);
  }

  @Override
  public boolean solve() throws Exception {
    resultStatus = MPSolver.ResultStatus.NOT_SOLVED;
    solutionResponse = null;
    solutionDuration = null;

    // INVOKE SOLVER
    LocalDateTime startTime = LocalDateTime.now();
    resultStatus = getSolver().solve();
    solutionDuration = Duration.between(startTime, LocalDateTime.now());

    // EXTRACT SOLUTION RESPONSE
    solutionResponse = getSolver().createSolutionResponseProto();

    // Check that the problem has an optimal solution.
    if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
      return false;
    }

    return true;
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
    return OrtoolsStatusMapper.convert(resultStatus);
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
