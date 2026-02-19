package dinosaur.park;

import java.util.Scanner;

public class LegacyStaffConsole {
    private String name;
    private String position;
    private int age;
    private static int yearsOfExperience;

    public LegacyStaffConsole(String name, int age, String position, int yearsOfExperience) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.yearsOfExperience = yearsOfExperience;
    }



    static void addedStaff() {
        // Adding staff members
        LegacyStaffConsole zookeeper = new LegacyStaffConsole("Spencer", 24, "zookeeper", 10);
        LegacyStaffConsole veterinarian = new LegacyStaffConsole("Brooke", 22, "veterinarian", 8);
        LegacyStaffConsole dinoCaretaker = new LegacyStaffConsole("Landon", 26, "dinosaur caretaker", 6);
        LegacyStaffConsole tourGuide = new LegacyStaffConsole("Jessie", 20, "tour guide", 3);
        LegacyStaffConsole guestServiceAgent = new LegacyStaffConsole("Chloe", 19, "guest service agent", 2);
        LegacyStaffConsole intern = new LegacyStaffConsole("Kyle", 18, "intern", 1);


        // Display zookeeper information
        zookeeper.displayStaffInfo();

        // Wait two seconds before displaying next staff
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Display veterinarian information
        veterinarian.displayStaffInfo();

        // Wait two seconds before displaying next staff
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Display dinosaur caretaker information
        dinoCaretaker.displayStaffInfo();

        // Wait two seconds before displaying next staff
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Display tour guide information
        tourGuide.displayStaffInfo();

        // Wait two seconds before displaying next staff
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Display guest service agent information
        guestServiceAgent.displayStaffInfo();

        // Wait two seconds before displaying next staff
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Display intern information
        intern.displayStaffInfo();

        // Wait two seconds before displaying next staff
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        System.out.println();

        // Wait for the user to press Enter to continue
        System.out.println("Press Enter to continue...");
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

    }


    public void displayStaffInfo() {
        // Display the information of the staff members
        System.out.println("This is " + name + ", they are " + age +
                ", they are an " + position + ", and they have worked here for " +
                yearsOfExperience + " years.");
    }


    // 1. staffGreeting
    // 2. staffOptions
    // 3. displayStaffInfo
    // 4. dinosByExperience
    // 5. taskByPosition

    public static void staffGreeting() {
        System.out.println();

        // Ask for the users name
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your name?");

        // Get user input
        String response = scanner.nextLine();

        System.out.println();
        // Welcome the user back to work
        System.out.println("Welcome back to work " + response + "!");

        // Wait one second before moving on
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Go forward to the staff options
        staffOptions();
    }

    public static void staffOptions() {

        System.out.println();
        // Staff options of what tools they want to use
        System.out.println("Here are the tools you have available to you to help you throughout your shift.");
        System.out.println("[1]: Average Weight Calculator");
        System.out.println("[2]: Proper Nutrition Calculator");
        System.out.println("[3]: Age Difference Calculator");
        System.out.println("[4]: Check if your dinosaur is a carnivore, herbivore, or omnivore");
        System.out.println("[5]: Check which dinosaurs you are allowed to work with");
        System.out.println("[6]: Check the tasks for your position");
        System.out.println("[7]: Get the current date and time");
        System.out.println("[8]: Determine the habitat size for a dinosaur");
        System.out.println("[9]: Sell tickets to customers");
        System.out.println("[10]: End your shift");

        // Get user input
        Scanner sc = new Scanner(System.in);
        int result = sc.nextInt();

        // Handle user input
        if (result == 1) {
            System.out.println();
            // Calculate the average weight of two dinosaurs
            ParkDinosaur.displayAvgWeight();
        } else if (result == 2) {
            System.out.println();
            // Calculate how much to feed a dinosaur and how many times a day
            ParkDinosaur.properNutrition();
        } else if (result == 3) {
            System.out.println();
            // Calculate the age difference between two dinosaurs
            ParkDinosaur.displayAgeDiiference();
        } else if (result == 4) {
            System.out.println();
            // Determine if the dinosaur is a carnivore, herbivore, or omnivore
            ParkDinosaur.diet();
        } else if (result == 5) {
            System.out.println();
            // Check which dinosaurs you can interact with based on years of experience
            dinosByExperience();
        } else if (result == 6) {
            System.out.println();
            // Check which task are assigned based off of users position
            taskByPosition();
        }
        else if (result == 7) {
            System.out.println();
            // Get the current date, time, or date and time
            LegacyParkConsole.getDateTime();
        }
        else if (result ==8) {
            // Check the habitat size based on the dinosaur species
            ParkDinosaur.habitatSize();
        }
        else if (result == 9){
            // Simulate selling tickets
            ticketSellingSim();
        }
        else if (result == 10){
            System.exit(0);
        }
        else {
            System.out.println();
            // Error message
            System.out.println("Please enter a valid response.");

            // Wait one second to continue
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Return to staff options menu
            staffOptions();
        }
        // Return to staff options menu
        staffOptions();

    }

    public static void dinosByExperience() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many years have you worked at the park?");

        // Get user input
        int result = scanner.nextInt();
        System.out.println();

        // Handling user input
        if (result <= 1 && result >= 0) {
            // If user has worked at park for up to a year
            System.out.println("You have not worked here long enough to work with any of the dinosaurs.");
        } else if (result >= 2 && result <= 4) {
            // If user has worked at park between two and four years
            System.out.println("You can work with the less aggressive dinosaurs.");
        } else if (result >= 5 && result <= 7) {
            // If user has worked at park between five and seven years
            System.out.println("You can work with somewhat aggressive dinosaurs.");
        } else if (result >= 7) {
            // If user has worked at park for more than seven years
            System.out.println("You can work with any dinosaur in the park!");
        } else {
            // Error handling
            System.out.println();

            System.out.println("Please enter a valid whole number");

            // Wait two seconds before continuing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Return to dinosaurs by experience
            dinosByExperience();
        }
        System.out.println();

        // Wait three seconds before continuing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Wait for the user to press Enter to continue
        System.out.println("Press Enter to continue...");

        // Get user input [enter] to continue
        Scanner scan = new Scanner(System.in);
        scan.nextLine();

        // Return to staff options menu
        staffOptions();
    }

    public static void taskByPosition() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your position?");
        System.out.println("[1]: Intern, [2]: Tour Guide, [3]: Zookeeper," +
                "\n[4]: Veterinarian, [5]: Dinosaur Caretaker, [6]: Guest Services Agent");

        // Get user input
        int position = scanner.nextInt();

        // Handling user input
        switch (position) {
            case 1:
                System.out.println();

                // Display duties for an intern
                System.out.println("Your duties include: " +
                                   "\nCleaning," +
                                   "\nMaking coffee," +
                                   "\nLearning from mentors");
                    break;
            case 2:
                System.out.println();

                // Display duties for a tour guide
                System.out.println("Your duties include: " +
                        "\nTaking the visitors on journey to explore all of the sights, " +
                        "sounds, and even the smells throughout the Zoo,");
                    break;
            case 3:
                System.out.println();

                // Display duties for a zookeeper
                System.out.println("Your duties include: " +
                        "\nPlanning diets," +
                        "\nFeeding dinosaurs," +
                        "\nMonitoring the dinosaurs eating patterns");
                    break;
            case 4:
                System.out.println();

                // Display duties for a veterinarian
                System.out.println("Your duties include: " +
                        "\nMonitoring the health of dinosaurs living at the zoo");
                break;
            case 5:
                System.out.println();

                // Display duties for a dinosaur caretaker
                System.out.println("Your duties include: " +
                        "\nFeeding dinosaurs," +
                        "\nBathing dinosaurs," +
                        "\nGrooming dinosaurs," +
                        "\nExercising dinosaurs," +
                        "\nProviding dinosaurs companionship");
                break;
            case 6:
                System.out.println();

                // Display duties for a guest services agent
                System.out.println("Your duties include: " +
                        "\nChecking guest in," +
                        "\nProcessing payments," +
                        "\nAssisting guests");
                    break;
            default:
                System.out.println();

                // Error Handling
                System.out.println("Please enter a valid choice");

                // Wait two seconds before continuing
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Return to task allocation
                taskByPosition();
        }

        System.out.println();

        // Wait three seconds before continuing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Wait for the user to press Enter to continue
        System.out.println("Press Enter to continue...");

        // Get user input [enter] to continue
        Scanner scan = new Scanner(System.in);
        scan.nextLine();

        // Return to staff options
        staffOptions();
    }

    public static void ticketSellingSim(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many tickets are left to sell?");
        int ticketsLeft = scanner.nextInt();

        while(ticketsLeft > 0){
            System.out.println("A customer is requesting their ticket");
            System.out.println("The customer hands you their debit card");
            System.out.println("Press [Enter] to swipe the the debit card.");

            // Wait for the user to press enter
            scanner.nextLine();

            System.out.println("The purchase was approved!");
            System.out.println("Press [Enter] to hand the customer back their debit card and give them their ticket");

            // Wait for the user to press enter
            scanner.nextLine();

            ticketsLeft -= 1;
        }
    }
}
