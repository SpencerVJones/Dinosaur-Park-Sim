package Dino;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Call intro
        intro();

    }

    static void intro() {
        Scanner scan = new Scanner(System.in);

        // Ask the user to choose if they are a visitor or a staff member
        System.out.println("""
                Are you a\s
                [1]: Visitor\s
                [2]: Staff member""");

        // Get user input
        int customerOrStaff = scan.nextInt();

        // Handling user input
        if (customerOrStaff == 1 ) {
            // Take user to the visitor greeting
            Visitor.visitorGreeting();
        }
        else if (customerOrStaff == 2) {
            // take user to the staff greeting
            Staff.staffGreeting();
        }
        // Error handling
        else {
            System.out.println();
            System.out.println("Please enter a valid response");
            System.out.println();

            // Wait two seconds before continuing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            intro();
        }
    }
}