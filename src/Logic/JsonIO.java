package Logic;

import com.google.gson.Gson;

import java.io.*;
import java.util.stream.Collectors;

public class JsonIO {

    public static String readStringFromFile(String fileName) throws IOException{
        String inStr;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            inStr = reader.lines().map(String::trim).collect(Collectors.joining());
        }
        return inStr;
    }

    public static void writeStringToFile(String str, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(str);
        }
    }

}
