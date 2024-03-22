package archiver.command;

import java.io.*;
import java.util.*;

public abstract class Command {

  private final String name;
  private static final Command[] COMMANDS = { new Create(), new Extract() };

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

  public String getName() {
    return name;
  }

  public abstract void run(File[] arguments, HashMap<String, String> options);
}
