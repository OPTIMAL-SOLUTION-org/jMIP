package org.optsol.jmip.core;

public interface ISolver<CONSTANTS, SOLUTION extends ISolution> {
  SOLUTION generateSolution(CONSTANTS constants) throws Exception;

  void setTimelimitSeconds(int timelimitSeconds);
}
