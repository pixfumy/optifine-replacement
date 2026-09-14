package net.pixfumy;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.function.Function;

public class Profiler {
    private final String name;

    public Profiler(String name) {
        this.name = name;
    }

    public void execute(Runnable runnable) throws IOException {
        long t = System.currentTimeMillis();
        runnable.run();
        long elapsed = System.currentTimeMillis() - t;
        Files.write(FabricLoader.getInstance().getGameDir().resolve(name + "-"+ new Date().getTime() + ".txt"), String.valueOf(elapsed).getBytes());
    }
}
