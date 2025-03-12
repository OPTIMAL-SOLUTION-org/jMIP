package org.optsol.jmip.linearsolver.solver;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;
import java.time.Duration;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.solver.Solver;
import org.optsol.jmip.core.solver.solution.ISolution;
import org.optsol.jmip.core.solver.solution.ISolutionExtractor;
import org.optsol.jmip.linearsolver.model.LinearModel;

@Slf4j
public final class LinearSolver<
    CONSTANTS extends IConstants,
    SOLUTION extends ISolution,
    MODEL extends LinearModel<CONSTANTS>>
    extends Solver<
    CONSTANTS,
    MPSolver,
    MPVariable,
    MODEL,
    SOLUTION> {
  static {
    Loader.loadNativeLibraries();
  }

  private final MPSolver.OptimizationProblemType solverEngineType;
  private final Duration timeLimit;
  private final boolean enableOutput;
  private final boolean suppressOutput;
  private final String solverSpecificParameters;
  private final MPSolverParameters solverParameters;
  private final Double dualTolerance;
  private final Double primalTolerance;
  private final Double relativeMipGap;
  private final MPSolverParameters.IncrementalityValues incrementality;
  private final MPSolverParameters.LpAlgorithmValues lpAlgorithm;
  private final MPSolverParameters.PresolveValues presolve;
  private final MPSolverParameters.ScalingValues scaling;

  public LinearSolver(
      Class<MODEL> modelClass,
      ISolutionExtractor<SOLUTION, MODEL> solutionExtractor,
      MPSolver.OptimizationProblemType solverEngineType,
      Duration timeLimit,
      boolean enableOutput,
      boolean suppressOutput,
      String solverSpecificParameters,
      MPSolverParameters solverParameters,
      Double dualTolerance,
      Double primalTolerance,
      Double relativeMipGap,
      MPSolverParameters.IncrementalityValues incrementality,
      MPSolverParameters.LpAlgorithmValues lpAlgorithm,
      MPSolverParameters.PresolveValues presolve,
      MPSolverParameters.ScalingValues scaling) {
    super(modelClass, solutionExtractor);
    this.solverEngineType = Objects.requireNonNullElse(
        solverEngineType,
        MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING);
    this.timeLimit = timeLimit;
    this.enableOutput = enableOutput;
    this.suppressOutput = suppressOutput;
    this.solverSpecificParameters = solverSpecificParameters;
    this.solverParameters = solverParameters;
    this.dualTolerance = dualTolerance;
    this.primalTolerance = primalTolerance;
    this.relativeMipGap = relativeMipGap;
    this.incrementality = incrementality;
    this.lpAlgorithm = lpAlgorithm;
    this.presolve = presolve;
    this.scaling = scaling;
  }

  public LinearSolver(
      Class<MODEL> modelClass,
      Class<SOLUTION> solutionInterface,
      MPSolver.OptimizationProblemType solverEngineType,
      Duration timeLimit,
      boolean enableOutput,
      boolean suppressOutput,
      String solverSpecificParameters,
      MPSolverParameters solverParameters,
      Double dualTolerance,
      Double primalTolerance,
      Double relativeMipGap,
      MPSolverParameters.IncrementalityValues incrementality,
      MPSolverParameters.LpAlgorithmValues lpAlgorithm,
      MPSolverParameters.PresolveValues presolve,
      MPSolverParameters.ScalingValues scaling) {
    super(modelClass, solutionInterface);
    this.solverEngineType = Objects.requireNonNullElse(
        solverEngineType,
        MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING);
    this.timeLimit = timeLimit;
    this.enableOutput = enableOutput;
    this.suppressOutput = suppressOutput;
    this.solverSpecificParameters = solverSpecificParameters;
    this.solverParameters = solverParameters;
    this.dualTolerance = dualTolerance;
    this.primalTolerance = primalTolerance;
    this.relativeMipGap = relativeMipGap;
    this.incrementality = incrementality;
    this.lpAlgorithm = lpAlgorithm;
    this.presolve = presolve;
    this.scaling = scaling;
  }

  public static <CONSTANTS extends IConstants, SOLUTION extends ISolution,
      MODEL extends LinearModel<CONSTANTS>> LinearSolverBuilder<CONSTANTS,
      SOLUTION, MODEL> builder(
      Class<MODEL> modelClass,
      ISolutionExtractor<SOLUTION, MODEL> solutionExtractor) {
    return
        new LinearSolverBuilder<>(
            modelClass,
            solutionExtractor);
  }

  public static <CONSTANTS extends IConstants, SOLUTION extends ISolution,
      MODEL extends LinearModel<CONSTANTS>> LinearSolverBuilder<CONSTANTS,
      SOLUTION, MODEL> builder(
      Class<MODEL> modelClass,
      Class<SOLUTION> solutionInterface) {
    return
        new LinearSolverBuilder<>(
            modelClass,
            solutionInterface);
  }

  @Override
  protected MPSolver generateSolverEngine() {
    return
        setTimeLimit(
            setEnableOutput(
                setSupressOutput(
                    setSolverSpecificParameters(
                        MPSolver.createSolver(solverEngineType.name())))));
  }

  @Override
  protected void solve(MODEL model) {
    model.solve(generateSolverParameters());
  }

  private MPSolverParameters generateSolverParameters() {
    MPSolverParameters mpSolverParameters = solverParameters;
    if (mpSolverParameters == null) {
      //default solver parameters
      mpSolverParameters = new MPSolverParameters();
    }

    return
        setDualTolerance(
            setPrimalTolerance(
                setRelativeMipGap(
                    setIncrementality(
                        setLpAlgorithm(
                            setPresolve(
                                setScaling(mpSolverParameters)))))));
  }

  private MPSolverParameters setDualTolerance(MPSolverParameters mpSolverParameters) {
    if (dualTolerance != null) {
      mpSolverParameters.setDoubleParam(
          MPSolverParameters.DoubleParam.DUAL_TOLERANCE,
          dualTolerance);
    }
    return mpSolverParameters;
  }

  private MPSolverParameters setPrimalTolerance(MPSolverParameters mpSolverParameters) {
    if (primalTolerance != null) {
      mpSolverParameters.setDoubleParam(
          MPSolverParameters.DoubleParam.PRIMAL_TOLERANCE,
          primalTolerance);
    }
    return mpSolverParameters;
  }

  private MPSolverParameters setRelativeMipGap(MPSolverParameters mpSolverParameters) {
    if (relativeMipGap != null) {
      mpSolverParameters.setDoubleParam(
          MPSolverParameters.DoubleParam.RELATIVE_MIP_GAP,
          relativeMipGap);
    }
    return mpSolverParameters;
  }

  private MPSolverParameters setIncrementality(MPSolverParameters mpSolverParameters) {
    if (incrementality != null) {
      mpSolverParameters.setIntegerParam(
          MPSolverParameters.IntegerParam.INCREMENTALITY,
          incrementality.swigValue());
    }
    return mpSolverParameters;
  }

  private MPSolverParameters setLpAlgorithm(MPSolverParameters mpSolverParameters) {
    if (lpAlgorithm != null) {
      mpSolverParameters.setIntegerParam(
          MPSolverParameters.IntegerParam.LP_ALGORITHM,
          lpAlgorithm.swigValue());
    }
    return mpSolverParameters;
  }

  private MPSolverParameters setPresolve(MPSolverParameters mpSolverParameters) {
    if (presolve != null) {
      mpSolverParameters.setIntegerParam(
          MPSolverParameters.IntegerParam.PRESOLVE,
          presolve.swigValue());
    }
    return mpSolverParameters;
  }

  private MPSolverParameters setScaling(MPSolverParameters mpSolverParameters) {
    if (scaling != null) {
      mpSolverParameters.setIntegerParam(
          MPSolverParameters.IntegerParam.SCALING,
          scaling.swigValue());
    }
    return mpSolverParameters;
  }

  //TODO
  private MPSolver setSolver(MPSolver mpSolver) {
    if (timeLimit != null) {
      mpSolver.setTimeLimit(timeLimit.toMillis());
    }
    return mpSolver;
  }

  private MPSolver setTimeLimit(MPSolver mpSolver) {
    if (timeLimit != null) {
      mpSolver.setTimeLimit(timeLimit.toMillis());
    }
    return mpSolver;
  }

  private MPSolver setEnableOutput(MPSolver mpSolver) {
    if (enableOutput) {
      mpSolver.enableOutput();
    }
    return mpSolver;
  }

  private MPSolver setSupressOutput(MPSolver mpSolver) {
    if (suppressOutput) {
      mpSolver.suppressOutput();
    }
    return mpSolver;
  }

  private MPSolver setSolverSpecificParameters(MPSolver mpSolver) {
    if (solverSpecificParameters != null) {
      mpSolver.setSolverSpecificParametersAsString(solverSpecificParameters);
    }
    return mpSolver;
  }

  public static class LinearSolverBuilder<
      CONSTANTS extends IConstants,
      SOLUTION extends ISolution,
      MODEL extends LinearModel<CONSTANTS>> {

    private final Class<MODEL> modelClass;
    private final ISolutionExtractor<SOLUTION, MODEL> solutionExtractor;
    private final Class<SOLUTION> solutionInterface;
    private MPSolver.OptimizationProblemType solverEngineType;
    private Duration timeLimit;
    private Boolean enableOutput;
    private Boolean suppressOutput;
    private String solverSpecificParameters;
    private MPSolverParameters solverParameters;
    private Double dualTolerance;
    private Double primalTolerance;
    private Double relativeMipGap;
    private MPSolverParameters.IncrementalityValues incrementality;
    private MPSolverParameters.LpAlgorithmValues lpAlgorithm;
    private MPSolverParameters.PresolveValues presolve;
    private MPSolverParameters.ScalingValues scaling;

    LinearSolverBuilder(
        Class<MODEL> modelClass,
        ISolutionExtractor<SOLUTION, MODEL> solutionExtractor) {
      this.modelClass = modelClass;
      this.solutionExtractor = solutionExtractor;
      this.solutionInterface = null;
    }

    LinearSolverBuilder(
        Class<MODEL> modelClass,
        Class<SOLUTION> solutionInterface) {
      this.modelClass = modelClass;
      this.solutionExtractor = null;
      this.solutionInterface = solutionInterface;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> solverEngineType(MPSolver.OptimizationProblemType solverEngineType) {
      this.solverEngineType = solverEngineType;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> timeLimit(Duration timeLimit) {
      this.timeLimit = timeLimit;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> enableOutput() {
      this.enableOutput = true;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> suppressOutput() {
      this.suppressOutput = true;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> solverSpecificParameters(String solverSpecificParameters) {
      this.solverSpecificParameters = solverSpecificParameters;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> solverParameters(MPSolverParameters solverParameters) {
      this.solverParameters = solverParameters;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> dualTolerance(double dualTolerance) {
      this.dualTolerance = dualTolerance;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> primalTolerance(double primalTolerance) {
      this.primalTolerance = primalTolerance;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> relativeMipGap(double relativeMipGap) {
      this.relativeMipGap = relativeMipGap;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> incrementalityOn() {
      this.incrementality = MPSolverParameters.IncrementalityValues.INCREMENTALITY_ON;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> incrementalityOff() {
      this.incrementality = MPSolverParameters.IncrementalityValues.INCREMENTALITY_OFF;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> lpAlgorithm_Barrier() {
      this.lpAlgorithm = MPSolverParameters.LpAlgorithmValues.BARRIER;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> lpAlgorithm_Dual() {
      this.lpAlgorithm = MPSolverParameters.LpAlgorithmValues.DUAL;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> lpAlgorithm_Primal() {
      this.lpAlgorithm = MPSolverParameters.LpAlgorithmValues.PRIMAL;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> presolveOn() {
      this.presolve = MPSolverParameters.PresolveValues.PRESOLVE_ON;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> presolveOff() {
      this.presolve = MPSolverParameters.PresolveValues.PRESOLVE_OFF;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> scalingOn() {
      this.scaling = MPSolverParameters.ScalingValues.SCALING_ON;
      return this;
    }

    public LinearSolverBuilder<CONSTANTS, SOLUTION, MODEL> scalingOff() {
      this.scaling = MPSolverParameters.ScalingValues.SCALING_OFF;
      return this;
    }

    public LinearSolver<CONSTANTS, SOLUTION, MODEL> build() {
      if (solutionExtractor != null) {
        return new LinearSolver<>(
            this.modelClass,
            this.solutionExtractor,
            this.solverEngineType,
            this.timeLimit,
            Boolean.TRUE.equals(this.enableOutput),
            Boolean.TRUE.equals(this.suppressOutput),
            this.solverSpecificParameters,
            this.solverParameters,
            this.dualTolerance,
            this.primalTolerance,
            this.relativeMipGap,
            this.incrementality,
            this.lpAlgorithm,
            this.presolve,
            this.scaling);

      } else {
        return new LinearSolver<>(
            this.modelClass,
            this.solutionInterface,
            this.solverEngineType,
            this.timeLimit,
            Boolean.TRUE.equals(this.enableOutput),
            Boolean.TRUE.equals(this.suppressOutput),
            this.solverSpecificParameters,
            this.solverParameters,
            this.dualTolerance,
            this.primalTolerance,
            this.relativeMipGap,
            this.incrementality,
            this.lpAlgorithm,
            this.presolve,
            this.scaling);
      }
    }
  }
}
