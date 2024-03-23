package archiver.format;

import archiver.config.Config;
import java.io.*;

public abstract class Format {

  private final String name;
  private final String fileExtension;
  private static final Format[] FORMATS = { new Zip(), new Tar() };

  protected Format(final String name, final String fileExtension) {
    this.name = name;
    this.fileExtension = fileExtension;
  }

  public static Format getInstance(final String x) {
    for (final Format format : FORMATS) {
      if (format.name.equalsIgnoreCase(x) || x.endsWith(format.fileExtension)) {
        return format;
      }
    }

    return null;
  }

  public String getName() {
    return name;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public abstract void compress(File archiveName, File[] fileNames, Config config);

  public abstract void decompress(File archiveName, File outputDir);

  public abstract String[] getFileNames(File archiveName);
}
