package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.File;
import java.util.*;

public class List extends Command {

  private static final String NAME = "list";

  protected List() {
    super(NAME);
  }

  @Override
  public void execute(File[] arguments, HashMap<String, String> options) {
    if (arguments.length == 0) {
      System.out.println("No archive to be listed provided");
      return;
    }

    if (arguments.length > 1) {
      System.out.println("Invalid input, too many arguments");
      return;
    }

    if (!arguments[0].exists()) {
      System.out.println(arguments[0].getName() + " does not exist");
      return;
    }

    Format fmt = Format.getInstance(arguments[0].getName());
    File[] files;

    if (fmt == null) {
      System.out.println("Invalid format. Please provide a file with a valid extension.");
      return;
    }

    if (Encryption.isEncrypted(arguments[0])) {
      final String password = promptPassword(arguments[0]);

      if (password == null) {
        System.out.println(ABORT_MESSAGE);
        return;
      }

      Encryption.decrypt(arguments[0], password);

      files = fmt.getFiles(arguments[0]);

      Encryption.encrypt(arguments[0], password);
    } else {
      files = fmt.getFiles(arguments[0]);
    }

    System.out.println("list of files in " + arguments[0].getName() + ":");

    for (int i = 0; i < files.length; i++) {
      System.out.println((i + 1) + ". " + files[i].getPath());
    }
  }
}
