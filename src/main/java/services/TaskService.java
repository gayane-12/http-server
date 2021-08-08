package services;

import models.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    public static final File RESOURCE_DIR = new File(System.getProperty("user.dir") + "\\src\\main\\resources");
    public static final String TASKS_FILE = "tasks.txt";
    public static File TASKS = new File(RESOURCE_DIR,
            TaskService.TASKS_FILE);

    public static List<Task> convertFromFiles(List<String> strings) {
        List<Task> tasks = new ArrayList<>();
        for (String str :
                strings) {
            String[] splittedString = str.split(",");
            tasks.add(new Task(splittedString[0], splittedString[1], Boolean.parseBoolean(splittedString[2])));
        }
        return tasks;
    }

    public static List<Task> getAll() throws IOException {
        return convertFromFiles(Files.readAllLines(TASKS.toPath()));
    }

    public static List<Task> getById(String id, List<Task> tasks) throws IOException {
        return tasks.stream().filter(task -> task.getId().equals(id)).collect(Collectors.toList());
    }

    public static String printAll(List<Task> tasks) {
        StringBuilder stringBuilder = new StringBuilder();
        tasks.forEach(task -> stringBuilder.append("ID: "
                + task.getId()
                + "\nDescription: "
                + task.getTaskDescription()
                + "\nDone: "
                + task.isDone()
                + "\n\n"));
        return stringBuilder.toString();
    }

}
