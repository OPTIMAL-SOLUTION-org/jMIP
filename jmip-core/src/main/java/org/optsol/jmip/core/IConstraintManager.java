package org.optsol.jmip.core;


public interface IConstraintManager<CONSTANTS, VARCLASS, SOLVER> {

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
