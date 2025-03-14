package org.optsol.jmip.core.solver.solution;

import java.lang.reflect.Proxy;
import org.optsol.jmip.core.model.IModel;
import org.optsol.jmip.core.model.constants.IConstants;

public class GenericSolutionExtractor<
    SOLVER, VARCLASS, CONSTANTS extends IConstants,
    SOLUTION extends ISolution,
    MODEL extends IModel<SOLVER, VARCLASS, CONSTANTS>>
    implements ISolutionExtractor<SOLUTION, MODEL> {

  private final Class<SOLUTION> solutionClazz;

  public GenericSolutionExtractor(
      Class<SOLUTION> solutionClazz) {
    this.solutionClazz = solutionClazz;
  }

  @Override
  public SOLUTION extractSolution(MODEL model) {
    return (SOLUTION)
        Proxy.newProxyInstance(
            ISolution.class.getClassLoader(),
            new Class[] {solutionClazz},
            new ProxySolution<>(model)
        );
  }
}
