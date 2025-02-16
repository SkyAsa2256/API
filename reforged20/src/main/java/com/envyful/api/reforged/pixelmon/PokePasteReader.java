package com.envyful.api.reforged.pixelmon;

import com.envyful.api.concurrency.UtilLogger;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.export.PokemonConverterFactory;
import com.pixelmonmod.pixelmon.api.pokemon.export.exception.PokemonImportException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 * Reads a PokePaste format and converts it to a list of Pokemon
 * <br>
 * Can read from a URL or a file
 *
 */
public class PokePasteReader {

    private final BufferedReader reader;

    private PokePasteReader(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     *
     * Reads the lines from the input source and converts them to a list of Pokemon
     * <br>
     * Warning: This method does not cache the result
     * and closes the reader after reading all the lines
     *
     * @return
     */
    public List<Pokemon> build() {
        List<String> lines = new ArrayList<>();
        String currentLine;

        while((currentLine = this.readLine(this.reader)) != null) {
            lines.add(currentLine);
        }

        this.closeReader();

        try {
            return PokemonConverterFactory.importText(lines);
        } catch (PokemonImportException e) {
            UtilLogger.getLogger().error("Failed to import pokemon from PokePaste", e);
        }

        return Collections.emptyList();
    }

    private void closeReader() {
        try {
            this.reader.close();
        } catch (IOException e) {
            UtilLogger.getLogger().error("Failed to import pokemon from PokePaste", e);
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            UtilLogger.getLogger().error("Failed to import pokemon from PokePaste", e);
        }

        return null;
    }

    /**
     *
     * Creates a new PokePasteReader from a PokePaste URL
     *
     * @param paste The PokePaste URL
     * @return The PokePasteReader
     */
    public static PokePasteReader from(String paste) {
        URL url = getPokePasteURL(paste);

        if(url == null) {
            return null;
        }

        InputStream inputStream = getConnectionStream(url);

        if(inputStream == null) {
            return null;
        }

        return new PokePasteReader(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    private static URL getPokePasteURL(String paste) {
        if (!paste.toLowerCase(Locale.ROOT).endsWith("/raw")) {
            paste += "/raw";
        }

        try {
            return new URL(paste);
        } catch (MalformedURLException e) {
            UtilLogger.getLogger().error("Failed to import pokemon from PokePaste", e);
        }

        return null;
    }

    private static InputStream getConnectionStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            UtilLogger.getLogger().error("Failed to import pokemon from PokePaste", e);
        }

        return null;
    }

    /**
     *
     * Creates a new PokePasteReader from a file
     *
     * @param file The file
     * @return The PokePasteReader
     */
    public static PokePasteReader from(File file) {
        if(file == null) {
            return null;
        }

        InputStream inputStream = getFileStream(file);

        if(inputStream == null) {
            return null;
        }

        return new PokePasteReader(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    private static InputStream getFileStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            UtilLogger.getLogger().error("Failed to import pokemon from PokePaste", e);
        }

        return null;
    }
}