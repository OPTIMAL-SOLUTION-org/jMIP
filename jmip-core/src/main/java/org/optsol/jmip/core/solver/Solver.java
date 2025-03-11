package org.optsol.jmip.core.solver;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.optsol.jmip.core.model.AbstractModel;
import org.optsol.jmip.core.model.AbstractModelFactory;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.solver.solution.GenericSolutionExtractor;
import org.optsol.jmip.core.solver.solution.ISolution;
import org.optsol.jmip.core.solver.solution.ISolutionExtractor;

@AllArgsConstructor
@Setter
public abstract class Solver<
    CONSTANTS extends IConstants,
    SOLVER,
    VARCLASS,
    MODEL extends AbstractModel<SOLVER, VARCLASS, CONSTANTS>,
    SOLUTION extends ISolution,
    MODELFACTORY extends AbstractModelFactory<CONSTANTS, VARCLASS, SOLVER, MODEL>>
    implements ISolver<CONSTANTS, SOLUTION> {

  private final MODELFACTORY modelFactory;
  private final ISolutionExtractor<SOLUTION, MODEL> solutionExtractor;

  public Solver(
      MODELFACTORY modelFactory,
      Class<SOLUTION> solutionInterface) {
    this.modelFactory = modelFactory;
    this.solutionExtractor =
        new GenericSolutionExtractor<>(solutionInterface);
  }

  @Override
  public final SOLUTION generateSolution(CONSTANTS constants) throws Exception {

    MODEL model = modelFactory.buildModel(constants, generateSolverEngine());

    model = modelManipulation(model);

    solve(model);

    return solutionExtractor.extractSolution(model);
  }

  protected abstract SOLVER generateSolverEngine();

  protected MODEL modelManipulation(MODEL model) {
    return model;
  }

  protected abstract void solve(MODEL model);
}
