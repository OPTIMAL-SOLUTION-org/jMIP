package org.optsol.jmip.linearsolver.solver;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;
import java.time.Duration;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.solver.Solver;
import org.optsol.jmip.core.solver.solution.ISolution;
import org.optsol.jmip.core.solver.solution.ISolutionExtractor;
import org.optsol.jmip.linearsolver.model.LinearModel;
import org.optsol.jmip.linearsolver.model.LinearModelFactory;

public class LinearSolver<
    CONSTANTS extends IConstants,
    SOLUTION extends ISolution>
    extends Solver<
    CONSTANTS, MPSolver, MPVariable, LinearModel<CONSTANTS>,
    SOLUTION,
    LinearModelFactory<CONSTANTS>> {

  protected MPSolver.OptimizationProblemType solverEngineType;
  protected Duration timeLimit;
  protected Boolean enableOutput;
  protected String solverSpecificParameters;
  protected MPSolverParameters solverParameters;

  public LinearSolver(
      LinearModelFactory<CONSTANTS> modelFactory,
      ISolutionExtractor<SOLUTION, LinearModel<CONSTANTS>> solutionExtractor,
      MPSolver.OptimizationProblemType solverEngineType,
      Duration timeLimit,
      boolean enableOutput,
      String solverSpecificParameters,
      MPSolverParameters solverParameters) {
    super(modelFactory, solutionExtractor);
    this.solverEngineType = solverEngineType;
    this.timeLimit = timeLimit;
    this.enableOutput = enableOutput;
    this.solverSpecificParameters = solverSpecificParameters;
    this.solverParameters = solverParameters;
  }

  public LinearSolver(
      LinearModelFactory<CONSTANTS> modelFactory,
      Class<SOLUTION> solutionInterface,
      MPSolver.OptimizationProblemType solverEngineType,
      Duration timeLimit,
      boolean enableOutput,
      String solverSpecificParameters,
      MPSolverParameters solverParameters) {
    super(modelFactory, solutionInterface);
    this.solverEngineType = solverEngineType;
    this.timeLimit = timeLimit;
    this.enableOutput = enableOutput;
    this.solverSpecificParameters = solverSpecificParameters;
    this.solverParameters = solverParameters;
  }

  public static <CONSTANTS extends IConstants, SOLUTION extends ISolution> LinearSolverBuilder<CONSTANTS, SOLUTION> builder(
      LinearModelFactory<CONSTANTS> modelFactory,
      ISolutionExtractor<SOLUTION, LinearModel<CONSTANTS>> solutionExtractor,
      MPSolver.OptimizationProblemType solverEngineType) {
    return
        new LinearSolverBuilder<>(
            modelFactory,
            solutionExtractor,
            solverEngineType);
  }

  public static <CONSTANTS extends IConstants, SOLUTION extends ISolution> LinearSolverBuilder<CONSTANTS, SOLUTION> builder(
      LinearModelFactory<CONSTANTS> modelFactory,
      Class<SOLUTION> solutionInterface,
      MPSolver.OptimizationProblemType solverEngineType) {
    return
        new LinearSolverBuilder<>(
            modelFactory,
            solutionInterface,
            solverEngineType);
  }

  @Override
  protected MPSolver generateSolverEngine() {
    MPSolver mpSolver = MPSolver.createSolver(solverEngineType.name());

    setTimeLimit(mpSolver);
    setEnableOutput(mpSolver);
    setSolverSpecificParameters(mpSolver);

    return mpSolver;
  }

  @Override
  protected void solve(LinearModel<CONSTANTS> model) {
    MPSolverParameters mpSolverParameters = solverParameters;
    if (mpSolverParameters == null) {
      //default solver parameters
      mpSolverParameters = new MPSolverParameters();
    }

    model.solve(mpSolverParameters);
  }

  private void setTimeLimit(MPSolver mpSolver) {
    if (timeLimit != null) {
      mpSolver.setTimeLimit(timeLimit.toMillis());
    }
  }

  private void setEnableOutput(MPSolver mpSolver) {
    if (enableOutput != null) {
      if (enableOutput) {
        mpSolver.enableOutput();
      } else {
        mpSolver.suppressOutput();
      }
    }
  }

  private void setSolverSpecificParameters(MPSolver mpSolver) {
    if (solverSpecificParameters != null) {
      mpSolver.setSolverSpecificParametersAsString(solverSpecificParameters);
    }
  }

  public static class LinearSolverBuilder<CONSTANTS extends IConstants,
      SOLUTION extends ISolution> {
    private final LinearModelFactory<CONSTANTS> modelFactory;
    private final ISolutionExtractor<SOLUTION, LinearModel<CONSTANTS>> solutionExtractor;
    private final Class<SOLUTION> solutionInterface;
    private final MPSolver.OptimizationProblemType solverEngineType;
    private Duration timeLimit;
    private boolean enableOutput;
    private String solverSpecificParameters;
    private MPSolverParameters solverParameters;

    LinearSolverBuilder(
        LinearModelFactory<CONSTANTS> modelFactory,
        ISolutionExtractor<SOLUTION, LinearModel<CONSTANTS>> solutionExtractor,
        MPSolver.OptimizationProblemType solverEngineType) {
      this.modelFactory = modelFactory;
      this.solutionExtractor = solutionExtractor;
      this.solutionInterface = null;
      this.solverEngineType = solverEngineType;
    }

    LinearSolverBuilder(
        LinearModelFactory<CONSTANTS> modelFactory,
        Class<SOLUTION> solutionInterface,
        MPSolver.OptimizationProblemType solverEngineType) {
      this.modelFactory = modelFactory;
      this.solutionExtractor = null;
      this.solutionInterface = solutionInterface;
      this.solverEngineType = solverEngineType;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION> timeLimit(Duration timeLimit) {
      this.timeLimit = timeLimit;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION> enableOutput(boolean enableOutput) {
      this.enableOutput = enableOutput;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION> solverSpecificParameters(String solverSpecificParameters) {
      this.solverSpecificParameters = solverSpecificParameters;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION> solverParameters(MPSolverParameters solverParameters) {
      this.solverParameters = solverParameters;
      return this;
    }

    public LinearSolver<CONSTANTS, SOLUTION> build() {
      if (solutionExtractor != null) {
        return new LinearSolver<>(
            this.modelFactory,
            this.solutionExtractor,
            this.solverEngineType,
            this.timeLimit,
            this.enableOutput,
            this.solverSpecificParameters,
            this.solverParameters);
      } else {
        return new LinearSolver<>(
            this.modelFactory,
            this.solutionInterface,
            this.solverEngineType,
            this.timeLimit,
            this.enableOutput,
            this.solverSpecificParameters,
            this.solverParameters);
      }
    }
  }
}
