package org.optsol.jmip.ortools;

import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.Collection;
import org.optsol.jmip.core.IConstants;
import org.optsol.jmip.core.IConstraintManager;
import org.optsol.jmip.core.IObjectiveManager;
import org.optsol.jmip.core.IVariableManager;

public class CbcModel<CONSTANTS extends IConstants> extends OrtoolsModel<CONSTANTS> {

  public CbcModel(
      IVariableManager<? super CONSTANTS, MPSolver, MPVariable> variables,
      IObjectiveManager<? super CONSTANTS, MPVariable, MPSolver> objective,
      Collection<IConstraintManager<? super CONSTANTS, MPVariable, MPSolver>> constraints)
      throws Exception {
    super("CBC", variables, objective, constraints);
  }
}
