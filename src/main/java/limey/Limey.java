package limey;

import limey.command.Deadline;
import limey.command.Event;
import limey.command.Task;
import limey.command.Todo;
import limey.exception.commandNotFoundException;
import limey.exception.invalidDateException;
import limey.iohandler.FileHandler;
import limey.iohandler.Parser;
import limey.iohandler.Speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Limey {
    public static final int MAX_NUM_TASKS = 100;

    public static void main(String[] args) {
        //initialise variables
        Task[] tasks = new Task[MAX_NUM_TASKS];
        String filePath = "C:\\Users\\sunil\\desktop\\NUS\\CS2113\\Indiv Project\\src\\main\\SavedList";
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory()){
            retrieveSavedList(tasks);
        }
        String inLine;
        String firstWord;
        String[] wordList;
        //start user interface
        Speech.sayHi();
        Scanner in = new Scanner(System.in);
        inLine = in.nextLine().trim();
        wordList = Parser.splitInput(inLine);
        firstWord = Parser.getFirstWord(inLine);

        initialiseLimey(firstWord, tasks, wordList, inLine, in);

        exitLimey(tasks);
    }

    private static void retrieveSavedList(Task[] tasks) {
        try {
            String filePath = "C:\\Users\\sunil\\desktop\\NUS\\CS2113\\Indiv Project\\src\\main\\SavedList";
            FileHandler.readFileToTasks(filePath, tasks);
        } catch (FileNotFoundException e) {
            Speech.invalidMessage("File not found");
        }
    }

    private static void exitLimey(Task[] tasks) {
        try {
            String filePath = "C:\\Users\\sunil\\desktop\\NUS\\CS2113\\Indiv Project\\src\\main\\SavedList";
            FileHandler.writeToFile(filePath, tasks);
        }catch (IOException e) {
            Speech.invalidMessage("File not found");
        }
        Speech.sayBye();
    }


    private static void makeNewTask (Task[] tasks, String inLine, String firstWord) throws commandNotFoundException {
        Task taskIn;
        inLine = inLine.substring(inLine.indexOf(" ") +1);
        switch (firstWord) {
        case "deadline":
            try {
                taskIn = new Deadline(inLine);
            } catch (invalidDateException | StringIndexOutOfBoundsException e){
                Speech.invalidMessage("Invalid deadline date");
                return;
            }
            break;
        case "event":
            try {
                taskIn = new Event(inLine);
            } catch (invalidDateException | StringIndexOutOfBoundsException e){
                Speech.invalidMessage("Invalid event date");
                return;
            }
            break;
        case "todo": // currently default create a todo object
            taskIn = new Todo(inLine);
            break;
        default:
            throw new commandNotFoundException();
        }
        tasks[Task.numTasks] = taskIn;
        Task.numTasks++;
        Speech.printAdded(taskIn, Task.numTasks);
    }

    private static void initialiseLimey(String firstWord, Task[] tasks, String[] wordList, String inLine,  Scanner in) {
        while (!firstWord.equals("bye")) { //loop until input 'bye'
            switch (firstWord) { //switch case to decide what to do
            case "list":
                Speech.printTaskList(tasks, Task.numTasks);
                break;
            case "mark":
                printMarkTask(tasks, wordList);
                break;
            case "unmark":
                printUnmarkTask(tasks, wordList);
                break;
            default:
                try{
                    makeNewTask(tasks, inLine.trim(), firstWord);
                } catch (commandNotFoundException e){
                    Speech.invalidMessage("Invalid Command");
                } catch (StringIndexOutOfBoundsException e){
                    Speech.invalidMessage("String Index out of bounds");
                }
                break;
            }
            inLine = in.nextLine().trim();
            wordList = Parser.splitInput(inLine);
            firstWord = wordList[0];
        }
    }

    private static void printUnmarkTask(Task[] tasks, String[] wordList) {
        try {
            unmarkTask(tasks, wordList);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            Speech.invalidMessage("Index out of bounds.");
        } catch (NumberFormatException e) {
            Speech.invalidMessage("Index given is not a number.");
        }
    }

    private static void unmarkTask(Task[] tasks, String[] wordList) {
        String inLine;
        int taskIndex;
        inLine = wordList[1];
        taskIndex = Integer.parseInt(inLine) - 1;
        tasks[taskIndex].setDone(false);
        Speech.printUnmarked(tasks[taskIndex]);
    }

    private static void printMarkTask(Task[] tasks, String[] wordList) {
        try {
            markTask(tasks, wordList);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            Speech.invalidMessage("Index out of bounds.");
        } catch (NumberFormatException e) {
            Speech.invalidMessage("Index given is not a number.");
        }
    }

    private static void markTask(Task[] tasks, String[] wordList) {
        int taskIndex;
        String inLine;
        inLine = wordList[1];
        taskIndex = Integer.parseInt(inLine) - 1;
        tasks[taskIndex].setDone(true);
        Speech.printMarked(tasks[taskIndex]);
    }
}

