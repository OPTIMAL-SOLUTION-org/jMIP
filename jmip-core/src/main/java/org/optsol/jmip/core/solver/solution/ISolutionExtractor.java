package org.optsol.jmip.core.solver.solution;

public interface ISolutionExtractor<
    SOLUTION extends ISolution,
    MODEL> {
  SOLUTION extractSolution(MODEL model);
}
