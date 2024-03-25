package archiver.command;

import archiver.encryption.Encryption;
import java.io.*;
import java.util.*;

public abstract class Command {

  private static final int PASSWORD_ATTEMPTS = 3;
  private static final String PASSWORD_PROMPT = "Password: ";
  private static final String INCORRECT_PROMPT = "Incorrect, try again.";
  protected static final String ABORT_MESSAGE = "Incorrect, aborting.";
  private final String name;
  private static final Command[] COMMANDS = { new Create(), new Extract(), new List() };

  protected Command(final String name) {
    this.name = name;
  }

  public static Command getInstance(final String x) {
    for (final Command command : COMMANDS) {
      if (command.name.equalsIgnoreCase(x)) {
        return command;
      }
    }

    return null;
  }

  protected String promptPassword(final File archive) {
    @SuppressWarnings("resource")
    final Scanner scanner = new Scanner(System.in);

    for (int i = 0; i < PASSWORD_ATTEMPTS; i++) {
      System.out.print(PASSWORD_PROMPT);

      final String line = scanner.nextLine();

      if (Encryption.isPassword(archive, line)) {
        return line;
      }

      System.out.println(INCORRECT_PROMPT);
    }

    return null;
  }

  public abstract void run(File[] arguments, HashMap<String, String> options);
}
