package archiver.format.zip;

import archiver.config.Config;
import archiver.format.Format;
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

      for (int i = 0; i < fileNames.length; i++) {
        if (fileNames[i].isDirectory()) {
          File[] directoryFiles = fileNames[i].listFiles();
          if (directoryFiles != null) compress(fileNames[i], directoryFiles, config); else {
            throw new FileNotFoundException("A null directory cannot be zip compressed.");
          }
        }

        FileInputStream fInput = new FileInputStream(fileNames[i]);
        ZipEntry zipEntry = new ZipEntry(fileNames[i].getName());
        zippedOutput.putNextEntry(zipEntry);

        byte[] fileNumBytes = Files.readAllBytes(Paths.get(fileNames[i].getName()));
        zippedOutput.write(fileNumBytes, 0, fileNumBytes.length);

        fInput.close();
      }
      zippedOutput.close();
      outputFile.close();
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
