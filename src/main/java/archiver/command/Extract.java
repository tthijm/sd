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

      //Create input stream from the archive
      ZipInputStream inputStream = new ZipInputStream(new FileInputStream(archive));
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
        }
        inputStream.closeEntry();
        inputStream.close();
      }
    } catch (IOException err) {
      System.out.println("An error has occurred: " + err.getMessage());
    }
  }
}
