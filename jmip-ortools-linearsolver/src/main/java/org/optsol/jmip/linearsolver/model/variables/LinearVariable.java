package org.optsol.jmip.linearsolver.model.variables;

import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.List;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.model.variables.AbstractVariable;

public class LinearVariable<CONSTANTS extends IConstants>
    extends AbstractVariable<MPSolver, MPVariable, CONSTANTS> {

  private LinearVariable(AbstractBuilder<MPSolver, MPVariable, CONSTANTS> builder) {
    super(builder);
  }

  private List<Double> variableValueList = null;
  private List<Double> variableReducedCostList = null;

  @Override
  protected MPVariable createIntVar(
      VarKey varKey,
      MPSolver solver,
      Double upperBound,
      Double lowerBound) {
    if (upperBound == null) {
      upperBound = Double.POSITIVE_INFINITY;
    }
    if (lowerBound == null) {
      lowerBound = Double.NEGATIVE_INFINITY;
    }

    return solver.makeIntVar(lowerBound, upperBound, varKey.toString());
  }

  @Override
  protected MPVariable createNumVar(
      VarKey varKey,
      MPSolver solver,
      Double upperBound,
      Double lowerBound) {
    if (upperBound == null) {
      upperBound = Double.POSITIVE_INFINITY;
    }
    if (lowerBound == null) {
      lowerBound = Double.NEGATIVE_INFINITY;
    }

    return solver.makeNumVar(lowerBound, upperBound, varKey.toString());
  }

  @Override
  protected void updateBoundsOfVar(
      MPVariable var,
      MPSolver mpSolver,
      Double upperBound,
      Double lowerBound) throws Exception {
    if (upperBound == null) {
      upperBound = Double.POSITIVE_INFINITY;
    }
    if (lowerBound == null) {
      lowerBound = Double.NEGATIVE_INFINITY;
    }

    var.setBounds(lowerBound, upperBound);
  }

  @Override
  protected double getValueOfVar(
      MPSolver solver,
      MPVariable var) {

    if (variableValueList == null) {
      variableValueList =
          solver.createSolutionResponseProto().getVariableValueList();
    }

    return variableValueList.get(var.index());
    //return var.solutionValue();
  }

  @Override
  protected double getReducedCostOfVar(
      MPSolver solver,
      MPVariable var) {

    if (variableReducedCostList == null) {
      variableReducedCostList =
          solver.createSolutionResponseProto().getReducedCostList();
    }

    return variableReducedCostList.get(var.index());
    //return var.solutionValue();
  }

  public static class Builder<CONSTANTS extends IConstants>
      extends AbstractBuilder<MPSolver, MPVariable, CONSTANTS> {

    @Override
    public AbstractVariable<MPSolver, MPVariable, CONSTANTS> build() {
      return new LinearVariable<>(this);
    }
  }
}
