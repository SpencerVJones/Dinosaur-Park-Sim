package dinosaur.park;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

public class LegacyParkConsole {
    public LegacyParkConsole() {
    }

    // currentDate
    // currentTime
    // parkGateOpening
    // parkSafetyRating
    // amountOfVisitors

    public static void amountOfVisitors() {
        Random random = new Random();
        int upperBound = 100;
        int visitorCount = random.nextInt(upperBound);
        System.out.println("There is a maximum of 100 visitors allowed in Ancient Eden at one time.");
        if (visitorCount <= 99) {
            visitorCount += 1;
            System.out.println("You are the " + visitorCount + "th visitor, so there is still space in the park!");
        } else {
            System.out.println("Sorry! There are too many visitors in the park, " +
                    "please come back at another time!");
            System.exit(0);
        }
    }

    public static void parkOpeningCountdown() throws InterruptedException {
        int time = 10;
        System.out.println("The park opens in 10 seconds!");
        Thread.sleep(500);
        while (time > 0) {
            System.out.println(time);
            time--;
            Thread.sleep(1000);
        }
    }

    public static void getDateTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What information do you need?");
        System.out.println("[1]: Current Date");
        System.out.println("[2]: Current Time");
        System.out.println("[3]: Current Date & Time");
        System.out.println("[0]: Quit");
        int result = scanner.nextInt();

        if (result == 1) {
            LocalDate date = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MM dd, yyyy");
            String formattedDate = date.format(dateFormatter);
            System.out.println("Today is " +formattedDate);
        }
        else if (result == 2) {
            LocalTime time = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedTime = time.format(timeFormatter);
            System.out.println("The current time is " + formattedTime);
        }
        else if (result == 3) {
            LocalDate date = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MM dd, yyyy");
            String formattedDate = date.format(dateFormatter);
            System.out.println("Today is " + formattedDate);

            LocalTime time = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedTime = time.format(timeFormatter);
            System.out.println("The current time is " + formattedTime);
        }
        else if (result == 0) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Were you a [1]: Visitor or a [2]: Staff Member?");
            int memberType = sc.nextInt();

            if (memberType == 1) {
                LegacyVisitorConsole.visitorOptions();
            }
            else if (memberType == 2) {
                LegacyStaffConsole.staffOptions();
            }
            else {
                System.out.println("Please enter a valid option" +
                        "\n [1]: Visitor" +
                        "\n [2]: Staff Member");
            }
        }
        else {
            System.out.println("Please enter a valid option");
            System.out.println("[1]: Current Date");
            System.out.println("[2]: Current Time");
            System.out.println("[3]: Current Date & Time");
            System.out.println("[0]: Quit");
        }
    }

}
