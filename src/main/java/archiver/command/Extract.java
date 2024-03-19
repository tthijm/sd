package archiver.command;

import archiver.format.Format;
import archiver.format.Zip;
import java.io.File;
import java.util.*;

public class Extract extends Command {

  public void run(File[] args, HashMap<String, String> options) {
    Format fmt = new Zip();
    fmt.decompress(args[0], args[1]);
  }
}
