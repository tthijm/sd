package archiver.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Extract {

  public File createFile(File dir, ZipEntry ent) throws IOException {
    File newFile = new File(dir, ent.getName());

    //Check for ZipSlip
    if (!newFile.getCanonicalPath().startsWith(dir.getCanonicalPath() + File.separator)) {
      throw new IOException("Error: incorrect path\nEntry: " + ent.getName());
    }
    return newFile;
  }

  public void run(String[] args) {
    try {
      String archive = args[0];
      File outputDir = new File(args[1]);

      //Create input stream from the archive
      ZipInputStream inputStream = new ZipInputStream(new FileInputStream(archive));
      ZipEntry entry = inputStream.getNextEntry();
      while (entry != null) {
        File newFile = createFile(outputDir, entry);
      }
    } catch (IOException err) {
      System.out.println("An error has occurred: " + err.getMessage());
    }
  }
}
