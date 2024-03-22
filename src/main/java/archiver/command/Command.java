package archiver.command;

import archiver.encryption.Encryption;
import java.io.*;
import java.util.*;

public abstract class Command {

  final Encryption encryption = new Encryption();

  public abstract void run(File[] arguments, HashMap<String, String> options);
}
