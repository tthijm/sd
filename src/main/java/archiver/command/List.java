package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.File;
import java.util.*;

public class List extends Command {

  private static final String NAME = "list";
  private static final int PASSWORD_ATTEMPTS = 3;
  private static final String PASSWORD_PROMPT = "Password: ";
  private static final String INCORRECT_PROMPT = "Incorrect, try again.";
  private static final String ABORT_MESSAGE = "Incorrect, aborting.";

  public List() {
    super(NAME);
  }

  private String promptPassword(final File archive) {
    @SuppressWarnings("resource")
    final Scanner scanner = new Scanner(System.in);
    int attempt = 1;

    while (true) {
      System.out.print(PASSWORD_PROMPT);

      final String line = scanner.nextLine();

      if (Encryption.isPassword(archive, line)) {
        return line;
      }

      if (attempt == PASSWORD_ATTEMPTS) {
        break;
      }

      System.out.println(INCORRECT_PROMPT);

      attempt += 1;
    }

    return null;
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

      String[] fileNames = fmt.getFileNames(args[0]);
      System.out.println(Arrays.toString(fileNames));

      Encryption.encrypt(args[0], password);
    } else {
      String[] fileNames = fmt.getFileNames(args[0]);
      System.out.println(Arrays.toString(fileNames));
    }
  }
}
