package archiver.command;

import archiver.format.Format;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Analyse extends Command {

  private static final String NAME = "analyse";
  private static final String NOT_FOUND_FORMAT = "%s does not exist\n";
  private static final String ROW_FORMAT = "%-10s %-10s %-10s %-10s\n";
  private static final String RECOMMEND_FORMAT = "\nBased on size reduction, it is recommended to use %s.\n";
  private static final Object[] LABELS = { "name", "size", "reduction", "duration" };
  private static final String HEADER = String.format(ROW_FORMAT, LABELS);
  private static final Format[] FORMATS = Format.getFormats();

  protected Analyse() {
    super(NAME);
  }

  private File getTempFile() {
    try {
      return File.createTempFile(NAME, null, Paths.get("").toAbsolutePath().toFile());
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  private long getTotalSize(final File[] files) {
    return Stream.of(files).mapToLong(file -> file.isFile() ? file.length() : FileUtils.sizeOfDirectory(file)).sum();
  }

  @Override
  public void execute(final File[] arguments, final HashMap<String, String> options) {
    for (final File argument : arguments) {
      if (argument.exists() == false) {
        System.out.printf(NOT_FOUND_FORMAT, argument.getName());
        return;
      }
    }

    final File tempFile = getTempFile();
    final long totalSize = getTotalSize(arguments);

    Pair<Long, String> best = Pair.of(Long.MAX_VALUE, null);

    System.out.print(HEADER);

    for (final Format format : FORMATS) {
      final String name = format.getName();
      final Instant before = Instant.now();

      format.compress(tempFile, arguments, Create.DEFAULT_LEVEL);

      final Instant after = Instant.now();
      final long size = tempFile.length();
      final double reduction = ((double) (totalSize - size) / totalSize) * 100;
      final double duration = Duration.between(before, after).toMillis() / 1000d;

      if (size < best.getLeft()) {
        best = Pair.of(size, name);
      }

      System.out.printf(
        ROW_FORMAT,
        name,
        size,
        totalSize == 0 ? "x" : String.format("%.2f%%", reduction),
        String.format("%.3f", duration)
      );
    }

    tempFile.delete();
    System.out.printf(RECOMMEND_FORMAT, best.getRight());
  }
}
