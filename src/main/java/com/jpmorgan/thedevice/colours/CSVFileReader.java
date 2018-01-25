package com.jpmorgan.thedevice.colours;

import com.jpmorgan.thedevice.wires.WireName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by S.King on 15/02/2017.
 */
public class CSVFileReader {

    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(CSVFileReader.class);

    private static String wires[] = {"Red", "White", "Green", "Black", "Blue", "Yellow"};
    private static String wireNames[] = {"Red", "White", "Green", "Grey", "Blue", "Orange"};


    public List<ColourSequence> read(String fileName) {
        List<ColourSequence> result = new ArrayList<>();
        try {
            File file = new File(fileName);
            this.read(new FileInputStream(file));
        } catch (FileNotFoundException fnfe) {
            log.error("File : " + fileName + "Not found : " + fnfe.getMessage(), fnfe);
        }

        return result;
    }

    public List<ColourSequence> read(InputStream inputStream) {

        String line = "";
        String cvsSplitBy = ",";
        String[] colours;
        List<ColourSequence> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                log.trace("Line = [" + line + "]");

                // use comma as separator
                colours = line.split(cvsSplitBy);

                ColourSequence colourSequence = new ColourSequence(
                        ColourSet.match(colours[0]),
                        ColourSet.match(colours[1]),
                        ColourSet.match(colours[2]),
                        ColourSet.match(colours[3]),
                        wireNameMatch(colours[5]),
                        1);

                log.trace("Colour Sequence " + colourSequence.toString());

                result.add(colourSequence);
            }

        } catch (IOException e) {
            log.error("IOException : " + e.getMessage(), e);
        }

        return result;
    }

    private WireName wireNameMatch(String name) {
        WireName result = WireName.NONE;
        int position = -1;
        for (int i = 0; i < 6; i++) {
            if (name.toUpperCase().trim().equals(wires[i].trim().toUpperCase())) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            for (WireName wn : WireName.values()) {
                if (wireNames[position].toUpperCase().equals(wn.name().toUpperCase())) {
                    result = wn;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        FileOutputStream fop = null;
        File file;

        try {

            file = new File("D:/Programming/Projects/Java/TheDevice/colourset.csv");

            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            ColourSet[] colours = ColourSet.values();
            Random random = new Random();
            int counter = 0;

            for (int index = 0; index < 300; index++) {
                String content = "";

                for (int sequence = 0; sequence < 4; sequence++) {
                    String colour = colours[random.nextInt(5) + 1].name();
                    content += colour + ", ";
                }

                content += "equals, " + wires[counter++] + ", " + (random.nextInt(2) + 1) + "\n";

                // get the content in bytes
                fop.write(content.getBytes());
                fop.flush();

                if (counter == 6) {
                    counter = 0;
                }
            }

            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
