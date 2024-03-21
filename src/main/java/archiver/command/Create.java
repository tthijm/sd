package archiver.command;

import archiver.config.Config;
import archiver.encryption.Encryption;
import archiver.format.Format;
import archiver.format.Tar;
import archiver.format.Zip;
import java.io.*;
import java.util.*;

public class Create extends Command {

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

    Format compressionFormat;
    String formatString = ".zip";
    if (!options.containsKey("f")) {
      compressionFormat = new Zip();
    } else {
      String format = options.get("f");
      switch (format) {
        case "tar":
          compressionFormat = new Tar();
          formatString = ".tar";
          break;
        default:
          System.out.println("Invalid format");
          return;
      }
    }

    File checkFolder = new File(arguments[0] + formatString);

    if (checkFolder.exists()) {
      System.out.println(checkFolder.getName() + " already exists.");
      return;
    }

    Config configurations = new Config();

    File[] filesToCompress = new File[arguments.length - 1];
    for (int i = 0; i < filesToCompress.length; i++) {
      filesToCompress[i] = arguments[i + 1];
    }
    compressionFormat.compress(arguments[0], filesToCompress, configurations);

    if (options.containsKey("p")) {
      final Encryption encryption = new Encryption();
      final String password = options.get("p");

      encryption.encrypt(arguments[0], password);
    }
  }
}
