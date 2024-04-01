package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.File;
import java.util.*;

public class Extract extends Command {

  private static final String NAME = "extract";

  protected Extract() {
    super(NAME);
  }

  @Override
  public void execute(File[] arguments, HashMap<String, String> options) {
    if (arguments.length == 0) {
      System.out.println(
        "Invalid number of arguments. Please provide a file to be extracted and optionally a destination."
      );
      return;
    }

    if (!arguments[0].exists()) {
      System.out.println("The given archive does not exist. Please provide an existing archive.");
      return;
    }

    Format fmt = Format.getInstance(arguments[0].getName());
    File outputDir = arguments.length > 1
      ? arguments[1]
      : new File(arguments[0].getName().replace(fmt.getFileExtension(), ""));

    if (fmt == null) {
      System.out.println("Invalid format. Please provide a file with a valid extension.");
      return;
    }

    if (outputDir.exists()) {
      System.out.println("The given destination already exists.");
      return;
    }

    if (Encryption.isEncrypted(arguments[0])) {
      final String password = promptPassword(arguments[0]);

      if (password == null) {
        System.out.println(ABORT_MESSAGE);
        return;
      }

      Encryption.decrypt(arguments[0], password);
      fmt.decompress(arguments[0], outputDir);
      Encryption.encrypt(arguments[0], password);
    } else {
      fmt.decompress(arguments[0], outputDir);
    }
  }
}
