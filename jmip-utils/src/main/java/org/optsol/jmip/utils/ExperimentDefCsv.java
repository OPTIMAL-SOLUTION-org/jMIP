package org.optsol.jmip.utils;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExperimentDefCsv {
  @CsvBindByName
  private String id;
  @CsvBindByName
  private String path;
  @CsvBindByName
  private String filename;
  @CsvBindByName
  private double timelimit;

  public String getResourceFilePathAndName() {
    return enhanceResourcePath(getPath()) + "/" + getFilename();
  }

  private static String enhanceResourcePath(String path) {
    String trim = path.trim();
    if (trim.startsWith("/")) {
      trim = trim.substring(1);
    }
    if (trim.endsWith("/")) {
      trim = trim.substring(0, trim.length() - 1);
    }
    return trim;
  }
}
