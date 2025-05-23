import java.util.Scanner; // Import Scanner for reading user input
public class RoomAdventure { // Main class containing game logic

    // constants
    final private static String DEFAULT_STATUS =
        "\nSorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', 'use', and 'take'."; // Default error message
    
    // class variables
    private static Room currentRoom; // The room the player is currently in
    private static String[] inventory = {null, null, null, null, null}; // Player inventory slots
    private static String status; // Message to display after each action
    private static boolean hasItem; // If an item is in inventory
    private static boolean hasKey = false;
    private static boolean hasHammer = false;
    private static boolean hasCockroach = false;
    private static boolean hasBucket = false;
    private static boolean hasBeer = false;
    private static int drunkenness = 0;

    private static void handleGo(String noun) { // Handles moving between rooms
        String[] exitDirections = currentRoom.getExitDirections(); // Get available directions
        Room[] exitDestinations = currentRoom.getExitDestinations(); // Get rooms in those directions
        status = "\nI don't see that room."; // Default if direction not found

        for (int i = 0; i < exitDirections.length; i++) { // Loop through directions
            if (noun.equals(exitDirections[i])) { // If user direction matches
                currentRoom = exitDestinations[i]; // Change current room
                status = "\nChanged Room"; // Update status
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                }
            }
        }
    }

    private static void handleLook(String noun) { // Handles inspecting items
        String[] items = currentRoom.getItems(); // Visible items in current room
        String[] itemDescriptions = currentRoom.getItemDescriptions(); // Descriptions for each item
        status = "\nI don't see that item."; // Default if item not found
        for (int i = 0; i < items.length; i++) { // Loop through items
            if (noun.equals(items[i])) { // If user-noun matches an item
                status = itemDescriptions[i]; // Set status to item description
            }
        }
    }

    private static void handleTake(String noun) { // Handles picking up items
        String[] grabbables = currentRoom.getGrabbables(); // Items that can be taken
        status = "\nI can't grab that."; // Default if not grabbable
        int numItems = 0;
        for (String item : inventory) {
            if (item != null) numItems++;
        }
        if (drunkenness >= 3 && numItems >= 2) {
            status = "\nYou're too drunk to carry any more.";
            return;
        }

        for (String item : grabbables) { // Loop through grabbable items
            
            if (noun.equals(item)) { // If user-noun matches grabbable
                for (int j = 0; j < inventory.length; j++) { // Find empty inventory slot
                    
                    if (hasBeer == true){
                        status = "\nDrink the one you have first";
                    }

                    if (inventory[j] == null) { // If slot is empty
                        inventory[j] = noun; // Add item to inventory
                        status = String.format("\nAdded %s to inventory", noun); // Update status

                        String[] items = currentRoom.getGrabbables();
                        int removeIndex = -1;
                        for (int k = 0; k < items.length; k++) {
                            if (items[k].equals(noun)) {
                                removeIndex = k;
                                break;
                            }
                            break;
                        }

                        currentRoom.setGrabbables(removeFromArray(grabbables, noun)); // Remove from grabbables
                        currentRoom.setItems(removeFromArray(currentRoom.getItems(), noun)); // Remove from items
                    break; // Exit inventory loop
                    }
                    break;
                }
            }
        }
    }
    //handles 'use' command
    private static void handleUse(String noun) {
        boolean hasKey = false;
        boolean hasHammer = false;
        boolean hasCockroach = false;
        boolean hasItem = false;

        status = "\nYou try to use the " + noun + ", but nothing happens.";
        String[] grabbables = currentRoom.getGrabbables(); // Items that can be taken
        // Check if the item is in the current room's items and get its use
        String[] items = currentRoom.getItems();
        String[] uses = currentRoom.getItemUses();
        for (int i = 0; i < items.length; i++) {
            if (noun.equals(items[i])) {
                if (uses != null && uses[i] != null) {
                    status = "\n" + uses[i];
                }
            }
        }
        for (String item : inventory) { // Loop through inventory
            if (noun.equals(item)) { // If user-noun matches inventory item
                hasItem = true;
                break; // Exit inventory loop
                    }
            else {
                hasItem = false;
            }
            if (!hasItem) {
                for (String grabbable:grabbables) {
                    if (noun.equals(grabbable)) {
                    status = "\nYou don't have that item.";
                    }
                }
            }
        }

        for (String item : inventory) {
            if (item == null) continue;
            if ("key".equals(item)) hasKey = true;
            if ("hammer".equals(item)) hasHammer = true;
            if ("cockroach".equals(item)) hasCockroach = true;
            if (noun.equals(item)) hasItem = true;
        }

        // If using beer
        if (noun.equals("beer")) {
            if (hasBeer) {
                drunkenness++;
                status = "\nYou drink the beer. You feel a little less sober.";
                if (drunkenness >= 3) {
                    status += " You're getting a bit dizzy...";
                }
                // Remove a beer when you drink it
                for (int i = 0; i < inventory.length; i++) {
                    if ("beer".equals(inventory[i])) {
                        inventory[i] = null;
                        break;
                    }
                break;
                }
                return;
            } else {
                status = "\nYou don't have any beer.";
                return;
            }
        }

        // Chance to forget action if drunk
        if (drunkenness > 0) {
            int drunk = 5 * drunkenness;
            int limit = (int)(Math.random() * 100) + 1;
            if (limit < drunk) {
                status = "\nYou got giggly and forgot what you meant to do...";
                return;
            }
        }

        if (currentRoom.toString().contains("Bathroom") && noun.equals("cockroach")) {
            status = "\nYou place the cockroach in the bathtub...\nIt begins to twitch violently.\n\nThe Roach King emerges.\nYou are not worthy.\n\nYOU DIED!";
            System.out.println(status);
            System.exit(0); // Game over
        }

        if (currentRoom.toString().contains("Garage") && noun.equals("car")) {
            if (!hasKey) {
                status = "\nYou try to start the car, but you don't have the key.";
            } else if (!hasHammer) {
                status = "\nThe car starts, but the garage door won't open!";
            } else {
                status = "\nYou smash the garage door open with the hammer and speed away into freedom. You win! ";
                System.out.println(status);
                System.exit(0); // Victory
            }
            return;
        }
    }

    // Remove an item from array by value
    private static String[] removeFromArray(String[] array, String value) {
    java.util.List<String> list = new java.util.ArrayList<>();
    boolean removed = false;
    for (String item : array) {
        if (!removed && item.equals(value)) {
            removed = true; // Skip this occurrence
            continue;
        }
        list.add(item);
    }
    return list.toArray(new String[0]);
}

    // Remove an item from array by index
    private static String[] removeFromArrayByIndex(String[] array, int index) {
    java.util.List<String> list = new java.util.ArrayList<>();
    for (int i = 0; i < array.length; i++) {
        if (i != index) {
            list.add(array[i]);
        }
    }
    return list.toArray(new String[0]);
}

    private static void setupGame() { // Initializes game world
        
        Room room1 = new Room("Bedroom"); // Create Room 1
        Room room2 = new Room("Bathroom"); // Create Room 2
        Room room3 = new Room("Living Room"); // Create Room 3
        Room room4 = new Room("Kitchen"); // Create Room 4
        Room room5 = new Room("Garage"); // Create Room 5

        // ROOM 1 (BEDROOM)
        String[] room1ExitDirections = {"west", "south"}; // Room 1 exits
        Room[] room1ExitDestinations = {room2, room4}; // Destination rooms for Room 1
        String[] room1Items = {"chair", "desk"}; // Items in Room 1
        String[] room1ItemDescriptions = { // Descriptions for Room 1 items
            "\nIt is a chair",
            "\nIt's a desk, there is a drawer."
        };
        String[] room1ItemUses = {
            "You sit on the chair. It's surprisingly comfortable.",
            "You open the desk and find a key."
        };
        String[] room1Grabbables = {"key"}; // Items you can take in Room 1
        room1.setExitDirections(room1ExitDirections); // Set exits
        room1.setExitDestinations(room1ExitDestinations); // Set exit destinations
        room1.setItems(room1Items); // Set visible items
        room1.setItemDescriptions(room1ItemDescriptions); // Set item descriptions
        room1.setItemUses(room1ItemUses); // Set item uses
        room1.setGrabbables(room1Grabbables); // Set grabbable items

        // ROOM 2 (BATHROOM)
        String[] room2ExitDirections = {"east", "south"}; // Room 2 exits
        Room[] room2ExitDestinations = {room1, room3}; // Destination rooms for Room 2
        String[] room2Items = {"bathtub", "toilet"}; // Items in Room 2
        String[] room2ItemDescriptions = { // Descriptions for Room 2 items
            "\nThere is a cockroach in the bathtub.",
            "\nJust a gross, clogged toilet."
        };
        String[] room2Grabbables = {"cockroach"}; // Items you can take in Room 2
        String[] room2ItemUses = {
            "You try to use the bathtub. It's full of cockroaches!",
            "You try to flush the toilet. It makes a horrible noise."
        };
        room2.setExitDirections(room2ExitDirections); // Set exits
        room2.setExitDestinations(room2ExitDestinations); // Set exit destinations
        room2.setItems(room2Items); // Set visible items
        room2.setItemDescriptions(room2ItemDescriptions); // Set item descriptions
        room2.setGrabbables(room2Grabbables); // Set grabbable items
        room2.setItemUses(room2ItemUses);

        //ROOM 3 (LIVING ROOM)
        String[] room3ExitDirections = {"north", "west", "south"}; // Room 3 exits
        Room[] room3ExitDestinations = {room2, room4, room5}; // Destination rooms for Room 3
        String[] room3Items = {"couch", "tv"}; // Items in Room 3
        String[] room3ItemDescriptions = { // Descriptions for Room 3 items
            "\nIt is a couch",
            "\nIt's a tv, but there is no signal."
        };
        String[] room3Grabbables = {"bucket"}; // Items you can take in Room 3
        String[] room3ItemUses = {
            "You sit on the couch. It's comfy, but you find nothing.",
            "You turn on the TV. Still no signal."
        };
        room3.setExitDirections(room3ExitDirections); // Set exits
        room3.setExitDestinations(room3ExitDestinations); // Set exit destinations
        room3.setItems(room3Items); // Set visible items
        room3.setItemDescriptions(room3ItemDescriptions); // Set item descriptions
        room3.setItemUses(room3ItemUses);
        room3.setGrabbables(room3Grabbables); // Set grabbable items

        //ROOM 4 (KITCHEN)
        String[] room4ExitDirections = {"north", "east"}; // Room 4 exits
        Room[] room4ExitDestinations = {room1, room3}; // Destination rooms for Room 4
        String[] room4Items = {"fridge", "sink"}; // Items in Room 4
        String[] room4ItemDescriptions = {
            "\nIt's a fridge, but it is filled with beer and moldy food.",
            "\nIt is a full sink",
            "\nA cold beer.",
            "\nA cold beer.",
            "\nA cold beer.",
            "\nA cold beer.",
            "\nA cold beer.",
            "\nA cold beer."
        };
        String[] room4Grabbables = {"beer", "beer", "beer", "beer", "beer", "beer"};
        String[] room4ItemUses = {
            "You open the fridge and see beer and moldy food.",
            "You try to use the sink. The water is cold.",
        };
        room4.setExitDirections(room4ExitDirections); // Set exits
        room4.setExitDestinations(room4ExitDestinations); // Set exit destinations
        room4.setItems(room4Items); // Set visible items
        room4.setItemDescriptions(room4ItemDescriptions); // Set item descriptions
        room4.setItemUses(room4ItemUses);
        room4.setGrabbables(room4Grabbables); // Set grabbable items


        // ROOM 5 (GARAGE)
        String[] room5ExitDirections = {"north"}; // Room 5 exits
        Room[] room5ExitDestinations = {room3}; // Destination rooms for Room 5
        String[] room5Items = {"car", "hammer"}; // Items in Room 5
        String[] room5ItemDescriptions = { // Descriptions for Room 5 items
            "\nIt is a broken-down car.",
            "\nIt's a hammer, where can I use this?"
        };
        String[] room5Grabbables = {"hammer"}; // Items you can take in Room 5
        String[] room5ItemUses = {
            "",
        };

        room5.setExitDirections(room5ExitDirections); // Set exits
        room5.setExitDestinations(room5ExitDestinations); // Set exit destinations
        room5.setItems(room5Items); // Set visible items
        room5.setItemDescriptions(room5ItemDescriptions); // Set item descriptions
        room5.setGrabbables(room5Grabbables); // Set grabbable items
        room5.setItemUses(room5ItemUses);

        currentRoom = room1; // Start game in Room 1

    }
    
    @SuppressWarnings("java:S2189")
    public static void main(String[] args) { // Entry point of the program
        Intro.intro();
        setupGame(); // Initialize rooms, items, and starting room

        while (true) { // Game loop, runs until program is terminated
            System.out.print(currentRoom.toString()); // Display current room description
            System.out.print("Inventory: "); // Prompt for inventory display

            hasKey = false;
            hasHammer = false;
            hasCockroach = false;
            hasBucket = false;
            hasBeer = false;

            for (String item : inventory) {
                if (item == null) continue;
                if ("key".equals(item)) hasKey = true;
                if ("hammer".equals(item)) hasHammer = true;
                if ("cockroach".equals(item)) hasCockroach = true;
                if ("bucket".equals(item)) hasBucket = true;
                if ("beer".equals(item)) hasBeer = true;
            }
        
            for (int i = 0; i < inventory.length; i++) { // Loop through inventory slots
                System.out.print(inventory[i] + " "); // Print each inventory item
            }

            System.out.println("\n\nWhat would you like to do? "); // Prompt user for next action


            @SuppressWarnings("resource")
			Scanner s = new Scanner(System.in); // Create Scanner to read input
            String input = s.nextLine(); // Read entire line of input
            String[] words = input.split(" "); // Split input into words

            if (words.length != 2) { // Check for proper two-word command
                status = DEFAULT_STATUS; // Set status to error message
                continue; // Skip to next loop iteration
            }

            String verb = words[0]; // First word is the action verb
            String noun = words[1]; // Second word is the target noun

            switch (verb) { // Decide which action to take
                case "go": // If verb is 'go'
                    handleGo(noun); // Move to another room
                    break;
                case "look": // If verb is 'look'
                    handleLook(noun); // Describe an item
                    break;
                case "take": // If verb is 'take'
                    handleTake(noun); // Pick up an item
                    break;
                case "use": // If Verb is 'use'
                    handleUse(noun);
                    break;
                default: // If verb is unrecognized
                    status = DEFAULT_STATUS; // Set status to error message
            }

            System.out.println(status); // Print the status message
            try{
                Thread.sleep(2000);
                }catch(InterruptedException e){
            }
            
        }
    }
}
    

