package org.optsol.jmip.core.solver;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.optsol.jmip.core.model.IModel;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.solver.solution.GenericSolutionExtractor;
import org.optsol.jmip.core.solver.solution.ISolution;
import org.optsol.jmip.core.solver.solution.ISolutionExtractor;

@AllArgsConstructor
@Setter
@Slf4j
public abstract class Solver<
    CONSTANTS extends IConstants,
    SOLVER,
    VARCLASS,
    MODEL extends IModel<SOLVER, VARCLASS, CONSTANTS>,
    SOLUTION extends ISolution>
    implements ISolver<CONSTANTS, SOLUTION> {

  private final Class<MODEL> modelClass;
  private final ISolutionExtractor<SOLUTION, MODEL> solutionExtractor;

  public Solver(
      Class<MODEL> modelClass,
      Class<SOLUTION> solutionInterface) {
    this.modelClass = modelClass;
    this.solutionExtractor =
        new GenericSolutionExtractor<>(solutionInterface);
  }

  @Override
  public final SOLUTION generateSolution(CONSTANTS constants) throws Exception {

    log.info("Instantiating model: {} ", modelClass.getName());
    MODEL model = modelClass.getConstructor().newInstance();

    model.initModel(generateSolverEngine());
    log.info("Building model: {} ", modelClass.getName());
    model.buildOrUpdate(constants);

    model = modelManipulation(model);

    log.info("Optimizing model: {} ", modelClass.getName());
    solve(model);

    log.info("Extracting solution: {} ", modelClass.getName());
    return solutionExtractor.extractSolution(model);
  }

  protected abstract SOLVER generateSolverEngine();

  protected MODEL modelManipulation(MODEL model) {
    return model;
  }

  protected abstract void solve(MODEL model);
}
