package org.optsol.jmip.core;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractModel<SOLVER, VARCLASS, CONSTANTS extends IConstants> {

  private final SOLVER solver;
  private final IVariableManager<? super CONSTANTS, SOLVER, VARCLASS> variables;
  private final IObjectiveManager<? super CONSTANTS, VARCLASS, SOLVER> objective;
  private final Set<IConstraintManager<? super CONSTANTS, VARCLASS, SOLVER>> constraints =
      new HashSet<>();

  private CONSTANTS constants;

  public AbstractModel(
      SOLVER solver,
      IVariableManager<? super CONSTANTS, SOLVER, VARCLASS> variables,
      IObjectiveManager<? super CONSTANTS, VARCLASS, SOLVER> objective,
      Collection<IConstraintManager<? super CONSTANTS, VARCLASS, SOLVER>> constraints)
      throws Exception {

    this.solver = solver;

    this.variables = variables;
    this.variables.setSolver(solver);

    this.objective = objective;

    this.constraints.addAll(constraints);
  }

  public void buildOrUpdate(CONSTANTS constants) throws Exception {
    this.constants = constants;

    variables.updateVariables(constants);

    objective.createAndAddOrUpdateObjective(solver, constants, variables);

    for (IConstraintManager<? super CONSTANTS, VARCLASS, SOLVER> constraint : constraints) {
      constraint.createAndAddOrUpdateConstraints(solver, constants, variables);
    }
  }

  public IVariableManager<? super CONSTANTS, SOLVER, VARCLASS> getVariables() {
    return variables;
  }

  public Set<IConstraintManager<? super CONSTANTS, VARCLASS, SOLVER>> getConstraints() {
    return constraints;
  }

  public SOLVER getSolver() {
    return solver;
  }

  public CONSTANTS getConstants() {
    return constants;
  }

  public abstract boolean solve() throws Exception;

  public abstract Double getObjectiveValue() throws Exception;

  public abstract Double getBestObjectiveBound() throws Exception;

  public abstract SolutionState getSolutionState() throws Exception;

  public abstract Duration getSolutionTime() throws Exception;
}
