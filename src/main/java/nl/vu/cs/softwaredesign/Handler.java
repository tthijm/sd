package nl.vu.cs.softwaredesign;

import java.io.File;
import java.util.*;
import org.javatuples.*;
public class Handler {
    private Triplet<String, File[], HashMap<String, String>> parse(String line){
        String[] parsedLine = line.split(" ");
        String commandString = parsedLine[0];

        int i = 1;
        int numberArguments = 0;
        while(i < parsedLine.length && (!Objects.equals(parsedLine[i], "-c") && !Objects.equals(parsedLine[i], "-p") && !Objects.equals(parsedLine[i], "-f"))){
            numberArguments++;
            i++;
        }

        File[] arguments = new File[numberArguments];
        i = 1;
        while(i < parsedLine.length && (!Objects.equals(parsedLine[i], "-c") && !Objects.equals(parsedLine[i], "-p") && !Objects.equals(parsedLine[i], "-f"))){
            File addedFile = new File(parsedLine[i]);
            arguments[i-1] = addedFile;
            i++;
        }

        HashMap<String, String> options = new HashMap<>();
        return new Triplet<>(commandString, arguments, options);
    }
    void loop(){
        //ask input from the user
        String userInput = "create has.zip file1.png file2.png -c speed";

        Triplet<String, File[], HashMap<String, String>> parsedLine = parse(userInput);
        File[] argumentsArray = parsedLine.getValue1();
        HashMap<String, String> optionsMap = parsedLine.getValue2();

        if(parsedLine.getValue0().equals("create")){
            Create createCommand = new Create();
            createCommand.run(argumentsArray, optionsMap);
        }
    }
    Handler(){
    }
}
