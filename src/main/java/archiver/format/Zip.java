package archiver.format;

import archiver.config.Config;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.*;

public class Zip extends Format {

  @Override
  public void compress(File archiveName, File[] fileNames, Config config) {
    try {
      FileOutputStream outputFile = new FileOutputStream(archiveName);
      ZipOutputStream zippedOutput = new ZipOutputStream(outputFile);

      for (File fileName : fileNames) {
        add(fileName, zippedOutput);
      }
      zippedOutput.close();
      outputFile.close();
    } catch (IOException exception) {
      System.out.println(exception);
    }
  }

  private void add(File file, ZipOutputStream output) {
    try {
      if (file.isDirectory()) {
        for (File nestedFile : file.listFiles()) {
          add(nestedFile, output);
        }
      } else {
        FileInputStream fInput = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getPath());
        output.putNextEntry(zipEntry);

        byte[] fileNumBytes = Files.readAllBytes(Paths.get(file.getPath()));
        output.write(fileNumBytes, 0, fileNumBytes.length);

        fInput.close();
        output.closeEntry();
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void decompress(File archiveName, File outputDir) {}

  @Override
  public File[] getFileNames(File archiveName) {
    return new File[0];
  }
}
