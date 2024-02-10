package org.optsol.jmip.ortools;

import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.optsol.jmip.core.IConstants;
import org.optsol.jmip.core.ISolution;
import org.optsol.jmip.core.ISolutionExtractor;
import org.optsol.jmip.core.Solver;

public class OrtoolsSolver<CONSTANTS extends IConstants, SOLUTION extends ISolution>
    extends Solver<
    CONSTANTS,
    MPSolver,
    MPVariable,
    OrtoolsModel<CONSTANTS>,
    SOLUTION,
    AbstractOrtoolsModelFactory<CONSTANTS>> {

  public OrtoolsSolver(
      AbstractOrtoolsModelFactory<CONSTANTS> modelFactory,
      ISolutionExtractor<SOLUTION, OrtoolsModel<CONSTANTS>> solutionExtractor) {
    super(modelFactory, solutionExtractor);
  }

  public OrtoolsSolver(
      int timelimitSeconds,
      AbstractOrtoolsModelFactory<CONSTANTS> modelFactory,
      ISolutionExtractor<SOLUTION, OrtoolsModel<CONSTANTS>> solutionExtractor) {
    super(timelimitSeconds, modelFactory, solutionExtractor);
  }

  public OrtoolsSolver(
      AbstractOrtoolsModelFactory<CONSTANTS> modelFactory,
      Class<SOLUTION> solutionInterface) {
    super(modelFactory, solutionInterface);
  }

  public OrtoolsSolver(
      int timelimitSeconds,
      AbstractOrtoolsModelFactory<CONSTANTS> modelFactory,
      Class<SOLUTION> solutionInterface) {
    super(timelimitSeconds, modelFactory, solutionInterface);
  }

  @Override
  protected void setTimeLimitSeconds(
      OrtoolsModel<CONSTANTS> model,
      int timelimitSeconds) {
    model.getSolver().setTimeLimit(timelimitSeconds * 1000L);
  }
}
