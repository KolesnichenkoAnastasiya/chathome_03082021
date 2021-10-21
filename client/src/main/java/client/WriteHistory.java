package client;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WriteHistory {
    private static PrintWriter write;
    public static String file_name(String login){
        StringBuffer file_name = new StringBuffer("history/history_" + login + ".txt");
        return String.valueOf(file_name);
    }
    public static void make_file_history(String login){
         try (FileWriter writer = new FileWriter(String.valueOf(file_name(login)), true)) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void start(String login){
        try{ write = new PrintWriter(new FileOutputStream(file_name(login), true), true);
    } catch (FileNotFoundException e) {
            e.printStackTrace();
    }}
    public static void writeMsg (String msg) {
        write.println(msg);}
    public static void writeLine(String msg) {
        write.println(msg);
    }
    public static String history_last100 (String login) {
        if (!Files.exists(Paths.get(file_name(login)))) {
            return " ";
        }
        StringBuilder sb = new StringBuilder();
        try {
            List<String> historyLines =Files.readAllLines(Paths.get(file_name(login)));
            int startPosition = 0;
            if (historyLines.size() > 100) {
                startPosition = historyLines.size() - 100;
            }
            for (int i = startPosition; i < historyLines.size(); i++) {
                sb.append(historyLines.get(i)).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static void stopWrite() {
        if (write !=null){
       write.close();
       }
    }
}
