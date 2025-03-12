package org.optsol.jmip.core.solver;

import org.optsol.jmip.core.solver.solution.ISolution;

public interface ISolver<CONSTANTS, SOLUTION extends ISolution> {
  SOLUTION generateSolution(CONSTANTS constants) throws Exception;
}
