package org.optsol.jmip.core.model.objective;

import org.optsol.jmip.core.model.variables.IVariableProvider;

public abstract class AbstractObjective<SOLVER, CONSTANTS, VARCLASS, OBJECTIVECLASS>
    implements IObjective<CONSTANTS, VARCLASS, SOLVER> {

  private final String objectiveName;
  private OBJECTIVECLASS objective = null;

  public AbstractObjective() {
    this.objectiveName = this.getClass().getSimpleName();
  }

  @Override
  public final void createAndAddOrUpdateObjective(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables) throws Exception {
    //REMOVE
    if (objective != null) {
      removeObjective(solver, objective);
      objective = null;
    }

    //GENERATE AND ADD NEW
    objective =
        generateObjective(
            solver,
            constants,
            variables);
  }

  protected final String getObjectiveName() {
    return objectiveName;
  }

  protected abstract OBJECTIVECLASS generateObjective(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables)
      throws Exception;

  protected abstract void removeObjective(
      SOLVER solver,
      OBJECTIVECLASS objective) throws Exception;
}
