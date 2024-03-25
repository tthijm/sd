package archiver.format;

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
  private static final HashMap<String, Integer> CONFIGURATION = new HashMap<String, Integer>(
    Map.of(
      "none",
      Deflater.NO_COMPRESSION,
      "default",
      Deflater.DEFAULT_COMPRESSION,
      "high",
      Deflater.BEST_COMPRESSION,
      "fast",
      Deflater.BEST_SPEED
    )
  );

  public Zip() {
    super(NAME, FILE_EXTENSION);
  }

  @Override
  public void compress(File archiveName, File[] fileNames, String config) {
    try {
      FileOutputStream outputFile = new FileOutputStream(archiveName);
      ZipOutputStream zippedOutput = new ZipOutputStream(outputFile);
      zippedOutput.setLevel(CONFIGURATION.get(config));

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
  public void decompress(File archiveName, File outputDir) {
    try {
      //Create input stream from the archive
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

        //Check if directory exists or can be created
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

          //Writing to the new file
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
      //Create input stream from the archive
      ZipInputStream inputStream = new ZipInputStream(new FileInputStream(archiveName));
      ZipEntry entry = inputStream.getNextEntry();

      ArrayList<File> result = new ArrayList<>();
      while (entry != null) {
        result.add(new File(entry.getName()));
        entry = inputStream.getNextEntry();
      }

      inputStream.closeEntry();
      inputStream.close();
      return result.toArray(new File[0]);
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