class Room { // Represents a game room
    private String name; // Room name
    private String[] exitDirections; // Directions you can go
    private Room[] exitDestinations; // Rooms reached by each direction
    private String[] items; // Items visible in the room
    private String[] itemDescriptions; // Descriptions for those items
    private String[] grabbables; // Items you can take
    private String[] itemUses; // Uses for items

    public Room(String name) { // Constructor
        this.name = name; // Set the room's name
    }

    public void setExitDirections(String[] exitDirections) { // Setter for exits
        this.exitDirections = exitDirections;
    }

    public String[] getExitDirections() { // Getter for exits
        return exitDirections;
    }

    public void setExitDestinations(Room[] exitDestinations) { // Setter for exit destinations
        this.exitDestinations = exitDestinations;
    }

    public Room[] getExitDestinations() { // Getter for exit destinations
        return exitDestinations;
    }

    public void setItems(String[] items) { // Setter for items
        this.items = items;
    }

    public String[] getItems() { // Getter for items
        return items;
    }

    public void setItemDescriptions(String[] itemDescriptions) { // Setter for descriptions
        this.itemDescriptions = itemDescriptions;
    }

    public String[] getItemDescriptions() { // Getter for descriptions
        return itemDescriptions;
    }

    public void setItemUses(String[] itemUses) {
        this.itemUses = itemUses;
    }
    public String[] getItemUses() {
        return itemUses;
    }

    public void setGrabbables(String[] grabbables) { // Setter for grabbable items
        this.grabbables = grabbables;
    }

    public String[] getGrabbables() { // Getter for grabbable items
        return grabbables;
    }

    @Override
    public String toString() { // Custom print for the room
        String result = "\nLocation: " + name; // Show room name
        result += "\nYou See: "; // List items
        for (String item : items) { // Loop items
            result += item + " "; // Append each item , improve grammar/appearnce when printed
        }
        result += "\nExits: "; // List exits
        for (String direction : exitDirections) { // Loop exits
            result += direction + " "; // Append each direction
        }
        return result + "\n"; // Return full description
    }
}
