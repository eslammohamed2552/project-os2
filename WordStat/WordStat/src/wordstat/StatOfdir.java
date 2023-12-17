package wordstat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

public class StatOfdir {

    private final Map<String, Integer> map;
    String longestWord;
    String shortestWord;
    JTextArea jTextArea;

    public StatOfdir(JTextArea jText) {
        map = new HashMap<>();
        shortestWord = "";
        longestWord = "";
        jTextArea = jText;
    }

    public void calcStatOfDir(Path path, boolean readSubDir) {
        try {
            if (Files.exists(path) && Files.isDirectory(path)) {
                File folder = path.toFile();
                var files = folder.listFiles();
                var stack = new Stack<File>();

                for (var file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        CompletableFuture.supplyAsync(() -> {
                            return readDataOfFile(file.toPath());
                        }).thenAcceptAsync(result -> {
                            jTextArea.append(file.getName() + ":" + "\n");
                            jTextArea.append("  Number Of Words : " + result + "\n");
                            jTextArea.append("  Longest Of Words : " + longestWord + "\n");
                            jTextArea.append("  Shortest Of Words : " + shortestWord + "\n");
                            jTextArea.append("  Number Of is : " + map.computeIfAbsent("is", k -> 0) + "\n");
                            jTextArea.append("  Number Of are : " + map.computeIfAbsent("are", k -> 0) + "\n");
                            jTextArea.append("  Number Of you : " + map.computeIfAbsent("you", k -> 0) + "\n");
                        });
                    } else if (file.isDirectory()) {
                        stack.push(file);
                    }
                }

                if (readSubDir) {
                    while (!stack.isEmpty()) {
                        var fileData = stack.pop();
                        var listOfFiles = fileData.listFiles();
                        for (var file : listOfFiles) {
                            if (file.isFile() && file.getName().endsWith(".txt")) {
                                CompletableFuture.supplyAsync(() -> {
                                    return readDataOfFile(file.toPath());
                                }).thenAcceptAsync(result -> {
                                    jTextArea.append(file.getName() + ":" + "\n");
                                    jTextArea.append("  Number Of Words : " + result + "\n");
                                    jTextArea.append("  Longest Of Words : " + longestWord + "\n");
                                    jTextArea.append("  Shortest Of Words : " + shortestWord + "\n");
                                    jTextArea.append("  Number Of is : " + map.computeIfAbsent("is", k -> 0) + "\n");
                                    jTextArea.append("  Number Of are : " + map.computeIfAbsent("are", k -> 0) + "\n");
                                    jTextArea.append("  Number Of you : " + map.computeIfAbsent("you", k -> 0) + "\n");
                                });
                            } else if (file.isDirectory()) {
                                stack.push(file);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    public Integer readDataOfFile(Path path) {
        try {
            var list = Files.readAllLines(path);
            var count = 0;
            for (var line : list) {
                for (var word : line.toLowerCase().split(" ")) {
                    word = word.trim();
                    if (word.length() > 0) {
                        Integer c = map.containsKey(word) ? map.get(word) : 0;
                        map.put(word, c + 1);
                        count++;

                        if (longestWord.length() < word.length()) {
                            longestWord = word;
                        }

                        if (shortestWord.isEmpty() || shortestWord.length() > word.length()) {
                            shortestWord = word;
                        }
                    }
                }
            }
            return count;
        } catch (IOException ex) {
            Logger.getLogger(StatOfdir.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public void clear() {
        map.clear();
        longestWord = "";
        shortestWord = "";
        jTextArea.setText("");
    }
}
