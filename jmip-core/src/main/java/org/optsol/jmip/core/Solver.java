package org.optsol.jmip.core;

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

  private int timelimitSeconds = 0;

  public Solver(
      MODELFACTORY modelFactory,
      ISolutionExtractor<SOLUTION, MODEL> solutionExtractor) {
    this.modelFactory = modelFactory;
    this.solutionExtractor = solutionExtractor;
  }

  public Solver(
      int timeLimitSeconds,
      MODELFACTORY modelFactory,
      ISolutionExtractor<SOLUTION, MODEL> solutionExtractor) {
    this(modelFactory, solutionExtractor);
    setTimelimitSeconds(timeLimitSeconds);
  }

  public Solver(
      MODELFACTORY modelFactory,
      Class<SOLUTION> solutionInterface) {
    this.modelFactory = modelFactory;
    this.solutionExtractor =
        new GenericSolutionExtractor<>(solutionInterface);
  }

  public Solver(
      int timeLimitSeconds,
      MODELFACTORY modelFactory,
      Class<SOLUTION> solutionInterface) {
    this(modelFactory, solutionInterface);
    setTimelimitSeconds(timeLimitSeconds);
  }

  @Override
  public final SOLUTION generateSolution(CONSTANTS constants) throws Exception {

    MODEL model = null;

    model = modelFactory.buildModel(constants);

    if (timelimitSeconds > 0) {
      setTimeLimitSeconds(model, timelimitSeconds);
    }

    model = modelManipulation(model);

    model.solve();

    return solutionExtractor.extractSolution(model);
  }

  @Override
  public void setTimelimitSeconds(int timelimitSeconds) {
    this.timelimitSeconds = timelimitSeconds;
  }

  protected MODEL modelManipulation(MODEL model) {
    return model;
  }

  abstract protected void setTimeLimitSeconds(
      MODEL model,
      int timelimitSeconds) throws Exception;
}
