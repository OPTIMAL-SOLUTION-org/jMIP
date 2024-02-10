package org.optsol.jmip.ortools;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.Collection;
import org.optsol.jmip.core.AbstractConstraintManager;
import org.optsol.jmip.core.IConstants;
import org.optsol.jmip.core.IVariableProvider;

public abstract class AbstractOrtoolsConstraintManager<CONSTANTS extends IConstants>
    extends AbstractConstraintManager<MPSolver, CONSTANTS, MPVariable, MPConstraint> {

  protected AbstractOrtoolsConstraintManager(String... constraintIndexNames) {
    super(constraintIndexNames);
  }

  @Override
  protected final MPConstraint generateConstraint(
      MPSolver solver,
      CONSTANTS constants,
      IVariableProvider<MPVariable> variables,
      ConstraintKey constraintKey) throws Exception {

    MPConstraint constraint =
        solver.makeConstraint(
            generateConstraintName(constraintKey));

    configureConstraint(
        constraint,
        constants,
        variables,
        constraintKey);

    return constraint;
  }

  protected abstract void configureConstraint(
      MPConstraint constraint,
      CONSTANTS constants,
      IVariableProvider<MPVariable> variables,
      ConstraintKey constraintKey) throws Exception;

  @Override
  protected final void removeConstraints(
      MPSolver solver,
      Collection<MPConstraint> constraints) {
    constraints.forEach(MPConstraint::delete);
  }

  @Override
  protected double getDualOfConstraint(
      MPSolver solver,
      MPConstraint constraint) {
    return constraint.dualValue();
  }
}
