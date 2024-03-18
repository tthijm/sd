package archiver.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Extract {

  public void run(String[] args) {
    try {
      String archive = args[0];
      File outputDir = new File(args[1]);

      ZipInputStream inputStream = new ZipInputStream(new FileInputStream(archive));
      ZipEntry entry = inputStream.getNextEntry();
      while (entry != null) {}
    } catch (IOException err) {
      System.out.println("An error has occurred: " + err.getMessage());
    }
  }
}
