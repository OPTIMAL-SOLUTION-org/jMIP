package org.optsol.jmip.core;


import java.util.Map;

public interface IVariableManager<CONSTANTS, SOLVER, VARCLASS> extends IVariableProvider<VARCLASS> {
  VARCLASS getVar(
      String varGroupName,
      int... varIndexTuple) throws Exception;

  VARCLASS getVar(
      boolean createIfNotExists,
      String varGroupName,
      int... varIndexTuple) throws Exception;

  void updateVariables(CONSTANTS constants) throws Exception;

  void setSolver(SOLVER solver);

  boolean existsVarGroupName(String varGroupName);

  int getDimOfVarGroup(String varGroupName);

  Map<String, Boolean> getValuesOfVarGroupAsBooleanMap(String varGroupName);

  Map<String, Double> getValuesOfVarGroupAsDoubleMap(String varGroupName);

  Map<String, Integer> getValuesOfVarGroupAsIntegerMap(String varGroupName);

  boolean getValueOfVarAsBool(
      String varGroupName,
      int... varIndexTuple) throws Exception;

  Boolean[] getValuesOfVarGroup1DAsBool(String varGroupName);

  Boolean[][] getValuesOfVarGroup2DAsBool(String varGroupName);

  Boolean[][][] getValuesOfVarGroup3DAsBool(String varGroupName);

  Boolean[][][][] getValuesOfVarGroup4DAsBool(String varGroupName);

  int getValueOfVarAsInt(
      String varGroupName,
      int... varIndexTuple) throws Exception;

  Integer[] getValuesOfVarGroup1DAsInt(String varGroupName);

  Integer[][] getValuesOfVarGroup2DAsInt(String varGroupName);

  Integer[][][] getValuesOfVarGroup3DAsInt(String varGroupName);

  Integer[][][][] getValuesOfVarGroup4DAsInt(String varGroupName);

  double getValueOfVarAsReal(
      String varGroupName,
      int... varIndexTuple) throws Exception;

  Double[] getValuesOfVarGroup1DAsReal(String varGroupName);

  Double[][] getValuesOfVarGroup2DAsReal(String varGroupName);

  Double[][][] getValuesOfVarGroup3DAsReal(String varGroupName);

  Double[][][][] getValuesOfVarGroup4DAsReal(String varGroupName);

  //REDUCED COST
  Map<String, Double> getReducedCostOfVarGroupAsMap(String varGroupName);

  double getReducedCostOfVar(
      String varGroupName,
      int... varIndexTuple) throws Exception;

  Double[] getReducedCostOfVarGroup1D(String varGroupName);

  Double[][] getReducedCostOfVarGroup2D(String varGroupName);

  Double[][][] getReducedCostOfVarGroup3D(String varGroupName);

  Double[][][][] getReducedCostOfVarGroup4D(String varGroupName);
}
