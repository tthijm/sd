package archiver.command;

import java.io.File;
import java.util.HashMap;

public class Analyse extends Command {

  private static final String NAME = "analyse";

  public Analyse() {
    super(NAME);
  }

  public void run(final File[] arguments, final HashMap<String, String> options) {}
}
