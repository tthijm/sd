package archiver.format;

import archiver.config.Config;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;

public class Tar extends Format {

  private static final String NAME = "tar";
  private static final String FILE_EXTENSION = ".tar.bz2";

  public Tar() {
    super(NAME, FILE_EXTENSION);
  }

  private void addToArchive(final File file, final TarArchiveOutputStream output) {
    if (file.isDirectory()) {
      for (final File nestedFile : file.listFiles()) {
        addToArchive(nestedFile, output);
      }

      return;
    }

    try {
      final TarArchiveEntry entry = output.createArchiveEntry(file, file.getPath());
      final InputStream fileStream = Files.newInputStream(file.toPath());

      output.putArchiveEntry(entry);
      IOUtils.copy(fileStream, output);
      output.closeArchiveEntry();
      fileStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void compress(final File archiveName, final File[] fileNames, final Config config) {
    try {
      final OutputStream fileStream = Files.newOutputStream(archiveName.toPath());
      final BZip2CompressorOutputStream compressionStream = new BZip2CompressorOutputStream(fileStream);
      final TarArchiveOutputStream output = new TarArchiveOutputStream(compressionStream);

      for (final File file : fileNames) {
        addToArchive(file, output);
      }

      output.finish();
      output.close();
      compressionStream.close();
      fileStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void decompress(final File archiveName, final File outputDir) {
    try {
      final InputStream fileStream = Files.newInputStream(archiveName.toPath());
      final BufferedInputStream bufferedStream = new BufferedInputStream(fileStream);
      final BZip2CompressorInputStream compressionStream = new BZip2CompressorInputStream(bufferedStream);
      final TarArchiveInputStream input = new TarArchiveInputStream(compressionStream);

      TarArchiveEntry entry = input.getNextEntry();

      while (entry != null) {
        final File file = new File(outputDir.getName() + "/" + entry.getName());

        file.getParentFile().mkdirs();

        final OutputStream outputStream = Files.newOutputStream(file.toPath());

        IOUtils.copy(input, outputStream);
        outputStream.close();

        entry = input.getNextEntry();
      }

      input.close();
      compressionStream.close();
      bufferedStream.close();
      fileStream.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public File[] getFileNames(File archiveName) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getFileNames'");
  }
}
