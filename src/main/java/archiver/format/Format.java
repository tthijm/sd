package archiver.format;

import archiver.level.*;
import java.io.*;
import java.util.ArrayList;

public abstract class Format implements Cloneable {

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

  public static Format[] getFormats() {
    final ArrayList<Format> result = new ArrayList<>();

    for (final Format format : FORMATS) {
      try {
        result.add((Format) format.clone());
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

    return result.toArray(Format[]::new);
  }

  public String getName() {
    return name;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public abstract void compress(File archiveName, File[] fileNames, Level config);

  public abstract void decompress(File archiveName, File outputDir);

  public abstract File[] getFiles(File archiveName);
}
