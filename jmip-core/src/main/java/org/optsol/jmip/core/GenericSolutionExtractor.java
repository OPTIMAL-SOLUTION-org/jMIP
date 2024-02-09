package org.optsol.jmip.core;

import java.lang.reflect.Proxy;

class GenericSolutionExtractor<
    SOLVER, VARCLASS, CONSTANTS extends IConstants,
    SOLUTION extends ISolution,
    MODEL extends AbstractModel<SOLVER, VARCLASS, CONSTANTS>>
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
