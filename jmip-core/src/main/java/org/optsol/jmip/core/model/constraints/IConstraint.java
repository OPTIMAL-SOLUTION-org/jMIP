package org.optsol.jmip.core.model.constraints;


import org.optsol.jmip.core.model.variables.IVariableProvider;

public interface IConstraint<CONSTANTS, VARCLASS, SOLVER> {

  void createAndAddOrUpdateConstraints(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables) throws Exception;

  String getConstraintGroupName();

  int getDimOfIndex();

  Double getDual0D(SOLVER solver);

  Double[] getDual1D(SOLVER solver);

  Double[][] getDual2D(SOLVER solver);

  Double[][][] getDual3D(SOLVER solver);

  Double[][][][] getDual4D(SOLVER solver);
}
