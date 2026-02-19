package dinosaur.park;

import java.util.Scanner;

public class ParkDinosaur {
    public String name;
    public String species;
    private int age;
    private String diet;
    private int height;
    private int length;
    private int weight;
    private int speed;

    public ParkDinosaur(String name, String species, int age, String diet,
                    int height, int length, int weight, int speed) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.diet = diet;
        this.height = height;
        this.length = length;
        this.weight = weight;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public int getAge() {
        return age;
    }

    public String getDiet() {
        return diet;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getWeight() {
        return weight;
    }

    public int getSpeed() {
        return speed;
    }


    // 1. dinosaurInfo
    // 2. getRandomDino
    // 3. speciesFacts
    // 4. dinoNutrition
    // 5. dinoDiet
    // 6. avgDinoWeight
    // 7. dinoAgeDifference
    // 8. dino housing size

    // Get dinosaur information
    public void dinosaurInfo() {
        System.out.println("This is a " + species + "!");
        System.out.println("It is " + height + " feet tall, "
                + length + " feet long, and it weighs "
                + weight + " pounds.");

        System.out.println();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Its name is " + name
                + ", it has been around for " + age
                + " million years, and it is a "
                + diet + ".");
        System.out.println("This dinosaur can reach speeds of up to "
                + speed + " miles per hour!");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Wait for the user to press Enter to continue
        System.out.println();
        System.out.println("Press Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // Return to staff options
        LegacyStaffConsole.staffOptions();
    }

    // Get a random dinosaur
    public static void getRandomDino(DinosaurRegistry park) {
        ParkDinosaur randomDinosaur = park.getRandomDinosaur();
        if (randomDinosaur != null) {
            randomDinosaur.dinosaurInfo();
            System.out.println();
            System.out.println();
        } else {
            System.out.println("We currently have no dinosaurs in the park.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LegacyVisitorConsole.visitorOptions();
        }
    }


    // Get facts about a dinosaur species
    public static void speciesFacts() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the species you would like information about?");
        System.out.println("""
                [1]: Tyrannosaurus Rex, [2]: Allosaurus, [3]: Ankylosaurus,\s
                [4]: Triceratops, [5]: Brachiosaurus, [6]: Diplodocus,\s
                [7]: Stegosaurus, [8]: [Spinosaurus, [9]: Velociraptor""");

        // Get user input
        int species = scanner.nextInt();

        String facts = "";

        // Handle user input
        switch(species) {
            case 1:
                // Get facts about the Tyrannosaurus Rex
                facts = """
                        - The T-Rex had highly developed senses, including keen vision, excellent hearing, and a strong sense of smell.
                        - The Tyrannosaurus Rex had a massive skull balanced by a long, heavy tail.\s
                        - The Tyrannosaurus Rex reigned at the top of the Late Cretaceous food chain.""";
                System.out.println(facts);
                break;
            case 2:
                // Get facts about the Allosaurus
                facts = """
                        The Allosaurus was known for its large size, sharp teeth, and strong jaws, making it one of the top predators of its time.
                        - The Allosaurus had unique physical features, including long, powerful legs and short arms with three-fingered hands equipped with sharp claws.
                        - Allosaurus means "different lizard," referring to its unique vertebrae structure compared to other dinosaurs of its time.""";
                System.out.println(facts);
                break;
            case 3:
                // Get facts about the Ankylosaurus
                facts = """
                        - The Ankylosaurus was a heavily armored dinosaur with bony plates embedded in its skin, offering protection against predators.
                        - The Ankylosaurus possessed a distinctive club-like tail, which it likely used for defense against predators like Tyrannosaurus rex.
                        - The Ankylosaurus Fossil remains have been found in North America, particularly in present-day Montana, Wyoming, and Alberta, Canada.""";
                System.out.println(facts);
                break;
            case 4:
                // Get facts about the Triceratops
                facts = """
                        - The Triceratops means "three-horned face" in Greek.
                        - Fossil evidence suggests Triceratops lived in herds and may have used their horns for defense against predators like T. rex.
                        - The Triceratops likely had a relatively slow growth rate compared to other dinosaurs.""";
                System.out.println(facts);
                break;
            case 5:
                // Get facts about the Brachiosaurus
                facts = """
                        - The Brachiosaurus had a long neck, which made up almost half of its total length, and a small head with nostrils on the top of its skull.
                        - The Brachiosaurus had a relatively long tail compared to other sauropods.
                        - The Brachiosaurus was likely relatively agile due to its long limbs.""";
                System.out.println(facts);
                break;
            case 6:
                // Get facts about the Diplodocus
                facts = """
                        - The Diplodocus  had a small head with peg-like teeth, ideal for stripping leaves off branches.
                        - The Diplodocus long neck and tail made up more than half of its total length.
                        - The Diplodocus likely traveled in herds and may have used its long tail as a defense against predators.""";
                System.out.println(facts);
                break;
            case 7:
                // Get facts about the Stegosaurus
                facts = """
                        - The Stegosaurus had a small head with a beak for cropping vegetation and tiny teeth.
                        - The Stegosaurus had a relatively small brain compared to its body size.
                        - The Stegosaurus had a tail tipped with spikes, known as a thagomizer, which may have been used for defense.""";
                System.out.println(facts);
                break;
            case 8:
                // Get facts about the Spinosaurus
                facts = """
                        - The Spinosaurus likely had a semi-aquatic lifestyle, with adaptations for swimming and hunting in water.
                        - The Spinosaurus' skull was long and narrow, resembling that of a crocodile, with conical teeth adapted for catching fish.
                        - The Spinosaurus considered a highly specialized predator, uniquely adapted to its aquatic environment.""";
                System.out.println(facts);
                break;
            case 9:
                // Get facts about the Velociraptor
                facts = """
                        - The Velociraptor was a small, agile carnivorous dinosaur, about the size of a turkey.
                        - The Velociraptor had a distinctive sickle-shaped claw on each foot, used for slashing prey.
                        - Fossils of the Velociraptor have been found in Mongolia and China.""";
                System.out.println(facts);
                break;
            default:
                System.out.println();
                System.out.println("Please enter a valid option.");

                // Wait for two seconds before continuing
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Return to species facts
                speciesFacts();
                break;

        }
        // Return to visitor options
        LegacyVisitorConsole.visitorOptions();
    }

    // Get the proper nutrition of a dinosaur based on its weight and how many times it eats a day
    public static void properNutrition() {
        System.out.println("What is the weight of your dinosaur?");
        // Get user input
        Scanner scan = new Scanner(System.in);
        int weight = scan.nextInt();
        System.out.println();

        System.out.println("How many times a day does this dinosaur eat?");
        // Get user input
        int timesPerDay = scan.nextInt();

        // Handle user input
        if (weight > 0 && timesPerDay >0) {
            // 1 pound of food per 70 pounds
            int dailyFood = weight / 70;
            int amountOfFood = dailyFood / timesPerDay;

            System.out.println();
            System.out.println("You need to feed this dinosaur " + amountOfFood +
                    " pounds of food " + timesPerDay + " times per day.");
            System.out.println("That is a total of " + dailyFood + " pounds of food a day.");
            System.out.println();
        }
        else {
            // Error handling
            System.out.println("Please make sure to enter valid whole numbers greater than zero");

            // Wait for one second before continuing
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Return to proper nutrition method
            System.out.println();
            properNutrition();
        }

        // Wait for one second before continuing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Wait for the user to press Enter to continue
        System.out.println();
        System.out.println("Press Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // Return to visitor options
        LegacyVisitorConsole.visitorOptions();
    }

    // Display the average weight of two dinosaurs
    public static void displayAvgWeight() {
        System.out.println("What is the weight of the smallest dinosaur?");
        Scanner sc = new Scanner(System.in);
        //
        double weight1 = sc.nextDouble();
        System.out.println("What is the weight of the largest dinosaur?");
        double weight2 = sc.nextDouble();

        double avgWeight = (weight1 + weight2) / 2;
        System.out.println("The average weight is " + avgWeight + ".");
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.println();




    }

    // Display the age difference of two dinosaurs
    public static void displayAgeDiiference() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the age of the older dinosaur");
        int age1 = scanner.nextInt();
        System.out.println("Enter the age of the younger dinosaur");
        int age2 = scanner.nextInt();
        int ageDiff = age1 - age2;
        System.out.println("The age difference is " + ageDiff + " years.");
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.println();


    }

    // Determine if your dinosaur is a carnivore, herbivore, or omnivore based off of what they eat
    public static void diet () {
        // if statement that prints whether a dinosaur is a carnivore or herbivore based
        // on a boolean variable.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Does this dinosaur eat meat, plants, or both?");
        String diet = scanner.nextLine();
        if (diet.equals("meat")) {
            System.out.println("Your dinosaur is a carnivore.");
        }
        else if (diet.equals("plants")) {
            System.out.println("Your dinosaur is a herbivore.");
        }
        else if (diet.equals("both")) {
            System.out.println("Your dinosaur is an omnivore.");
        }
        else {
            System.out.println("Please enter a valid response.");
            System.out.println("[meat][plants][both]");
            diet();
        }
    }

    // Determine the housing size your dinosaur needs based off of the species
    public static void habitatSize() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the species of your dinosaur.");
        System.out.println("Tyrannosaurus Rex, Allosaurus, Ankylosaurus, Triceratops, Brachiosaurus, Diplodocus, Stegosaurus, Spinosaurus, Velociraptor");
        String result = scanner.nextLine();
        switch (result) {
            case "Velociraptor":
                System.out.println("This dinosaur belongs in a small enclosure.");
                break;
            case "Allosaurus", "Ankylosaurus", "Triceratops", "Stegosaurus", "Spinosaurus":
                System.out.println("This dinosaur belongs in a medium enclosure.");
                break;
            case "Tyrannosaurus Rex":
                System.out.println("This dinosaur belongs in a large enclosure.");
                break;
            case "Brachiosaurus", "Diplodocus":
                System.out.println("This dinosaur belongs in an extra large enclosure.");
                break;
            default:
                System.out.println("We do not currently have that dinosaur.");
                habitatSize();
        }
        LegacyStaffConsole.staffOptions();
    }

}
