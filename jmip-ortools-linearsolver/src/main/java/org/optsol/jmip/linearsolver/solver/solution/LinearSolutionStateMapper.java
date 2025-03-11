package org.optsol.jmip.linearsolver.solver.solution;

import com.google.ortools.linearsolver.MPSolver;
import org.optsol.jmip.core.solver.solution.SolutionState;

public class LinearSolutionStateMapper {

  public static SolutionState convert(MPSolver.ResultStatus status) {

    switch (status) {
      case OPTIMAL:
        return SolutionState.OPTIMAL;
      case FEASIBLE:
        return SolutionState.FEASIBLE;
      case INFEASIBLE:
        return SolutionState.INFEASIBLE;
      case UNBOUNDED:
        return SolutionState.UNBOUNDED;
      case ABNORMAL:
      case MODEL_INVALID:
        return SolutionState.FAILURE;
      case NOT_SOLVED:
        return SolutionState.UNKNOWN;
      default:
        throw new IllegalStateException("Unknown Mapping for value: " + status);
    }
  }
}

