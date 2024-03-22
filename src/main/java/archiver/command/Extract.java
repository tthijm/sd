package archiver.command;

import archiver.format.Format;
import java.io.File;
import java.util.*;

public class Extract extends Command {

  private static final int PASSWORD_ATTEMPTS = 3;
  private static final String PASSWORD_PROMPT = "Password: ";
  private static final String INCORRECT_PROMPT = "Incorrect, try again.";
  private static final String ABORT_MESSAGE = "Incorrect, aborting.";

  private String promptPassword(final File archive) {
    @SuppressWarnings("resource")
    final Scanner scanner = new Scanner(System.in);
    int attempt = 1;

    while (true) {
      System.out.print(PASSWORD_PROMPT);

      final String line = scanner.nextLine();

      if (encryption.isPassword(archive, line)) {
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
    final boolean hasPassword = encryption.isEncrypted(args[0]);
    Format fmt = Format.getInstance(args[0].getName());

    if (hasPassword) {
      final String password = promptPassword(args[0]);

      if (password == null) {
        System.out.println(ABORT_MESSAGE);
        return;
      }

      encryption.decrypt(args[0], password);
      fmt.decompress(args[0], args[1]);
      encryption.encrypt(args[0], password);
    } else {
      fmt.decompress(args[0], args[1]);
    }
  }
}
