package net.ae97.reporter;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

public class ReporterRunnable implements Runnable {

    private final Reporter plugin;
    private final URL reportURL;

    public ReporterRunnable(Reporter pl) throws MalformedURLException {
        plugin = pl;
        reportURL = new URL(plugin.getConfig().getString("url"));
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        long free, total, max;
        int proc = runtime.availableProcessors();
        free = runtime.freeMemory();
        total = runtime.totalMemory();
        max = runtime.maxMemory();
        try {
            HttpURLConnection conn = (HttpURLConnection) reportURL.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            Gson json = new Gson();
            HashMap<String, Long> mappings = new HashMap<>();
            mappings.put("free", free);
            mappings.put("total", total);
            mappings.put("max", max);

            byte[] jsonBytes = json.toJson(mappings).getBytes();

            conn.connect();
            conn.addRequestProperty("User-Agent", "Reporter");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.addRequestProperty("Content-Length", Integer.toString(jsonBytes.length));
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Connection", "close");
            try (OutputStream out = conn.getOutputStream()) {
                out.write(jsonBytes);
                out.flush();
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not report memory stats", ex);
        }
    }

}
