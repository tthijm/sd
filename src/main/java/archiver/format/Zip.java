package archiver.format;

import archiver.config.Config;
import archiver.format.Format;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.*;

public class Zip extends Format {

  @Override
  public void compress(File archiveName, File[] fileNames, Config config) {
    try {
      FileOutputStream outputFile = new FileOutputStream(archiveName);
      ZipOutputStream zippedOutput = new ZipOutputStream(outputFile);

      for (File fileName : fileNames) {
        add(fileName, zippedOutput, "");
      }
      zippedOutput.close();
      outputFile.close();
    } catch (IOException exception) {
      System.out.println(exception);
    }
  }

  private void add(File file, ZipOutputStream output, String path) {
    String name = path + "/" + file.getName();
    try {
      if (file.isDirectory()) {
        for (File nestedFile : file.listFiles()) {
          add(nestedFile, output, name);
        }
      } else {
        FileInputStream fInput = new FileInputStream(name);
        ZipEntry zipEntry = new ZipEntry(name);
        output.putNextEntry(zipEntry);

        byte[] fileNumBytes = Files.readAllBytes(Paths.get(name));
        output.write(fileNumBytes, 0, fileNumBytes.length);

        fInput.close();
      }
    } catch (IOException exception) {
      System.out.println(exception);
    }
  }

  @Override
  public void decompress(File archiveName, File outputDir) {}

  @Override
  public File[] getFileNames(File archiveName) {
    return new File[0];
  }
}
