import java.util.Scanner; // Import Scanner for reading user input

public class Intro {
    @SuppressWarnings({ "unused", "resource" })
    public static void intro() {
        StringBuilder intro = new StringBuilder();

        intro.append("\n------------------------------------------------------------------------------------\n");
        intro.append("You wake up finding yourself trapped in a strange, abandoned-looking house all alone. The doors and windows are locked.");
        intro.append("\nYour objective is to try to escape.");
        intro.append("\nHere are some commands to know:");
        intro.append("\n------------------------------------------------------------------------------------");
        intro.append("\nMove rooms: 'go (north, west, east, or south)'\n");
        intro.append("\nLook: 'look (object)'\n");
        intro.append("\nTake: 'take (object)'\n");
        intro.append("\nUse: 'use (object)'\n");
        intro.append("------------------------------------------------------------------------------------\n");

        System.out.println(intro.toString());

        Scanner s = new Scanner(System.in); // Create Scanner to read input
        System.out.print("Type any key to begin: ");
        String input = s.nextLine(); // Read entire line of input        

    }
}
