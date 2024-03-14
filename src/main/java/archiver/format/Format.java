package archiver.format;

import archiver.config.Config;
import java.io.*;

public abstract class Format {

  public abstract void compress(File archiveName, File[] fileNames, Config config);

  public abstract void decompress(File archiveName, File outputDir);

  public abstract File[] getFileNames(File archiveName);
}
