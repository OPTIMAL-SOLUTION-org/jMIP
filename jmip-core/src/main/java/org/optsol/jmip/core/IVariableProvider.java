package org.optsol.jmip.core;


public interface IVariableProvider<VARCLASS> {
  VARCLASS getVar(
      String varGroupName,
      int... varIndexTuple) throws Exception;
}
