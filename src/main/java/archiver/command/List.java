package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.File;
import java.util.*;

public class List extends Command {

  private static final String NAME = "list";

  public List() {
    super(NAME);
  }

  public void run(File[] args, HashMap<String, String> options) {
    if (args.length == 0) {
      System.out.println("No archive to be listed provided");
      return;
    } else if (args.length > 1) {
      System.out.println("Invalid input, too many arguments");
      return;
    } else if (!args[0].exists()) {
      System.out.println(args[0].getName() + " does not exist");
      return;
    }

    // TODO: input validation for providing options
    //        if (options == null) {
    //            System.out.println("Invalid input, no options available");
    //            return;
    //        }

    //TODO: checking for if the format existst in the format package

    final boolean hasPassword = Encryption.isEncrypted(args[0]);
    Format fmt = Format.getInstance(args[0].getName());

    if (hasPassword) {
      final String password = promptPassword(args[0]);

      if (password == null) {
        System.out.println(ABORT_MESSAGE);
        return;
      }

      Encryption.decrypt(args[0], password);

      File[] files = fmt.getFiles(args[0]);

      System.out.println("The following files can be found in the archive:");
      for (int i = 0; i < files.length; i++) {
        System.out.println((i + 1) + ". " + files[i].getName());
      }

      Encryption.encrypt(args[0], password);
    } else {
      File[] files = fmt.getFiles(args[0]);

      for (int i = 0; i < files.length; i++) {
        System.out.println((i + 1) + ". " + files[i].getName());
      }
    }
  }
}
