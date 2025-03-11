package org.optsol.jmip.core.solver.solution;

import java.time.Duration;

public class BasicSolution implements ISolution {
  private final SolutionState solutionState;
  private final Double objectiveValue;
  private final Double bestObjectiveBound;
  private final Duration solutionTime;

  public BasicSolution(
      SolutionState solutionState,
      Double objectiveValue,
      Double bestObjectiveBound,
      Duration solutionTime) {
    this.solutionState = solutionState;
    this.objectiveValue = objectiveValue;
    this.bestObjectiveBound = bestObjectiveBound;
    this.solutionTime = solutionTime;
  }


  @Override
  public SolutionState getSolutionState() {
    return solutionState;
  }

  @Override
  public Double getObjectiveValue() {
    return objectiveValue;
  }

  @Override
  public Double getBestObjectiveBound() {
    return bestObjectiveBound;
  }

  @Override
  public Duration getSolutionTime() {
    return solutionTime;
  }
}
