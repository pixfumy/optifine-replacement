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
    private final String name;
    private final HashMap<String, Long> elapsedTimes = new HashMap<>();

    public Profiler(String name) {
        this.name = name;
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
                                stringBuilder.append(entry.getValue());
                                stringBuilder.append("ms");
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
        long t = System.currentTimeMillis();
        runnable.run();
        long elapsed = System.currentTimeMillis() - t;
        synchronized (elapsedTimes) {
            elapsedTimes.put(runnableName, elapsed);
        }
    }
}
