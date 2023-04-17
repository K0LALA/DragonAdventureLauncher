package fr.kolala.launcher.game.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerStatus {

    private final String ip;
    private JSONObject jsonObject;

    public ServerStatus(String ip) {
        this.ip = ip;

        update();
    }

    public void update() {
        try {
            // Create a URL object
            URL url = new URL("https://api.mcsrvstat.us/2/" + ip);

            // Open a connection to the URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the request method
            con.setRequestMethod("GET");

            // Get the input stream from the HTTP response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            // Read the response data into a string
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse the JSON data into a Java object using json-simple library
            JSONParser parser = new JSONParser();
            this.jsonObject = (JSONObject) parser.parse(content.toString());

            // Disconnect from the URL connection
            con.disconnect();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline () {
        return (boolean) this.jsonObject.get("online");
    }

    public long getOnlinePlayers () {
        return (long) ((JSONObject) this.jsonObject.get("players")).get("online");
    }

    public long getMaxPlayer () {
        return (long) ((JSONObject) this.jsonObject.get("players")).get("max");
    }


}
