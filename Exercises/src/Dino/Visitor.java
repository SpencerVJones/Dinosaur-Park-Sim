package Dino;

import java.util.Scanner;

public class Visitor {


    // 1. visitorGreeting
    // 2. visitorOptions

    static void visitorGreeting() {

        System.out.println();

        // Ask the user for their name
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, what is your name?");

        // Get user input
        String yourName = scanner.nextLine();

        System.out.println();

        // Check amount of visitors
        ParkManager.amountOfVisitors();

        // Wait for the user to press Enter to continue
        System.out.println("Press Enter to continue...");

        // Get user input from pressing enter
        Scanner scan = new Scanner(System.in);
        scan.nextLine();


        System.out.println("Welcome to Ancient Eden, " + yourName + "!");


        // Wait two seconds before next statement
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("My name is Spencer. I am the Zoo Keeper here.");

        // Wait two seconds before next statement
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Ancient Eden is safe and secure.");

        // Wait two seconds before next statement
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("We maintain a safety rating scale to ensure our standards.");

        // Wait two seconds before next statement
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Our safety rating is currently 10 out of 10.");

        // Wait two seconds before next statement
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("The park opens at 08:00 and closes at 20:00.");

        System.out.println();

        // Wait for the user to press Enter to continue
        System.out.println("Press Enter to continue...");

        // Get user input [enter] to continue
        Scanner sc = new Scanner(System.in);
        sc.nextLine();

        // Take user to the visitor options menu
        visitorOptions();


    }


    static void visitorOptions() {
        System.out.println("What would you like to do?");
        System.out.println("[1]: Meet the staff");
        System.out.println("[2]: Visit a dinosaur");
        System.out.println("[3]: Get facts about a dinosaur species");
        System.out.println("[4]: Get the current date and time");
        System.out.println("[5]: Leave the park");
        Scanner scan = new Scanner(System.in);
        int result = scan.nextInt();

        if(result == 1) {
            System.out.println();
            // Display park staff information
            Staff.addedStaff();
        }
        else if (result == 2) {
            System.out.println();
            // Retrieve a random dinosaur and display information
            DinosaurPark park = new DinosaurPark();
            park.initializeDinosaurs();
            Dinosaur.getRandomDino(park);
        }
        else if (result == 3) {
            System.out.println();
            // Get facts about a dinosaur species
            Dinosaur.speciesFacts();
        }
        else if (result == 4) {
            System.out.println();
            // Get the current date, time, ot date & time
            ParkManager.getDateTime();
        }
        else if (result == 5) {
            System.exit(0);
        }
        else {
            // Error Handling
            System.out.println();

            System.out.println("Please enter a valid response.");

            // Wait two seconds to continue
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Return to visitor options menu
            visitorOptions();
        }
        // Return to visitor options menu
        visitorOptions();
    }

}
