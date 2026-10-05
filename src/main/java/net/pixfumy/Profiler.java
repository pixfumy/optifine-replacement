package net.pixfumy;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Profiler {
    public enum OutputType {
        MS("ms", 1000000F),
        NS("ns", 1F);

        public final String format;
        public final float divider;

        OutputType(String format, float divider) {
            this.format = format;
            this.divider = divider;
        }
    }

    private final String name;
    private final OutputType outputType;

    private final HashMap<String, Long> startTimes = new HashMap<>();
    private final HashMap<String, Long> elapsedTimes = new HashMap<>();

    public Profiler(String name) {
        this(name, OutputType.MS);
    }

    public Profiler(String name, OutputType outputType) {
        this.name = name;
        this.outputType = outputType;
    }

    public void start() {
        new Thread(() -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss z", Locale.ENGLISH);
            while (true) {
                try {
                    synchronized (elapsedTimes) {
                        if (!elapsedTimes.isEmpty()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (Map.Entry<String, Long> entry : elapsedTimes.entrySet()) {
                                stringBuilder.append(entry.getKey());
                                stringBuilder.append(" -> ");
                                stringBuilder.append(entry.getValue() / outputType.divider);
                                stringBuilder.append(outputType.format);
                                stringBuilder.append(" @@@ ");
                                stringBuilder.append(dateFormat.format(new Date()));
                                stringBuilder.append("\n");
                            }
                            Files.write(FabricLoader.getInstance().getGameDir().resolve(name), stringBuilder.toString().getBytes(),
                                    StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                            elapsedTimes.clear();
                        }
                    }
                    Thread.sleep(5000);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void execute(Runnable runnable, String runnableName) {
        long startTime = System.nanoTime();
        runnable.run();
        long elapsedTime = System.nanoTime() - startTime;
        synchronized (elapsedTimes) {
            elapsedTimes.put(runnableName, elapsedTime);
        }
    }

    public void push(String runnableName) {
        long startTime = System.nanoTime();
        startTimes.put(runnableName, startTime);
    }

    public void pop(String runnableName) {
        long startTime = startTimes.remove(runnableName);
        long elapsedTime = System.nanoTime() - startTime;
        synchronized (elapsedTimes) {
            elapsedTimes.put(runnableName, elapsedTime);
        }
    }
}
