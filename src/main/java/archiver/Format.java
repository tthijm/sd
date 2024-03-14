package archiver;

import java.io.*;

abstract class Format {

  abstract void compress(File archiveName, File[] fileNames, Config config);

  abstract void decompress(File archiveName, File outputDir);

  abstract File[] getFileNames(File archiveName);
}
