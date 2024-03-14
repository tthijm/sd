package archiver.handler;

import archiver.command.*;
import archiver.command.create.*;
import java.io.File;
import java.util.*;
import org.javatuples.*;

public class Handler {

  private Triplet<String, File[], HashMap<String, String>> parse(String line) {
    String[] splitted = line.split(" ");
    String commandString = splitted[0];

    String[] arg = Arrays.copyOfRange(splitted, 1, splitted.length); //- to modify after making zip

    File[] arguments = toFileArray(arg);

    HashMap<String, String> options = new HashMap<>();
    return new Triplet<>(commandString, arguments, options);
  }

  private File[] toFileArray(String[] fileStrings) {
    File[] files = new File[fileStrings.length];
    for (int i = 0; i < fileStrings.length; i++) {
      File file = new File(fileStrings[i]);
      files[i] = file;
    }
    return files;
  }

  public void loop() {
    //ask input from the user
    String userInput = "create has.zip file2.txt";

    Triplet<String, File[], HashMap<String, String>> parsedLine = parse(userInput);
    File[] argumentsArray = parsedLine.getValue1();

    HashMap<String, String> optionsMap = parsedLine.getValue2();

    if (parsedLine.getValue0().equals("create")) {
      Command createCommand = new Create();
      createCommand.run(argumentsArray, optionsMap);
    }
  }
}
