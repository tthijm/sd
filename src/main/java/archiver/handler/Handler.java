package archiver.handler;

import archiver.command.*;
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
    Scanner inputStream = new Scanner(System.in);
    String input;
    Triplet<String, File[], HashMap<String, String>> parsedLine;
    File[] argumentsArray;
    HashMap<String, String> optionsMap;

    do {
      System.out.println("type next command:");

      input = inputStream.nextLine(); //"create has.zip testFolder";
      parsedLine = parse(input);
      argumentsArray = parsedLine.getValue1();

      optionsMap = parsedLine.getValue2();

      if (parsedLine.getValue0().equals("create")) {
        Command createCommand = new Create();
        createCommand.run(argumentsArray, optionsMap);
      } else if (parsedLine.getValue0().equals("extract")) {
        Command extractCommand = new Extract();
        extractCommand.run(argumentsArray, optionsMap);
      } else if (parsedLine.getValue0().equals("quit")) {
        System.out.println("thank you for using our file archiver, quitting program");
        return;
      } else {
        System.out.println("command not found");
      }
      inputStream.close();
    } while (true);
  }
}
