package org.optsol.jmip.core;

import java.util.Map;

public interface IConstants {
  default Map<String, Double> getLowerBoundOverrides() {
    return null;
  }

  default Map<String, Double> getUpperBoundOverrides() {
    return null;
  }
}
