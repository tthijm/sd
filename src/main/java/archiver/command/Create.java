package archiver.command;

import archiver.config.*;
import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.*;
import java.util.*;

public class Create extends Command {

  private static final String NAME = "create";
  private static final String DEFAULT_FORMAT_NAME = "zip";
  public static final Level DEFAULT_LEVEL = Level.medium;

  protected Create() {
    super(NAME);
  }

  private Level getConfigLevel(String val) {
    try {
      return Level.valueOf(val);
    } catch (final Exception e) {
      return null;
    }
  }

  @Override
  public void execute(File[] arguments, HashMap<String, String> options) {
    Format fmt = Format.getInstance(options.getOrDefault("f", DEFAULT_FORMAT_NAME));
    Level config = getConfigLevel(options.getOrDefault("c", "medium"));

    if (arguments.length <= 1) {
      System.out.println("No files/folders to be archived provided");
      return;
    }

    for (int i = 1; i < arguments.length; i++) {
      if (!arguments[i].exists()) {
        System.out.println(arguments[i].getName() + " does not exist");
        return;
      }
    }

    if (fmt == null) {
      System.out.println("Compression format does not exist.");
      return;
    }

    if (config == null) {
      System.out.println("Invalid compression level.");
      return;
    }

    arguments[0] = new File(arguments[0].getName() + fmt.getFileExtension());

    if (arguments[0].exists()) {
      System.out.println(arguments[0].getName() + " already exists.");
      return;
    }

    fmt.compress(arguments[0], Arrays.copyOfRange(arguments, 1, arguments.length), config);

    if (options.containsKey("p")) {
      Encryption.encrypt(arguments[0], options.get("p"));
    }
  }
}
