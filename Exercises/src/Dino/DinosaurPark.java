package Dino;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DinosaurPark {
    private List<Dinosaur> dinosaurs;

    public DinosaurPark() {
        this.dinosaurs = new ArrayList<>();
    }

    public void initializeDinosaurs() {
        DinosaursData.addDinosaurs(this.dinosaurs);
    }

    public Dinosaur getRandomDinosaur() {
        if (dinosaurs.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(dinosaurs.size());
        return dinosaurs.get(index);
    }
}
