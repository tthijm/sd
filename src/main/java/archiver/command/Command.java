package archiver.command;

import archiver.format.Format;
import java.io.*;
import java.util.*;

public abstract class Command {

  public abstract void run(File[] arguments, HashMap<String, String> options);
}
