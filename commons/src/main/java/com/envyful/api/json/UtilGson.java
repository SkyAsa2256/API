package com.envyful.api.json;

import com.envyful.api.concurrency.UtilConcurrency;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Static utility class for methods regarding Google's GSON API
 *
 */
public class UtilGson {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     *
     * Attempts to download a JSON object from the given URL
     *
     * @param url The URL to download from
     * @return The completable future
     */
    public static CompletableFuture<JsonElement> downloadJson(String url) {
        return UtilConcurrency.supplyAsyncWithException(() -> {
            var compiledURL = new URL(url);
            var stream = getConnectionStream(compiledURL);

            return GSON.fromJson(new InputStreamReader(stream), JsonElement.class);
        });
    }

    private static InputStream getConnectionStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException var2) {
            IOException e = var2;
            e.printStackTrace();
            return null;
        }
    }


}
