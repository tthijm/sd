package archiver.format;

import archiver.config.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;
import java.nio.file.Files;
import java.util.ArrayList;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;

public class Tar extends Format {

  private static final String NAME = "tar";
  private static final String FILE_EXTENSION = ".tar.bz2";

  protected Tar() {
    super(NAME, FILE_EXTENSION);
  }

  private TarArchiveInputStream getInputStream(final File archive) {
    try {
      final InputStream fileStream = Files.newInputStream(archive.toPath());
      final BufferedInputStream bufferedStream = new BufferedInputStream(fileStream);
      final BZip2CompressorInputStream compressionStream = new BZip2CompressorInputStream(bufferedStream);
      final TarArchiveInputStream input = new TarArchiveInputStream(compressionStream);

      return input;
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  private TarArchiveOutputStream getOuputStream(final File archive) {
    try {
      final OutputStream fileStream = Files.newOutputStream(archive.toPath());
      final BZip2CompressorOutputStream compressionStream = new BZip2CompressorOutputStream(fileStream);
      final TarArchiveOutputStream output = new TarArchiveOutputStream(compressionStream);

      return output;
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
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
  public void compress(final File archiveName, final File[] fileNames, final Level config) {
    try {
      final TarArchiveOutputStream output = getOuputStream(archiveName);

      for (final File file : fileNames) {
        addToArchive(file, output);
      }

      output.finish();
      output.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void decompress(final File archiveName, final File outputDir) {
    try {
      final TarArchiveInputStream input = getInputStream(archiveName);

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
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public File[] getFiles(File archiveName) {
    try {
      final TarArchiveInputStream input = getInputStream(archiveName);
      ArrayList<File> result = new ArrayList<>();
      TarArchiveEntry entry = input.getNextEntry();

      while (entry != null) {
        result.add(new File(entry.getName()));

        entry = input.getNextEntry();
      }

      input.close();

      return result.toArray(new File[0]);
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
