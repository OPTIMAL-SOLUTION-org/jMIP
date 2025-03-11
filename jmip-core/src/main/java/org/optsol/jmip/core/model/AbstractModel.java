package org.optsol.jmip.core.model;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.model.constraints.IConstraint;
import org.optsol.jmip.core.model.objective.IObjective;
import org.optsol.jmip.core.model.variables.IVariable;
import org.optsol.jmip.core.solver.solution.SolutionState;

@Getter
public abstract class AbstractModel<SOLVER, VARCLASS, CONSTANTS extends IConstants> {

  private final SOLVER solver;
  private final IVariable<? super CONSTANTS, SOLVER, VARCLASS> variables;
  private final IObjective<? super CONSTANTS, VARCLASS, SOLVER> objective;
  private final Set<IConstraint<? super CONSTANTS, VARCLASS, SOLVER>> constraints = new HashSet<>();

  public AbstractModel(
      SOLVER solver,
      IVariable<? super CONSTANTS, SOLVER, VARCLASS> variables,
      IObjective<? super CONSTANTS, VARCLASS, SOLVER> objective,
      Collection<IConstraint<? super CONSTANTS, VARCLASS, SOLVER>> constraints)
      throws Exception {

    this.solver = solver;

    this.variables = variables;
    this.variables.setSolver(solver);

    this.objective = objective;

    this.constraints.addAll(constraints);
  }

  public void buildOrUpdate(CONSTANTS constants) throws Exception {
    variables.updateVariables(constants);

    objective.createAndAddOrUpdateObjective(solver, constants, variables);

    for (IConstraint<? super CONSTANTS, VARCLASS, SOLVER> constraint : constraints) {
      constraint.createAndAddOrUpdateConstraints(solver, constants, variables);
    }
  }

  public abstract Double getObjectiveValue() throws Exception;

  public abstract Double getBestObjectiveBound() throws Exception;

  public abstract SolutionState getSolutionState() throws Exception;

  public abstract Duration getSolutionTime() throws Exception;
}
