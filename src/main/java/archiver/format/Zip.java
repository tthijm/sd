package archiver.format;

import archiver.level.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;

public class Zip extends Format {

  private static final String NAME = "zip";
  private static final String FILE_EXTENSION = ".zip";
  private static final int MEDIUM_COMPRESSION = 5;
  private static final HashMap<Level, Integer> CONFIGURATION = getConfiguration();

  protected Zip() {
    super(NAME, FILE_EXTENSION);
  }

  private static HashMap<Level, Integer> getConfiguration() {
    return new HashMap<Level, Integer>(
      Map.of(
        Level.none,
        Deflater.NO_COMPRESSION,
        Level.low,
        Deflater.BEST_SPEED,
        Level.medium,
        MEDIUM_COMPRESSION,
        Level.high,
        Deflater.BEST_COMPRESSION
      )
    );
  }

  private void addToArchive(File file, ZipOutputStream output) {
    try {
      if (file.isDirectory()) {
        for (File nestedFile : file.listFiles()) {
          addToArchive(nestedFile, output);
        }
      } else {
        FileInputStream fInput = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getPath());
        byte[] fileNumBytes = Files.readAllBytes(Paths.get(file.getPath()));

        output.putNextEntry(zipEntry);
        output.write(fileNumBytes, 0, fileNumBytes.length);
        fInput.close();
        output.closeEntry();
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void compress(File archiveName, File[] fileNames, Level config) {
    try {
      FileOutputStream outputFile = new FileOutputStream(archiveName);
      ZipOutputStream zippedOutput = new ZipOutputStream(outputFile);

      zippedOutput.setLevel(CONFIGURATION.get(config));

      for (File fileName : fileNames) {
        addToArchive(fileName, zippedOutput);
      }

      zippedOutput.close();
      outputFile.close();
    } catch (IOException exception) {
      System.out.println(exception);
    }
  }

  @Override
  public void decompress(File archiveName, File outputDir) {
    try {
      ZipInputStream inputStream = new ZipInputStream(new FileInputStream(archiveName));
      ZipEntry entry = inputStream.getNextEntry();

      while (entry != null) {
        File newFile = new File(outputDir, entry.getName());

        //Check for ZipSlip
        if (!newFile.getCanonicalPath().startsWith(outputDir.getCanonicalPath() + File.separator)) {
          inputStream.closeEntry();
          inputStream.close();
          throw new IOException("Error: incorrect path\nEntry: " + entry.getName());
        }

        if (entry.isDirectory()) {
          if (!newFile.isDirectory() && !newFile.mkdirs()) {
            inputStream.closeEntry();
            inputStream.close();
            throw new IOException("Error: failed to create directory\nEntry: " + entry);
          }
        } else {
          File parentFile = newFile.getParentFile();

          if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
            inputStream.closeEntry();
            inputStream.close();
            throw new IOException("Error: failed to create directory\nEntry: " + parentFile);
          }

          FileOutputStream outputStream = new FileOutputStream(newFile);
          byte[] buf = new byte[1024];
          int len;

          while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
          }

          outputStream.close();
        }

        entry = inputStream.getNextEntry();
      }

      inputStream.closeEntry();
      inputStream.close();
    } catch (IOException err) {
      System.out.println("An error has occurred: " + err.getMessage());
    }
  }

  @Override
  public File[] getFiles(File archiveName) {
    try {
      ZipInputStream inputStream = new ZipInputStream(new FileInputStream(archiveName));
      ZipEntry entry = inputStream.getNextEntry();
      ArrayList<File> result = new ArrayList<>();

      while (entry != null) {
        result.add(new File(entry.getName()));
        entry = inputStream.getNextEntry();
      }

      inputStream.closeEntry();
      inputStream.close();

      return result.toArray(File[]::new);
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
