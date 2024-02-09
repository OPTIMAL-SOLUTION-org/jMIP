package org.optsol.jmip.ortools;

import com.google.ortools.linearsolver.MPSolver;
import org.optsol.jmip.core.SolutionState;

public class OrtoolsStatusMapper {
  public static SolutionState convert(
      MPSolver.ResultStatus status) {
    if (status.equals(MPSolver.ResultStatus.NOT_SOLVED)) {
      return SolutionState.UNKNOWN;
    }
    if (status.equals(MPSolver.ResultStatus.FEASIBLE)) {
      return SolutionState.FEASIBLE;
    }
    if (status.equals(MPSolver.ResultStatus.ABNORMAL)) {
      return SolutionState.FAILURE;
    }
    if (status.equals(MPSolver.ResultStatus.OPTIMAL)) {
      return SolutionState.OPTIMAL;
    }
    if (status.equals(MPSolver.ResultStatus.INFEASIBLE)) {
      return SolutionState.INFEASIBLE;
    }
    if (status.equals(MPSolver.ResultStatus.UNBOUNDED)) {
      return SolutionState.UNBOUNDED;
    }
    throw new Error("Unknown Mapping!");
  }
}
