package org.optsol.jmip.core.model.variables;


public interface IVariableProvider<VARCLASS> {
  VARCLASS getVar(
      String varGroupName,
      int... varIndexTuple) throws Exception;
}
