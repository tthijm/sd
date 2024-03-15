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

      for (int i = 0; i < fileNames.length; i++) {
        if (fileNames[i].isDirectory()) {
          compressDirectory(fileNames[i], zippedOutput);
        } else {
          FileInputStream fInput = new FileInputStream(fileNames[i]);
          ZipEntry zipEntry = new ZipEntry(fileNames[i].getName());
          zippedOutput.putNextEntry(zipEntry);

          byte[] fileNumBytes = Files.readAllBytes(Paths.get(fileNames[i].getName()));
          zippedOutput.write(fileNumBytes, 0, fileNumBytes.length);

          fInput.close();
        }
      }
      zippedOutput.close();
      outputFile.close();
    } catch (IOException exception) {
      System.out.println(exception);
    }
  }

  private void compressDirectory(File folder, ZipOutputStream zippedOutput) {
    File[] directoryFiles = folder.listFiles();
    try {
      for (int i = 0; i < Objects.requireNonNull(directoryFiles).length; i++) {
        if (directoryFiles[i].isDirectory()) {
          compressDirectory(directoryFiles[i], zippedOutput);
        }
        FileInputStream fInput = new FileInputStream(directoryFiles[i]);
        ZipEntry zipEntry = new ZipEntry(directoryFiles[i].getName());
        zippedOutput.putNextEntry(zipEntry);

        byte[] fileNumBytes = Files.readAllBytes(Paths.get(directoryFiles[i].getName()));
        zippedOutput.write(fileNumBytes, 0, fileNumBytes.length);

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
