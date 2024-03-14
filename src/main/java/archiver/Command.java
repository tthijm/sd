package archiver;

import java.io.*;
import java.util.*;

abstract class Command {

  abstract void run(File[] arguments, HashMap<String, String> options);
}
