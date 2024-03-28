package archiver.handler;

import archiver.command.*;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  private File[] toFileArray(String[] fileStrings) {
    return Stream.of(fileStrings).map(v -> new File(v)).toArray(File[]::new);
  }

  private Triplet<String, File[], HashMap<String, String>> parse(String line) {
    String[] splitted = getArguments(line);
    File[] arguments = toFileArray(Arrays.copyOfRange(splitted, 1, splitted.length));
    HashMap<String, String> options = getOptions(line);

    return new Triplet<>(splitted[0], arguments, options);
  }

  public void loop() {
    Scanner inputStream = new Scanner(System.in);

    do {
      System.out.println("type next command:");

      String input = inputStream.nextLine();
      Triplet<String, File[], HashMap<String, String>> parsedLine = parse(input);
      File[] argumentsArray = parsedLine.getValue1();
      HashMap<String, String> optionsMap = parsedLine.getValue2();
      final Command command = Command.getInstance(parsedLine.getValue0());

      if (parsedLine.getValue0().equalsIgnoreCase("quit")) {
        System.out.println("thank you for using our file archiver, quitting program");
        break;
      }

      if (command == null) {
        System.out.println("command not found");
        continue;
      }

      command.execute(argumentsArray, optionsMap);
    } while (true);

    inputStream.close();
  }
}
