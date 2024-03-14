package archiver;

import java.io.*;
import java.util.*;

class Create extends Command {

  @Override
  void run(File[] arguments, HashMap<String, String> options) {
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
    compressionFormat.compress(arguments[0], arguments, configurations);
  }
}
