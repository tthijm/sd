package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.File;
import java.util.*;

public class Extract extends Command {

  private static final String NAME = "extract";

  public Extract() {
    super(NAME);
  }

  public void run(File[] args, HashMap<String, String> options) {
    if (args.length == 0) {
      System.out.println(
        "Invalid number of arguments. Please provide a file to be extracted and optionally a destination."
      );
      return;
    }

    if (!args[0].exists()) {
      System.out.println("The given archive does not exist. Please provide an existing archive.");
      return;
    }

    final boolean hasPassword = Encryption.isEncrypted(args[0]);
    Format fmt = Format.getInstance(args[0].getName());

    if (fmt == null) {
      System.out.println("Invalid format. Please provide a file with a valid extension.");
      return;
    }

    File outputDir = args.length > 1 ? args[1] : new File(args[0].getName().replace(fmt.getFileExtension(), ""));

    if (hasPassword) {
      final String password = promptPassword(args[0]);

      if (password == null) {
        System.out.println(ABORT_MESSAGE);
        return;
      }

      Encryption.decrypt(args[0], password);
      fmt.decompress(args[0], outputDir);
      Encryption.encrypt(args[0], password);
    } else {
      fmt.decompress(args[0], outputDir);
    }
  }
}
