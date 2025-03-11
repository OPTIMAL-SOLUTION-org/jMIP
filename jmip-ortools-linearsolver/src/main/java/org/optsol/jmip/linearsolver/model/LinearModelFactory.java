package org.optsol.jmip.linearsolver.model;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.optsol.jmip.core.model.AbstractModelFactory;
import org.optsol.jmip.core.model.constants.IConstants;

public abstract class LinearModelFactory<
    CONSTANTS extends IConstants>
    extends AbstractModelFactory<CONSTANTS, MPVariable, MPSolver, LinearModel<CONSTANTS>> {

  static {
    Loader.loadNativeLibraries();
  }

  @Override
  public final LinearModel<CONSTANTS> buildModel(
      CONSTANTS constants,
      MPSolver solver) throws Exception {
    LinearModel<CONSTANTS> model =
        new LinearModel<>(solver, generateVariables(), generateObjective(), generateConstraints());

    model.buildOrUpdate(constants);

    return model;
  }
}
