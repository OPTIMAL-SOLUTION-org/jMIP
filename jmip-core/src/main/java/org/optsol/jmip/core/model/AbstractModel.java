package org.optsol.jmip.core.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.model.constraints.IConstraint;
import org.optsol.jmip.core.model.objective.IObjective;
import org.optsol.jmip.core.model.variables.IVariable;

@Getter
public abstract class AbstractModel<SOLVER, VARCLASS, CONSTANTS extends IConstants>
    implements IModel<SOLVER, VARCLASS, CONSTANTS> {

  private SOLVER solver;
  private IVariable<? super CONSTANTS, SOLVER, VARCLASS> variables;
  private IObjective<? super CONSTANTS, VARCLASS, SOLVER> objective;
  private Set<IConstraint<? super CONSTANTS, VARCLASS, SOLVER>> constraints = new HashSet<>();


  @Override
  public void initModel(SOLVER solver) throws Exception {
    this.solver = solver;

    this.variables = generateVariables();
    this.variables.setSolver(solver);

    this.objective = generateObjective();

    this.constraints.addAll(generateConstraints());

    //model.buildOrUpdate(constants);
  }

  @Override
  public void buildOrUpdate(CONSTANTS constants) throws Exception {
    variables.updateVariables(constants);

    objective.createAndAddOrUpdateObjective(solver, constants, variables);

    for (IConstraint<? super CONSTANTS, VARCLASS, SOLVER> constraint : constraints) {
      constraint.createAndAddOrUpdateConstraints(solver, constants, variables);
    }
  }

  protected abstract IVariable<? super CONSTANTS, SOLVER, VARCLASS> generateVariables();

  protected abstract IObjective<? super CONSTANTS, VARCLASS, SOLVER> generateObjective();

  protected abstract List<IConstraint<? super CONSTANTS, VARCLASS, SOLVER>> generateConstraints();
}