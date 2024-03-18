package archiver.command;

import archiver.config.Config;
import archiver.format.Format;
import archiver.format.Zip;
import java.io.*;
import java.util.*;

public class Create extends Command {

  @Override
  public void run(File[] arguments, HashMap<String, String> options) {
    if (arguments.length <= 1 || arguments[0].exists()) {
      System.out.println("Invalid input");
    }

    for (int i = 1; i < arguments.length; i++) {
      if (!arguments[i].exists()) {
        System.out.println("Invalid file" + arguments[i].getName());
        return;
      }
    }

    Format compressionFormat;
    if (!options.containsKey("-f")) {
      compressionFormat = new Zip();
    } else {
      String format = options.get("-f");
      switch (format) {
        case "tar":
          compressionFormat = new Zip();
          break;
        case "bzip2":
          compressionFormat = new Zip();
          break;
        default:
          compressionFormat = new Zip();
          break;
      }
    }
    Config configurations = new Config();

    File[] filesToCompress = new File[arguments.length - 1];
    for (int i = 0; i < filesToCompress.length; i++) {
      filesToCompress[i] = arguments[i + 1];
    }
    compressionFormat.compress(arguments[0], filesToCompress, configurations);
  }
}
