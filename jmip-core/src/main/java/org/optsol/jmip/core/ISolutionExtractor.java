package org.optsol.jmip.core;

public interface ISolutionExtractor<
    SOLUTION extends ISolution,
    MODEL> {
  SOLUTION extractSolution(MODEL model);
}
