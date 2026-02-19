package dinosaur.park;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DinosaurRegistry {
    private final List<ParkDinosaur> dinosaurs;

    public DinosaurRegistry() {
        this.dinosaurs = new ArrayList<>();
    }

    public void initializeDinosaurs() {
        dinosaurs.clear();
        DinosaurCatalog database = DinosaurCatalog.loadDefault();
        for (DinosaurCatalog.DinosaurRecord real : database.all()) {
            dinosaurs.add(new ParkDinosaur(
                    real.commonName(),
                    real.scientificName(),
                    (int) Math.round(real.timeframeMya()),
                    real.diet(),
                    metersToFeet(real.lengthMeters() * 0.28),
                    metersToFeet(real.lengthMeters()),
                    kilogramsToPounds(real.massKg()),
                    kphToMph(real.topSpeedKph())
            ));
        }
    }

    public ParkDinosaur getRandomDinosaur() {
        if (dinosaurs.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(dinosaurs.size());
        return dinosaurs.get(index);
    }

    public List<ParkDinosaur> getDinosaurs() {
        return Collections.unmodifiableList(dinosaurs);
    }

    private int metersToFeet(double meters) {
        return (int) Math.round(meters * 3.28084);
    }

    private int kilogramsToPounds(double kilograms) {
        return (int) Math.round(kilograms * 2.20462);
    }

    private int kphToMph(double kph) {
        return (int) Math.round(kph * 0.621371);
    }
}
