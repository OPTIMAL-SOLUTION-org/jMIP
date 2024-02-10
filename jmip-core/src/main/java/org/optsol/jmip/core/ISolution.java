package org.optsol.jmip.core;

import java.time.Duration;

public interface ISolution {
  SolutionState getSolutionState();

  Double getObjectiveValue();

  Double getBestObjectiveBound();

  Duration getSolutionTime();
}
