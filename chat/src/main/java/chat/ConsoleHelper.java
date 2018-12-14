package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.println(message);
    }
    public static String readString(){
        String temp = "";
        while (true) {
            try {
                temp = reader.readLine();
                break;
            } catch (IOException e) {
                System.out.println("An error occurred while trying to enter text. Try again.");
            }
        }
        return temp;
    }
    public static int readInt(){
        int result = 0;
        while (true){
            try{
                String temp = readString();
                result = Integer.parseInt(temp);
                break;
            } catch (NumberFormatException e){
                System.out.println("An error occurred while trying to enter the number. Try again.");
            }
        }
        return result;
    }
}
