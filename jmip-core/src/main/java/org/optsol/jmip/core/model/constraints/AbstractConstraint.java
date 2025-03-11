package org.optsol.jmip.core.model.constraints;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.optsol.jmip.core.model.variables.IVariableProvider;

public abstract class AbstractConstraint<SOLVER, CONSTANTS, VARCLASS, CONSTRAINTCLASS>
    implements IConstraint<CONSTANTS, VARCLASS, SOLVER> {

  private final String constraintGroupName;
  private final Map<String, Integer> constraintIndexNames = new HashMap<>();
  private final Map<ConstraintKey, CONSTRAINTCLASS> constraints = new HashMap<>();

  protected AbstractConstraint(String... constraintIndexNames) {
    this.constraintGroupName = this.getClass().getSimpleName();
    for (int i = 0; i < constraintIndexNames.length; i++) {
      String constraintIndexName = constraintIndexNames[i];
      if (this.constraintIndexNames.containsKey(constraintIndexName)) {
        throw new RuntimeException("Duplicate index keys defined!");
      }
      this.constraintIndexNames.put(constraintIndexName, i);
    }
  }

  @Override
  public final void createAndAddOrUpdateConstraints(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables)
      throws Exception {
    //REMOVE
    removeConstraints(
        solver,
        constraints.values());
    constraints.clear();

    //GENERATE AND ADD NEW
    for (ConstraintKey constraintKey : createKeys(constants)) {
      if (constraintKey.constraintIndexTuple.length != getDimOfIndex()) {
        throw new RuntimeException(
            "Dimension of constraint keys does not match definition. "
                + "actual: " + constraintKey.constraintIndexTuple.length
                + " / expected: " + getDimOfIndex());
      }

      CONSTRAINTCLASS constraint =
          generateConstraint(
              solver,
              constants,
              variables,
              constraintKey);

      constraints.put(
          constraintKey,
          constraint);
    }
  }

  protected final String generateConstraintName(ConstraintKey constraintKey) {
    return constraintGroupName + constraintKey.generateIndexString();
  }

  protected Collection<ConstraintKey> createKeys(CONSTANTS constants) {
    HashSet<ConstraintKey> keys = new HashSet<>();
    keys.add(new ConstraintKey());
    return keys;
  }

  protected abstract CONSTRAINTCLASS generateConstraint(
      SOLVER solver,
      CONSTANTS constants,
      IVariableProvider<VARCLASS> variables,
      ConstraintKey constraintKey)
      throws Exception;

  protected abstract void removeConstraints(
      SOLVER solver,
      Collection<CONSTRAINTCLASS> constraints) throws Exception;

  public double getDualOfConstraint(
      SOLVER solver,
      int... constraintIndexTuple) throws Exception {
    return
        getDualOfConstraint(
            solver, new ConstraintKey(constraintIndexTuple)
        );
  }

  public Double getDualOfConstraintOrNull(
      SOLVER solver,
      int... constraintIndexTuple) throws Exception {
    return
        getDualOfConstraintOrNull(
            solver, new ConstraintKey(constraintIndexTuple));
  }

  @Override
  public String getConstraintGroupName() {
    return constraintGroupName;
  }

  private double getDualOfConstraint(
      SOLVER solver,
      ConstraintKey constraintKey) throws Exception {

    return getDualOfConstraint(
        solver, constraints.get(constraintKey)
    );
  }

  protected abstract double getDualOfConstraint(
      SOLVER solver,
      CONSTRAINTCLASS constraint) throws Exception;

  private Double getDualOfConstraintOrNull(
      SOLVER solver,
      ConstraintKey constraintKey) {

    try {
      return getDualOfConstraint(solver, constraintKey);
    } catch (Exception e) {
      return null;
    }
  }

  private void checkCorrectDimension(int expected) {
    if (constraintIndexNames.size() != expected) {
      throw new Error("ConstraintGroup " + constraintGroupName + " is not " + expected + "D!");
    }
  }

  private int getMaxIndex(int dimIdx) {
    return constraints.keySet().stream()
        .mapToInt(constraintKey -> constraintKey.constraintIndexTuple[dimIdx])
        .max()
        .orElseThrow(RuntimeException::new);//TODO: Exceptionbeschreibung
  }

  @Override
  public final int getDimOfIndex() {
    return constraintIndexNames.size();
  }

  @Override
  public final Double getDual0D(
      SOLVER solver) {
    checkCorrectDimension(0);
    return getDualOfConstraintOrNull(solver, new ConstraintKey());
  }

  @Override
  public final Double[] getDual1D(SOLVER solver) {
    checkCorrectDimension(1);
    int maxIndex = getMaxIndex(0);
    return
        IntStream.rangeClosed(0, maxIndex)
            .mapToObj(ConstraintKey::new)
            .map(constraintKey ->
                getDualOfConstraintOrNull(solver, constraintKey))
            .toArray(Double[]::new);
  }

  @Override
  public final Double[][] getDual2D(SOLVER solver) {

    checkCorrectDimension(2);
    int maxIndex1 = getMaxIndex(0);
    int maxIndex2 = getMaxIndex(1);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j -> new ConstraintKey(i, j))
                    .map(constraintKey ->
                        getDualOfConstraintOrNull(solver, constraintKey))
                    .toArray(Double[]::new)
            )
            .toArray(Double[][]::new);
  }

  @Override
  public final Double[][][] getDual3D(SOLVER solver) {

    checkCorrectDimension(3);
    int maxIndex1 = getMaxIndex(0);
    int maxIndex2 = getMaxIndex(1);
    int maxIndex3 = getMaxIndex(2);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k -> new ConstraintKey(i, j, k))
                            .map(constraintKey ->
                                getDualOfConstraintOrNull(solver, constraintKey))
                            .toArray(Double[]::new)
                    )
                    .toArray(Double[][]::new))
            .toArray(Double[][][]::new);
  }

  @Override
  public final Double[][][][] getDual4D(SOLVER solver) {

    checkCorrectDimension(4);
    int maxIndex1 = getMaxIndex(0);
    int maxIndex2 = getMaxIndex(1);
    int maxIndex3 = getMaxIndex(2);
    int maxIndex4 = getMaxIndex(3);

    return
        IntStream.rangeClosed(0, maxIndex1)
            .mapToObj(i ->
                IntStream.rangeClosed(0, maxIndex2)
                    .mapToObj(j ->
                        IntStream.rangeClosed(0, maxIndex3)
                            .mapToObj(k ->
                                IntStream.rangeClosed(0, maxIndex4)
                                    .mapToObj(l -> new ConstraintKey(i, j, k, l))
                                    .map(constraintKey ->
                                        getDualOfConstraintOrNull(
                                            solver, constraintKey
                                        ))
                                    .toArray(Double[]::new)
                            )
                            .toArray(Double[][]::new))
                    .toArray(Double[][][]::new))
            .toArray(Double[][][][]::new);
  }

  protected class ConstraintKey {
    private final int[] constraintIndexTuple;

    public ConstraintKey(int... constraintIndexTuple) {
      this.constraintIndexTuple = constraintIndexTuple;
    }

    public int get(String constraintIndexName) {
      return constraintIndexTuple[getIndex(constraintIndexName)];
    }

    private int getIndex(String constraintIndexName) {
      return constraintIndexNames.get(constraintIndexName);
    }

    private String getIndexName(int index) {
      return
          constraintIndexNames.entrySet().stream()
              .filter(entry -> entry.getValue() == index)
              .map(Map.Entry::getKey)
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Index unknown!"));
    }

    private String generateIndexString() {
      StringBuilder stringBuilder = new StringBuilder();

      constraintIndexNames.values().stream()
          .sorted()
          .map(this::getIndexName)
          .forEach(
              indexName ->
                  stringBuilder
                      .append("_")
                      .append(indexName)
                      .append(get(indexName)));

      return stringBuilder.toString();
    }

    @Override
    public String toString() {
      String constraintKey = constraintGroupName;
      if (constraintIndexTuple.length > 0) {
        constraintKey += "_"
            + Arrays.stream(constraintIndexTuple)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining("_"));
      }
      return constraintKey;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ConstraintKey that = (ConstraintKey) o;

      return Arrays.equals(constraintIndexTuple, that.constraintIndexTuple);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(constraintIndexTuple);
    }
  }
}
