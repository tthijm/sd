package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import archiver.level.*;
import java.io.*;
import java.util.*;

public class Create extends Command {

  private static final String NAME = "create";
  private static final String DEFAULT_FORMAT_NAME = "zip";
  public static final Level DEFAULT_LEVEL = Level.medium;

  public Create() {
    super(NAME);
  }

  @Override
  public void run(File[] arguments, HashMap<String, String> options) {
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

    Format compressionFormat = Format.getInstance(options.getOrDefault("f", DEFAULT_FORMAT_NAME));

    if (compressionFormat == null) {
      System.out.println("Compression format does not exist.");
      return;
    }

    Level config = getConfigLevel(options.getOrDefault("c", "medium"));
    if (config == null) {
      System.out.println("Invalid compression level.");
      return;
    }

    arguments[0] = new File(arguments[0].getName() + compressionFormat.getFileExtension());

    if (arguments[0].exists()) {
      System.out.println(arguments[0].getName() + " already exists.");
      return;
    }

    File[] filesToCompress = new File[arguments.length - 1];
    for (int i = 0; i < filesToCompress.length; i++) {
      filesToCompress[i] = arguments[i + 1];
    }
    compressionFormat.compress(arguments[0], filesToCompress, config);

    if (options.containsKey("p")) {
      final String password = options.get("p");

      Encryption.encrypt(arguments[0], password);
    }
  }

  private Level getConfigLevel(String val) {
    if (val.equals("none") || val.equals("low") || val.equals("medium") || val.equals("high")) {
      return Level.valueOf(val);
    }
    return null;
  }
}
