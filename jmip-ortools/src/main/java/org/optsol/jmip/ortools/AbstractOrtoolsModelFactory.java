package org.optsol.jmip.ortools;

import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.optsol.jmip.core.AbstractModelFactory;
import org.optsol.jmip.core.IConstants;

public abstract class AbstractOrtoolsModelFactory<CONSTANTS extends IConstants>
    extends AbstractModelFactory<CONSTANTS, MPVariable, MPSolver, OrtoolsModel<CONSTANTS>> {

  private final SolverEngine solverEngine;

  protected AbstractOrtoolsModelFactory(SolverEngine solverEngine) {
    this.solverEngine = solverEngine;
  }

  @Override
  public final OrtoolsModel<CONSTANTS> buildModel(CONSTANTS constants) throws Exception {
    switch (solverEngine) {

      case GLOP:
        return buildModel_GLOP(constants);
      case CBC:
        return buildModel_CBC(constants);
      case SCIP:
        return buildModel_SCIP(constants);
      case GUROBI:
        return buildModel_GUROBI(constants);

      default:
        throw new Error("Unknown Solver");
    }
  }

  private GlopModel<CONSTANTS> buildModel_GLOP(CONSTANTS constants)
      throws Exception {

    GlopModel<CONSTANTS> glopModel =
        new GlopModel<>(generateVarManager(), generateObjective(), generateConstraints());

    glopModel.buildOrUpdate(constants);

    return glopModel;
  }

  private CbcModel<CONSTANTS> buildModel_CBC(CONSTANTS constants)
      throws Exception {

    CbcModel<CONSTANTS> cbcModel =
        new CbcModel<>(generateVarManager(), generateObjective(), generateConstraints());

    cbcModel.buildOrUpdate(constants);

    return cbcModel;
  }

  private ScipModel<CONSTANTS> buildModel_SCIP(CONSTANTS constants)
      throws Exception {

    ScipModel<CONSTANTS> scipModel =
        new ScipModel<>(generateVarManager(), generateObjective(), generateConstraints());

    scipModel.buildOrUpdate(constants);

    return scipModel;
  }

  private GurobiModel<CONSTANTS> buildModel_GUROBI(CONSTANTS constants)
      throws Exception {

    GurobiModel<CONSTANTS> gurobiModel =
        new GurobiModel<>(generateVarManager(), generateObjective(), generateConstraints());

    gurobiModel.buildOrUpdate(constants);

    return gurobiModel;
  }
}
