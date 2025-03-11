package org.optsol.jmip.core.solver.solution;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import org.optsol.jmip.core.model.AbstractModel;
import org.optsol.jmip.core.model.constants.IConstants;
import org.optsol.jmip.core.model.constraints.IConstraint;

public class ProxySolution<SOLVER, VARCLASS, CONSTANTS extends IConstants>
    implements InvocationHandler {
  private final AbstractModel<SOLVER, VARCLASS, CONSTANTS> model;

  public ProxySolution(AbstractModel<SOLVER, VARCLASS, CONSTANTS> model) {
    this.model = model;
  }

  private String extractVarGroupName(String methodName)
      throws Exception {
    String varGroupName = methodName.replaceFirst("get_", "");
    while (!model.getVariables().existsVarGroupName(varGroupName)) {
      int i = varGroupName.lastIndexOf("_");
      if (i == -1) {
        throw new Exception("VarGroupName for Method " + methodName + " not found!");
      }
      varGroupName = varGroupName.substring(0, i);
    }
    return varGroupName;
  }

  private String extractConstraintGroupName(String methodName)
      throws Exception {
    final String[] constraintGroupName =
        new String[] {methodName.replaceFirst("getDual_", "")};
    while (model.getConstraints().stream()
        .noneMatch(constraint ->
            constraint.getConstraintGroupName().equals(constraintGroupName[0]))) {
      int i = constraintGroupName[0].lastIndexOf("_");
      if (i == -1) {
        throw new Exception("VarGroupName for Method " + methodName + " not found!");
      }
      constraintGroupName[0] = constraintGroupName[0].substring(0, i);
    }
    return constraintGroupName[0];
  }

  @Override
  public Object invoke(
      Object proxy,
      Method method,
      Object[] args) throws Throwable {

    try {
      switch (method.getName()) {
        case "getObjectiveValue":
          return model.getObjectiveValue();
        case "getSolutionState":
          return model.getSolutionState();
        case "getBestObjectiveBound":
          return model.getBestObjectiveBound();
        case "getSolutionTime":
          return model.getSolutionTime();
      }
      if (method.getName().startsWith("getDual_")) {
        String constraintGroupName =
            extractConstraintGroupName(method.getName());
        IConstraint<? super CONSTANTS, VARCLASS, SOLVER> constraintManager =
            model.getConstraints().stream()
                .filter(cm -> cm.getConstraintGroupName().equals(constraintGroupName))
                .findFirst()
                .get();
        int dim = constraintManager.getDimOfIndex();
        Class<?> type = method.getReturnType();

        return extractDualValues(type, constraintManager, dim);
      }
      if (method.getName().startsWith("getReducedCost_")) {
        String varGroupName = extractVarGroupName(method.getName());
        int dim = model.getVariables().getDimOfVarGroup(varGroupName);
        Class<?> type = method.getReturnType();

        if (Map.class.equals(type)) {
          return extractReducedCost(method, varGroupName);

        } else {

          return extractReducedCost(type, varGroupName, dim);
        }
      }
      if (method.getName().startsWith("get_")) {
        String varGroupName = extractVarGroupName(method.getName());
        int dim = model.getVariables().getDimOfVarGroup(varGroupName);
        Class<?> type = method.getReturnType();

        if (Map.class.equals(type)) {
          return extractSolution(method, varGroupName);

        } else {

          return extractSolution(type, varGroupName, dim);
        }
      }
      throw new RuntimeException("Unknown Methodname: " + method.getName());

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private Object extractSolution(
      Method method,
      String varGroupName)
      throws Exception {
    if (Map.class.equals(method.getReturnType())) {

      Type keyType =
          ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
      if (String.class.equals(keyType)) {

        Type valueType =
            ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[1];

        if (Boolean.class.equals(valueType)) {
          return model.getVariables().getValuesOfVarGroupAsBooleanMap(varGroupName);
        }

        if (Double.class.equals(valueType)) {
          return model.getVariables().getValuesOfVarGroupAsDoubleMap(varGroupName);
        }

        if (Integer.class.equals(valueType)) {
          return model.getVariables().getValuesOfVarGroupAsIntegerMap(varGroupName);
        }

        throw new Exception(
            "Return Type " + valueType.toString() + " for varGroup " + varGroupName + " unknown!");
      }
      throw new Exception(
          "Map key must be String!");
    }

    throw new Exception(
        "Internal Error!");

  }


  private Object extractSolution(
      Class<?> type,
      String varGroupName,
      int dim)
      throws Exception {

    if (Boolean.class.equals(type)) {
      checkCorrectDim(varGroupName, 0, dim);
      return model.getVariables().getValueOfVarAsBool(varGroupName);
    }
    if (Boolean[].class.equals(type)) {
      checkCorrectDim(varGroupName, 1, dim);
      return model.getVariables().getValuesOfVarGroup1DAsBool(varGroupName);
    }
    if (Boolean[][].class.equals(type)) {
      checkCorrectDim(varGroupName, 2, dim);
      return model.getVariables().getValuesOfVarGroup2DAsBool(varGroupName);
    }
    if (Boolean[][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 3, dim);
      return model.getVariables().getValuesOfVarGroup3DAsBool(varGroupName);
    }
    if (Boolean[][][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 4, dim);
      return model.getVariables().getValuesOfVarGroup4DAsBool(varGroupName);
    }

    if (Integer.class.equals(type)) {
      checkCorrectDim(varGroupName, 0, dim);
      return model.getVariables().getValueOfVarAsInt(varGroupName);
    }
    if (Integer[].class.equals(type)) {
      checkCorrectDim(varGroupName, 1, dim);
      return model.getVariables().getValuesOfVarGroup1DAsInt(varGroupName);
    }
    if (Integer[][].class.equals(type)) {
      checkCorrectDim(varGroupName, 2, dim);
      return model.getVariables().getValuesOfVarGroup2DAsInt(varGroupName);
    }
    if (Integer[][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 3, dim);
      return model.getVariables().getValuesOfVarGroup3DAsInt(varGroupName);
    }
    if (Integer[][][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 4, dim);
      return model.getVariables().getValuesOfVarGroup4DAsInt(varGroupName);
    }

    if (Double.class.equals(type)) {
      checkCorrectDim(varGroupName, 0, dim);
      return model.getVariables().getValueOfVarAsReal(varGroupName);
    }
    if (Double[].class.equals(type)) {
      checkCorrectDim(varGroupName, 1, dim);
      return model.getVariables().getValuesOfVarGroup1DAsReal(varGroupName);
    }
    if (Double[][].class.equals(type)) {
      checkCorrectDim(varGroupName, 2, dim);
      return model.getVariables().getValuesOfVarGroup2DAsReal(varGroupName);
    }
    if (Double[][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 3, dim);
      return model.getVariables().getValuesOfVarGroup3DAsReal(varGroupName);
    }
    if (Double[][][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 4, dim);
      return model.getVariables().getValuesOfVarGroup4DAsReal(varGroupName);
    }

    throw new Exception(
        "Return Type " + type.toString() + " for varGroup " + varGroupName + " unknown!");
  }

  private Object extractDualValues(
      Class<?> type,
      IConstraint<? super CONSTANTS, VARCLASS, SOLVER> constraintManager,
      int dim)
      throws Exception {

    if (Double.class.equals(type)) {
      checkCorrectDim(constraintManager.getConstraintGroupName(), 0, dim);
      return constraintManager.getDual0D(model.getSolver());
    }
    if (Double[].class.equals(type)) {
      checkCorrectDim(constraintManager.getConstraintGroupName(), 1, dim);
      return constraintManager.getDual1D(model.getSolver());
    }
    if (Double[][].class.equals(type)) {
      checkCorrectDim(constraintManager.getConstraintGroupName(), 2, dim);
      return constraintManager.getDual2D(model.getSolver());
    }
    if (Double[][][].class.equals(type)) {
      checkCorrectDim(constraintManager.getConstraintGroupName(), 3, dim);
      return constraintManager.getDual3D(model.getSolver());
    }
    if (Double[][][][].class.equals(type)) {
      checkCorrectDim(constraintManager.getConstraintGroupName(), 4, dim);
      return constraintManager.getDual4D(model.getSolver());
    }

    throw new Exception(
        "Return Type " + type.toString() + " for dual(s) of constraintGroup "
            + constraintManager.getConstraintGroupName() + " wrong: should be Double!");
  }

  private Object extractReducedCost(
      Method method,
      String varGroupName)
      throws Exception {
    if (Map.class.equals(method.getReturnType())) {

      Type keyType =
          ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
      if (String.class.equals(keyType)) {

        Type valueType =
            ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[1];

        if (Double.class.equals(valueType)) {
          return model.getVariables().getReducedCostOfVarGroupAsMap(varGroupName);
        }

        throw new Exception(
            "Return Type Double (not: " + valueType.toString() +
                ") expected for ReducedCost of varGroup " +
                varGroupName + "!");
      }
      throw new Exception(
          "Map key must be String!");
    }

    throw new Exception(
        "Internal Error!");

  }

  private Object extractReducedCost(
      Class<?> type,
      String varGroupName,
      int dim)
      throws Exception {

    if (Double.class.equals(type)) {
      checkCorrectDim(varGroupName, 0, dim);
      return model.getVariables().getReducedCostOfVar(varGroupName);
    }
    if (Double[].class.equals(type)) {
      checkCorrectDim(varGroupName, 1, dim);
      return model.getVariables().getReducedCostOfVarGroup1D(varGroupName);
    }
    if (Double[][].class.equals(type)) {
      checkCorrectDim(varGroupName, 2, dim);
      return model.getVariables().getReducedCostOfVarGroup2D(varGroupName);
    }
    if (Double[][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 3, dim);
      return model.getVariables().getReducedCostOfVarGroup3D(varGroupName);
    }
    if (Double[][][][].class.equals(type)) {
      checkCorrectDim(varGroupName, 4, dim);
      return model.getVariables().getReducedCostOfVarGroup4D(varGroupName);
    }

    throw new Exception(
        "Return Type Double (not: " + type.toString() + ") expected for ReducedCost of varGroup " +
            varGroupName + "!");
  }

  private void checkCorrectDim(
      String groupName,
      int expected,
      int value) throws Exception {
    if (expected != value) {
      throw new Exception(
          "Solution Interface expects wrong dimensions for " + groupName
              + " (" + expected + ":" + value + ")");
    }
  }
}
