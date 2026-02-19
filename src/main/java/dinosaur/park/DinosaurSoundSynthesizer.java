package dinosaur.park;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DinosaurSoundSynthesizer {
    private static final float SAMPLE_RATE = 44100f;
    private static final ExecutorService SOUND_POOL = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "dino-call");
        thread.setDaemon(true);
        return thread;
    });

    private DinosaurSoundSynthesizer() {
    }

    public static void playSpeciesCall(DinosaurCatalog.DinosaurRecord dinosaur) {
        if (dinosaur == null) {
            return;
        }

        SOUND_POOL.submit(() -> {
            if (dinosaur.massKg() <= 80 || dinosaur.lengthMeters() <= 2.2) {
                synthesizeBirdLikeCall(dinosaur.soundHz(), dinosaur.dangerLevel());
                return;
            }

            if ("Herbivore".equalsIgnoreCase(dinosaur.diet()) && dinosaur.massKg() >= 2500) {
                synthesizeResonantBellow(dinosaur.soundHz(), dinosaur.dangerLevel());
                return;
            }

            if ("Carnivore".equalsIgnoreCase(dinosaur.diet()) && dinosaur.massKg() >= 900) {
                synthesizePredatorRoar(dinosaur.soundHz(), dinosaur.dangerLevel());
                return;
            }

            synthesizeMidRangeCall(dinosaur.soundHz(), dinosaur.dangerLevel());
        });
    }

    public static void playDinoCall(int baseFrequencyHz, int dangerLevel) {
        int clampedHz = Math.max(70, Math.min(680, baseFrequencyHz));
        int clampedDanger = Math.max(1, Math.min(10, dangerLevel));
        SOUND_POOL.submit(() -> synthesizePredatorRoar(clampedHz, clampedDanger));
    }

    private static void synthesizePredatorRoar(int baseHz, int dangerLevel) {
        int durationMs = 760 + (dangerLevel * 105);
        double grit = 0.07 + dangerLevel * 0.02;
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, 4096);
            line.start();
            writeSamples(line, durationMs, t -> {
                double mod = Math.sin(2 * Math.PI * (3.0 + dangerLevel * 0.45) * t) * (18 + dangerLevel * 2.4);
                double freq = Math.max(45, baseHz - 22 + mod);
                double main = Math.sin(2 * Math.PI * freq * t);
                double undertone = Math.sin(2 * Math.PI * (freq * 0.52) * t) * 0.5;
                double rasp = (Math.random() * 2 - 1) * grit;
                return main * 0.58 + undertone * 0.34 + rasp;
            }, 0.72);
        } catch (LineUnavailableException ignored) {
        }
    }

    private static void synthesizeResonantBellow(int baseHz, int dangerLevel) {
        int durationMs = 920 + (dangerLevel * 80);
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, 4096);
            line.start();
            writeSamples(line, durationMs, t -> {
                double low = Math.max(35, baseHz * 0.55);
                double phase = Math.sin(2 * Math.PI * 1.8 * t);
                double tone = Math.sin(2 * Math.PI * (low + phase * 6) * t);
                double overtone = Math.sin(2 * Math.PI * (low * 2.0) * t) * 0.18;
                return tone * 0.84 + overtone;
            }, 0.68);
        } catch (LineUnavailableException ignored) {
        }
    }

    private static void synthesizeBirdLikeCall(int baseHz, int dangerLevel) {
        int pulses = 3 + Math.max(0, dangerLevel / 3);
        int pulseMs = 120;
        int gapMs = 70;
        int freq = Math.max(250, Math.min(1200, baseHz + 280));

        for (int i = 0; i < pulses; i++) {
            synthesizeChirp(freq + (i * 18), pulseMs);
            sleepQuietly(gapMs);
        }
    }

    private static void synthesizeMidRangeCall(int baseHz, int dangerLevel) {
        int durationMs = 540 + dangerLevel * 50;
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, 4096);
            line.start();
            writeSamples(line, durationMs, t -> {
                double freq = baseHz + Math.sin(2 * Math.PI * 4.3 * t) * 14;
                double wave = Math.sin(2 * Math.PI * freq * t);
                double harmonic = Math.sin(2 * Math.PI * (freq * 1.4) * t) * 0.25;
                return wave * 0.75 + harmonic;
            }, 0.64);
        } catch (LineUnavailableException ignored) {
        }
    }

    private static void synthesizeChirp(int frequencyHz, int durationMs) {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, 2048);
            line.start();
            writeSamples(line, durationMs, t -> {
                double glide = frequencyHz + (Math.sin(2 * Math.PI * 8 * t) * 35);
                return Math.sin(2 * Math.PI * glide * t);
            }, 0.58);
        } catch (LineUnavailableException ignored) {
        }
    }

    private interface SampleSignal {
        double sample(double timeSeconds);
    }

    private static void writeSamples(SourceDataLine line, int durationMs, SampleSignal signal, double gain) {
        int totalSamples = (int) ((durationMs / 1000.0) * SAMPLE_RATE);
        byte[] buffer = new byte[4096];
        int sampleIndex = 0;

        while (sampleIndex < totalSamples) {
            int byteIndex = 0;
            while (byteIndex < buffer.length && sampleIndex < totalSamples) {
                double progress = (double) sampleIndex / totalSamples;
                double envelope = Math.sin(Math.PI * progress);
                double t = sampleIndex / SAMPLE_RATE;

                double value = signal.sample(t) * envelope * gain;
                value = Math.max(-1.0, Math.min(1.0, value));
                short pcm = (short) (value * Short.MAX_VALUE);

                buffer[byteIndex++] = (byte) (pcm & 0xFF);
                buffer[byteIndex++] = (byte) ((pcm >> 8) & 0xFF);
                sampleIndex++;
            }
            line.write(buffer, 0, byteIndex);
        }

        line.drain();
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
