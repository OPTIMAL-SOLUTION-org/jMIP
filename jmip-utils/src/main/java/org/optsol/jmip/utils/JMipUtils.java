package org.optsol.jmip.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JMipUtils {

  public static <CLASS> List<CLASS> readJsonData(
      List<String> resourceFilePathAndNames,
      Class<CLASS> dataClazz) {

    return
        resourceFilePathAndNames.stream()
            .map(resourceFilePathAndName ->
                readJsonData(resourceFilePathAndName, dataClazz))
            .collect(Collectors.toList());
  }

  public static <CLASS> CLASS readJsonData(
      String resourceFilePathAndName,
      Class<CLASS> dataClazz) {

    ObjectMapper mapper = generateObjectMapper();
    try {
      return mapper.readValue(
          getResourceAsStream(resourceFilePathAndName),
          dataClazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static ObjectMapper generateObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    return mapper;
  }

  public static <CLASS> List<CLASS> readCsvData(
      String resourceFilePathAndName,
      Class<CLASS> csvBeanClazz) {

    InputStreamReader streamReader =
        new InputStreamReader(getResourceAsStream(resourceFilePathAndName), StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader);

    CsvToBean<CLASS> csvReader = new CsvToBeanBuilder<CLASS>(reader)
        .withType(csvBeanClazz)
        //.withSkipLines(1)
        .withSeparator(';')
        .build();

    return csvReader.parse();
  }

  public static void writeJsonFile(
      String fileName,
      Object object) {

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(
          generateObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object)
      );
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> getAllFileNamesInFolder(String resourceFolderPath) {
    try {
      File[] files = new File(Thread.currentThread()
          .getContextClassLoader().getResource(resourceFolderPath).getPath()).listFiles();

      return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static List<ExperimentDefCsv> getAllExperimentsFromExperimentFile(
      String resourceExperimentDefCsvPathAndFileName) {
    return
        readCsvData(resourceExperimentDefCsvPathAndFileName, ExperimentDefCsv.class);
  }

  public static Map<String, String> getAllFileNamesFromExperimentFile(
      String resourceExperimentDefCsvPathAndFileName) {
    return
        getAllExperimentsFromExperimentFile(resourceExperimentDefCsvPathAndFileName).stream()
            .collect(Collectors.toMap(
                ExperimentDefCsv::getId,
                ExperimentDefCsv::getResourceFilePathAndName));
  }


  public static <CLASS> Map<String, CLASS> getAllConstantsFromExperimentFile(
      String resourceExperimentDefCsvPathAndFileName,
      Class<CLASS> constantsClazz) {
    return
        getAllExperimentsFromExperimentFile(resourceExperimentDefCsvPathAndFileName).stream()
            .collect(Collectors.toMap(
                ExperimentDefCsv::getId,
                experimentDefCsv -> readJsonData(
                    experimentDefCsv.getResourceFilePathAndName(), constantsClazz)));
  }


  public static InputStream getResourceAsStream(String resourceFilePathAndName) {
    if (resourceFilePathAndName.startsWith("/")) {
      resourceFilePathAndName = resourceFilePathAndName.substring(1);
    }

    return Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream(resourceFilePathAndName);
  }
}
