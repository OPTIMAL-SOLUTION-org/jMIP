package org.optsol.jmip.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractVariableManager<SOLVER, VARCLASS, CONSTANTS extends IConstants>
    implements IVariableManager<CONSTANTS, SOLVER, VARCLASS> {

  private static final double EPSILON = 0.0001;
  protected final Map<VarKey, VARCLASS> variables = new HashMap<>();
  private final Set<String> intVarGroupNames; // = varGroupNames
  private final Map<String, Double> varGroupNameToLowerBoundMap; // key = varGroupName
  private final Map<String, Double> varGroupNameToUpperBoundMap; // key = varGroupName  
  private final Map<VarKey, Double> varKeyToLowerBoundMap; // key = varKey
  private final Map<VarKey, Double> varKeyToUpperBoundMap; // key = varKey
  private Map<VarKey, Double> varKeyToLowerBoundOverridesMap = new HashMap<>();
  // key = varKey
  private Map<VarKey, Double> varKeyToUpperBoundOverridesMap = new HashMap<>();
  // key = varKey
  private final Map<String, Integer> varGroupNameDimensions = new HashMap<>();

  private SOLVER solver = null;

  protected AbstractVariableManager(AbstractBuilder<SOLVER, VARCLASS, CONSTANTS> builder) {
    this.intVarGroupNames = builder.intVarGroupNamesBuilder;
    this.varGroupNameToLowerBoundMap = builder.lowerBoundsVarGroupBuilder;
    this.varGroupNameToUpperBoundMap = builder.upperBoundsVarGroupBuilder;
    this.varKeyToLowerBoundMap = builder.lowerBoundsVarKeyBuilder;
    this.varKeyToUpperBoundMap = builder.upperBoundsVarKeyBuilder;
  }

  @Override
  public void setSolver(SOLVER solver) {
    if (this.solver != null) {
      throw new Error("Can set solver only once!");
    }
    this.solver = solver;
  }

  @Override
  public boolean existsVarGroupName(String varGroupName) {
    return varGroupNameDimensions.containsKey(varGroupName);
  }

  @Override
  public int getDimOfVarGroup(String varGroupName) {
    return varGroupNameDimensions.get(varGroupName);
  }

  private VARCLASS getVar(
      boolean createIfNotExists,
      VarKey varKey) throws Exception {

    if (createIfNotExists
        && !variables.containsKey(varKey)) {
      variables.put(varKey, generateNewVar(varKey));
    }

    if (!variables.containsKey(varKey)) {
      throw new RuntimeException("Requested variable " + varKey.toString() + " was never defined!");
    }

    return variables.get(varKey);
  }

  @Override
  public VARCLASS getVar(
      String varGroupName,
      int... varIndexTuple) throws Exception {
    return getVar(
        true,
        varGroupName,
        varIndexTuple);
  }

  @Override
  public VARCLASS getVar(
      boolean createIfNotExists,
      String varGroupName,
      int... varIndexTuple) throws Exception {
    return getVar(
        createIfNotExists,
        new VarKey(
            varGroupName,
            varIndexTuple)
    );
  }

  private VARCLASS generateNewVar(
      VarKey varKey) throws Exception {
    //check: all variables of group have same index dimensions
    Integer currDimension =
        varGroupNameDimensions.putIfAbsent(
            varKey.varGroupName, varKey.varIndexTuple.length);
    if (currDimension != null &&
        currDimension != varKey.varIndexTuple.length) {
      throw new Error(
          "VariableGroup " + varKey.varGroupName + " used in different index dimensions!");
    }

    Double lowerBound = determineLowerBound(varKey);
    Double upperBound = determineUpperBound(varKey);

    if (intVarGroupNames.contains(varKey.varGroupName)) {
      return createIntVar(varKey, solver, upperBound, lowerBound);
    } else {
      return createNumVar(varKey, solver, upperBound, lowerBound);
    }
  }

  private Double determineLowerBound(VarKey varKey) {
    if (varKeyToLowerBoundOverridesMap.containsKey(varKey)) {
      return varKeyToLowerBoundOverridesMap.get(varKey);
    }

    Double lowerBoundVarGroup =
        varGroupNameToLowerBoundMap.getOrDefault(varKey.varGroupName, null);
    Double lowerBoundVarKey =
        varKeyToLowerBoundMap.getOrDefault(varKey, null);

    if (lowerBoundVarGroup != null && lowerBoundVarKey != null) {
      return Math.max(lowerBoundVarGroup, lowerBoundVarKey);
    }
    if (lowerBoundVarGroup != null) {
      return lowerBoundVarGroup;
    }
    return lowerBoundVarKey;
  }

  private Double determineUpperBound(VarKey varKey) {
    if (varKeyToUpperBoundOverridesMap.containsKey(varKey)) {
      return varKeyToUpperBoundOverridesMap.get(varKey);
    }

    Double upperBoundVarGroup =
        varGroupNameToUpperBoundMap.getOrDefault(varKey.varGroupName, null);
    Double upperBoundVarKey =
        varKeyToUpperBoundMap.getOrDefault(varKey, null);

    if (upperBoundVarGroup != null && upperBoundVarKey != null) {
      return Math.min(upperBoundVarGroup, upperBoundVarKey);
    }
    if (upperBoundVarGroup != null) {
      return upperBoundVarGroup;
    }
    return upperBoundVarKey;
  }

  @Override
  public final void updateVariables(CONSTANTS constants) throws Exception {
    if (constants.getLowerBoundOverrides() == null) {
      this.varKeyToLowerBoundOverridesMap = new HashMap<>();
    } else {
      this.varKeyToLowerBoundOverridesMap =
          constants.getLowerBoundOverrides().entrySet().stream()
              .collect(Collectors.toMap(
                  entry -> VarKey.fromString(entry.getKey()),
                  Map.Entry::getValue));
    }

    if (constants.getUpperBoundOverrides() == null) {
      this.varKeyToUpperBoundOverridesMap = new HashMap<>();
    } else {
      this.varKeyToUpperBoundOverridesMap =
          constants.getUpperBoundOverrides().entrySet().stream()
              .collect(Collectors.toMap(
                  entry -> VarKey.fromString(entry.getKey()),
                  Map.Entry::getValue));
    }

    for (VarKey varKey : variables.keySet()) {
      updateBoundsOfVar(
          variables.get(varKey),
          solver,
          determineUpperBound(varKey),
          determineLowerBound(varKey)
      );
    }
  }

  protected abstract VARCLASS createIntVar(
      VarKey varKey,
      SOLVER solver,
      Double upperBound,
      Double lowerBound) throws Exception;

  protected abstract VARCLASS createNumVar(
      VarKey varKey,
      SOLVER solver,
      Double upperBound,
      Double lowerBound) throws Exception;

  protected abstract void updateBoundsOfVar(
      VARCLASS var,
      SOLVER solver,
      Double upperBound,
      Double lowerBound) throws Exception;

  protected abstract double getValueOfVar(
      SOLVER solver,
      VARCLASS var) throws Exception;

  private double getValueOfVarAsReal(
      SOLVER solver,
      VarKey varKey) throws Exception {

    return getValueOfVar(
        solver, getVar(false, varKey)
    );
  }

  private Double getValueOfVarAsRealOrNull(
      VarKey varKey) {

    try {
      return getValueOfVarAsReal(solver, varKey);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public double getValueOfVarAsReal(
      String varGroupName,
      int... varIndexTuple) throws Exception {

    return getValueOfVarAsReal(
        solver, new VarKey(varGroupName, varIndexTuple)
    );
  }

  @Override
  public boolean getValueOfVarAsBool(
      String varGroupName,
      int... varIndexTuple) throws Exception {

    return
        getValueOfVarAsBool(
            new VarKey(varGroupName, varIndexTuple)
        );
  }

  private Boolean getValueOfVarAsBoolOrNull(
      VarKey varKey) {

    try {
      return getValueOfVarAsBool(varKey);
    } catch (Exception e) {
      return null;
    }
  }

  private boolean getValueOfVarAsBool(
      VarKey varKey) throws Exception {
    checkIsDefinedAsBool(varKey.varGroupName);

    return getValueOfVarAsBool(
        getVar(false, varKey)
    );
  }

  private boolean getValueOfVarAsBool(
      VARCLASS var) throws Exception {
    return getValueOfVarAsInt(var) > 0.5;
  }

  @Override
  public double getReducedCostOfVar(
      String varGroupName,
      int... varIndexTuple) throws Exception {

    return getReducedCostOfVar(
        solver, new VarKey(varGroupName, varIndexTuple)
    );
  }

  protected abstract double getReducedCostOfVar(
      SOLVER solver,
      VARCLASS var) throws Exception;

  private double getReducedCostOfVar(
      SOLVER solver,
      VarKey varKey) throws Exception {

    return getReducedCostOfVar(
        solver, getVar(false, varKey)
    );
  }

  private Double getReducedCostOfVarOrNull(
      VarKey varKey) {

    try {
      return getReducedCostOfVar(solver, varKey);
    } catch (Exception e) {
      return null;
    }
  }

  private void checkIsDefinedAsBool(String varGroupName) {
    try {
      checkIsDefinedAsInt(varGroupName);
    } catch (Error er) {
      throw new Error("Requested bool var from group " + varGroupName + " is not declared as int!");
    }
    if (!varGroupNameToLowerBoundMap.containsKey(varGroupName)
        || Math.abs(varGroupNameToLowerBoundMap.get(varGroupName)) > EPSILON) {
      throw new Error("Requested bool var from group " + varGroupName + " is missing lower bound!");
    }
    if (!varGroupNameToUpperBoundMap.containsKey(varGroupName)
        || Math.abs(varGroupNameToUpperBoundMap.get(varGroupName) - 1.) > EPSILON) {
      throw new Error("Requested bool var from group " + varGroupName + " is missing upper bound!");
    }
  }

  private void checkIsDefinedAsInt(String varGroupName) {
    if (!intVarGroupNames.contains(varGroupName)) {
      throw new Error("Requested int var from group " + varGroupName + " is not declared as int!");
    }
  }

  private int getMaxIndex(
      String varGroupName,
      int dimIdx) {
    return variables.keySet().stream()
        .filter(varKey -> varKey.varGroupName.equals(varGroupName))
        .mapToInt(varKey -> varKey.varIndexTuple[dimIdx])
        .max()
        .orElseThrow(RuntimeException::new);
  }

  private void checkCorrectVarGroupDimension(
      String varGroupName,
      int expected) {
    if (!varGroupNameDimensions.containsKey(varGroupName) ||
        varGroupNameDimensions.get(varGroupName) != expected) {
      throw new Error("VarGroup " + varGroupName + " is not " + expected + "D!");
    }
  }

  @Override
  public Double[] getValuesOfVarGroup1DAsReal(
      String varGroupName) {
    checkCorrectVarGroupDimension(varGroupName, 1);
    int maxIndex = getMaxIndex(varGroupName, 0);

    return
        IntStream.rangeClosed(0, maxIndex)
            .mapToObj(i -> new VarKey(varGroupName, i))
            .map(this::getValueOfVarAsRealOrNull)
            .toArray(Double[]::new);
  }

  @Override
  public Double[][] getValuesOfVarGroup2DAsReal(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 2);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j -> new VarKey(varGroupName, i, j))
                    .map(varKey -> getValueOfVarAsRealOrNull(varKey))
                    .toArray(Double[]::new)
            )
            .toArray(Double[][]::new);
  }

  @Override
  public Double[][][] getValuesOfVarGroup3DAsReal(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 3);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k -> new VarKey(varGroupName, i, j, k))
                            .map(varKey -> getValueOfVarAsRealOrNull(varKey))
                            .toArray(Double[]::new)
                    )
                    .toArray(Double[][]::new))
            .toArray(Double[][][]::new);
  }

  @Override
  public Double[][][][] getValuesOfVarGroup4DAsReal(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 4);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);
    int maxIndex4 = getMaxIndex(varGroupName, 3);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k ->
                                IntStream.rangeClosed(0, maxIndex4)
                                    .mapToObj(l -> new VarKey(varGroupName, i, j, k, l))
                                    .map(varKey -> getValueOfVarAsRealOrNull(varKey))
                                    .toArray(Double[]::new)
                            )
                            .toArray(Double[][]::new))
                    .toArray(Double[][][]::new))
            .toArray(Double[][][][]::new);
  }

  @Override
  public Boolean[] getValuesOfVarGroup1DAsBool(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 1);
    int maxIndex = getMaxIndex(varGroupName, 0);

    return
        IntStream.rangeClosed(0, maxIndex)
            .mapToObj(i -> new VarKey(varGroupName, i))
            .map(varKey -> getValueOfVarAsBoolOrNull(varKey))
            .toArray(Boolean[]::new);
  }

  @Override
  public Boolean[][] getValuesOfVarGroup2DAsBool(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 2);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j -> new VarKey(varGroupName, i, j))
                    .map(varKey -> getValueOfVarAsBoolOrNull(varKey))
                    .toArray(Boolean[]::new)
            )
            .toArray(Boolean[][]::new);
  }

  @Override
  public Boolean[][][] getValuesOfVarGroup3DAsBool(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 3);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k -> new VarKey(varGroupName, i, j, k))
                            .map(varKey -> getValueOfVarAsBoolOrNull(varKey))
                            .toArray(Boolean[]::new)
                    )
                    .toArray(Boolean[][]::new))
            .toArray(Boolean[][][]::new);
  }

  @Override
  public Boolean[][][][] getValuesOfVarGroup4DAsBool(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 4);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);
    int maxIndex4 = getMaxIndex(varGroupName, 3);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k ->
                                IntStream.rangeClosed(0, maxIndex4)
                                    .mapToObj(l -> new VarKey(varGroupName, i, j, k, l))
                                    .map(varKey -> getValueOfVarAsBoolOrNull(varKey))
                                    .toArray(Boolean[]::new)
                            )
                            .toArray(Boolean[][]::new))
                    .toArray(Boolean[][][]::new))
            .toArray(Boolean[][][][]::new);
  }

  @Override
  public Integer[] getValuesOfVarGroup1DAsInt(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 1);
    int maxIndex = getMaxIndex(varGroupName, 0);

    return
        IntStream.rangeClosed(0, maxIndex)
            .mapToObj(i -> new VarKey(varGroupName, i))
            .map(varKey -> getValueOfVarAsIntOrNull(varKey))
            .toArray(Integer[]::new);
  }

  @Override
  public Integer[][] getValuesOfVarGroup2DAsInt(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 2);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j -> new VarKey(varGroupName, i, j))
                    .map(varKey -> getValueOfVarAsIntOrNull(varKey))
                    .toArray(Integer[]::new)
            )
            .toArray(Integer[][]::new);
  }

  @Override
  public Integer[][][] getValuesOfVarGroup3DAsInt(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 3);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k -> new VarKey(varGroupName, i, j, k))
                            .map(varKey -> getValueOfVarAsIntOrNull(varKey))
                            .toArray(Integer[]::new)
                    )
                    .toArray(Integer[][]::new))
            .toArray(Integer[][][]::new);
  }

  @Override
  public Integer[][][][] getValuesOfVarGroup4DAsInt(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 4);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);
    int maxIndex4 = getMaxIndex(varGroupName, 3);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k ->
                                IntStream.rangeClosed(0, maxIndex4)
                                    .mapToObj(l -> new VarKey(varGroupName, i, j, k, l))
                                    .map(varKey -> getValueOfVarAsIntOrNull(varKey))
                                    .toArray(Integer[]::new)
                            )
                            .toArray(Integer[][]::new))
                    .toArray(Integer[][][]::new))
            .toArray(Integer[][][][]::new);
  }

  @Override
  public int getValueOfVarAsInt(
      String varGroupName,
      int... varIndexTuple) throws Exception {

    return
        getValueOfVarAsInt(
            new VarKey(varGroupName, varIndexTuple)
        );
  }

  private Integer getValueOfVarAsIntOrNull(
      VarKey varKey) {

    try {
      return getValueOfVarAsInt(varKey);
    } catch (Exception e) {
      return null;
    }
  }

  private int getValueOfVarAsInt(
      VarKey varKey)
      throws Exception {
    checkIsDefinedAsInt(varKey.varGroupName);

    return getValueOfVarAsInt(
        getVar(false, varKey)
    );
  }

  private int getValueOfVarAsInt(
      VARCLASS var)
      throws Exception {

    return (int) Math.round(getValueOfVar(solver, var));
  }

  @Override
  public Map<String, Boolean> getValuesOfVarGroupAsBooleanMap(
      String varGroupName) {

    return
        this.variables.keySet().stream()
            .filter(varKey -> varKey.varGroupName.equals(varGroupName))
            .collect(Collectors.toMap(
                VarKey::toString,
                this::getValueOfVarAsBoolOrNull));
  }

  @Override
  public Map<String, Double> getValuesOfVarGroupAsDoubleMap(
      String varGroupName) {

    return
        this.variables.keySet().stream()
            .filter(varKey -> varKey.varGroupName.equals(varGroupName))
            .collect(Collectors.toMap(
                VarKey::toString,
                this::getValueOfVarAsRealOrNull));
  }

  @Override
  public Map<String, Integer> getValuesOfVarGroupAsIntegerMap(
      String varGroupName) {

    return
        this.variables.keySet().stream()
            .filter(varKey -> varKey.varGroupName.equals(varGroupName))
            .collect(Collectors.toMap(
                VarKey::toString,
                this::getValueOfVarAsIntOrNull));
  }

  @Override
  public Double[] getReducedCostOfVarGroup1D(
      String varGroupName) {
    checkCorrectVarGroupDimension(varGroupName, 1);
    int maxIndex = getMaxIndex(varGroupName, 0);

    return
        IntStream.rangeClosed(0, maxIndex)
            .mapToObj(i -> new VarKey(varGroupName, i))
            .map(this::getReducedCostOfVarOrNull)
            .toArray(Double[]::new);
  }

  @Override
  public Double[][] getReducedCostOfVarGroup2D(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 2);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j -> new VarKey(varGroupName, i, j))
                    .map(varKey -> getReducedCostOfVarOrNull(varKey))
                    .toArray(Double[]::new)
            )
            .toArray(Double[][]::new);
  }

  @Override
  public Double[][][] getReducedCostOfVarGroup3D(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 3);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k -> new VarKey(varGroupName, i, j, k))
                            .map(varKey -> getReducedCostOfVarOrNull(varKey))
                            .toArray(Double[]::new)
                    )
                    .toArray(Double[][]::new))
            .toArray(Double[][][]::new);
  }

  @Override
  public Double[][][][] getReducedCostOfVarGroup4D(
      String varGroupName) {

    checkCorrectVarGroupDimension(varGroupName, 4);
    int maxIndex1 = getMaxIndex(varGroupName, 0);
    int maxIndex2 = getMaxIndex(varGroupName, 1);
    int maxIndex3 = getMaxIndex(varGroupName, 2);
    int maxIndex4 = getMaxIndex(varGroupName, 3);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k ->
                                IntStream.rangeClosed(0, maxIndex4)
                                    .mapToObj(l -> new VarKey(varGroupName, i, j, k, l))
                                    .map(varKey -> getReducedCostOfVarOrNull(varKey))
                                    .toArray(Double[]::new)
                            )
                            .toArray(Double[][]::new))
                    .toArray(Double[][][]::new))
            .toArray(Double[][][][]::new);
  }

  @Override
  public Map<String, Double> getReducedCostOfVarGroupAsMap(
      String varGroupName) {

    return
        this.variables.keySet().stream()
            .filter(varKey -> varKey.varGroupName.equals(varGroupName))
            .collect(Collectors.toMap(
                VarKey::toString,
                this::getReducedCostOfVarOrNull));
  }

  public abstract static class AbstractBuilder<SOLVER, VARCLASS, CONSTANTS extends IConstants> {

    private final Set<String> intVarGroupNamesBuilder = new HashSet<>();
    private final Map<String, Double> lowerBoundsVarGroupBuilder = new HashMap<>();
    private final Map<String, Double> upperBoundsVarGroupBuilder = new HashMap<>();
    private final Map<VarKey, Double> lowerBoundsVarKeyBuilder = new HashMap<>();
    private final Map<VarKey, Double> upperBoundsVarKeyBuilder = new HashMap<>();

    public AbstractBuilder<SOLVER, VARCLASS, CONSTANTS> addIntVar(String intVarGroupName) {
      intVarGroupNamesBuilder.add(intVarGroupName);
      return this;
    }

    public AbstractBuilder<SOLVER, VARCLASS, CONSTANTS> addLowerBound(
        String varGroupName,
        double lowerBound) {
      lowerBoundsVarGroupBuilder.put(varGroupName, lowerBound);
      return this;
    }

    public AbstractBuilder<SOLVER, VARCLASS, CONSTANTS> addLowerBound(
        double lowerBound,
        String varGroupName,
        int... varIndexTuple) {
      lowerBoundsVarKeyBuilder.put(new VarKey(varGroupName, varIndexTuple), lowerBound);
      return this;
    }

    public AbstractBuilder<SOLVER, VARCLASS, CONSTANTS> addUpperBound(
        String varGroupName,
        double upperBound) {
      upperBoundsVarGroupBuilder.put(varGroupName, upperBound);
      return this;
    }

    public AbstractBuilder<SOLVER, VARCLASS, CONSTANTS> addUpperBound(
        double upperBound,
        String varGroupName,
        int... varIndexTuple) {
      upperBoundsVarKeyBuilder.put(new VarKey(varGroupName, varIndexTuple), upperBound);
      return this;
    }

    public abstract AbstractVariableManager<SOLVER, VARCLASS, CONSTANTS> build();
  }

  protected static class VarKey {
    private final String varGroupName;
    private final int[] varIndexTuple;

    public VarKey(
        String varGroupName,
        int... varIndexTuple) {
      this.varGroupName = varGroupName;
      this.varIndexTuple = varIndexTuple;
    }

    public static VarKey fromString(String varKeyString) {
      String[] field = varKeyString.split("_");

      int indexSize;
      for (indexSize = 0; indexSize < field.length; indexSize++) {
        try {
          Integer.valueOf(field[field.length - indexSize - 1]);
        } catch (NumberFormatException nfe) {
          break;
        }
      }

      return
          new VarKey(
              Arrays.stream(field)
                  .limit(field.length - indexSize)
                  .collect(Collectors.joining("_")),
              Arrays.stream(field)
                  .skip(field.length - indexSize)
                  .mapToInt(Integer::valueOf)
                  .toArray());
    }

    @Override
    public String toString() {
      String varKey = varGroupName;
      if (varIndexTuple.length > 0) {
        varKey += "_"
            + Arrays.stream(varIndexTuple)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining("_"));
      }
      return varKey;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      VarKey varKey = (VarKey) o;

      if (!Objects.equals(varGroupName, varKey.varGroupName)) {
        return false;
      }
      return Arrays.equals(varIndexTuple, varKey.varIndexTuple);
    }

    @Override
    public int hashCode() {
      int result = varGroupName != null ? varGroupName.hashCode() : 0;
      result = 31 * result + Arrays.hashCode(varIndexTuple);
      return result;
    }
  }
}
