package archiver.handler;

import archiver.command.*;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.javatuples.*;

public class Handler {

  private static final Handler instance = new Handler();
  private static final String OPTIONS_REGEX = "-([^ ]+) ([^ ]+) ?";
  private static final Pattern PATTERN = Pattern.compile(OPTIONS_REGEX);

  private Handler() {}

  public static Handler getInstance() {
    return instance;
  }

  protected static String[] getArguments(String line) {
    return line.replaceAll(OPTIONS_REGEX, "").split(" ");
  }

  protected static HashMap<String, String> getOptions(String line) {
    final Matcher matcher = PATTERN.matcher(line);
    final Map<String, String> options = matcher.results().collect(Collectors.toMap(v -> v.group(1), v -> v.group(2)));

    return new HashMap<>(options);
  }

  private Triplet<String, File[], HashMap<String, String>> parse(String line) {
    String[] splitted = getArguments(line);
    String commandString = splitted[0];

    String[] arg = Arrays.copyOfRange(splitted, 1, splitted.length); //- to modify after making zip

    File[] arguments = toFileArray(arg);

    HashMap<String, String> options = getOptions(line);
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
      final Command command = Command.getInstance(parsedLine.getValue0());

      if (parsedLine.getValue0().equalsIgnoreCase("quit")) {
        System.out.println("thank you for using our file archiver, quitting program");
        break;
      }

      if (command == null) {
        System.out.println("command not found");
        continue;
      }

      command.run(argumentsArray, optionsMap);
    } while (true);

    inputStream.close();
  }
}
