package nl.vu.cs.softwaredesign;

import java.util.*;
import java.io.*;
abstract class Command {
    abstract void run(File[] arguments, HashMap<String, String> options);
}
