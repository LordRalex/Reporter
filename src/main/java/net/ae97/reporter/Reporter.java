package net.ae97.reporter;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class Reporter extends JavaPlugin {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            executorService.scheduleAtFixedRate(new ReporterRunnable(this), getConfig().getInt("startdelay", 30), getConfig().getInt("repeatdelay", 30), TimeUnit.SECONDS);
        } catch (MalformedURLException ex) {
            getLogger().log(Level.SEVERE, "Could not schedule service", ex);
        }
    }

}
