package karate.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    public static void cucumberJSON(List<String> jsonPath){
        List<JSONArray> data = new ArrayList<>(jsonPath.size());
        for (String path : jsonPath) {
            JSONParser jsonParser = new JSONParser();
            try {
                FileReader reader = new FileReader(path);
                Object parseObject = jsonParser.parse(reader);
                JSONArray  dataJSON = (JSONArray) parseObject;
                data.add(dataJSON);
            } catch (IOException | ParseException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        JSONArray cucumberjson = new JSONArray();
        for (JSONArray json : data) {
            for (Object o : json) {
                JSONObject current = (JSONObject) o;
                cucumberjson.add(current);
            }
        }

        try {
            String ruta = System.getProperty("user.dir") + "/target/cucumber-html-reports/cucumber.json";
            FileWriter file = new FileWriter(ruta);
            file.write(cucumberjson.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public static JSONObject getData(String rutaRelativa){
        JSONParser jsonParser = new JSONParser();
        try {
            String ruta = System.getProperty("user.dir") + "/src/main/resources/" + rutaRelativa;
            FileReader reader = new FileReader(ruta);

            //Read JSON file
            Object parseObject = jsonParser.parse(reader);

            JSONObject  data = (JSONObject) parseObject;

            return data;
        } catch (IOException | ParseException e) {
            System.out.println("[ERROR] " + e.getMessage());
            return null;
        }
    }

    public static String getData(String rutaRelativa, int index, String param) {
        JSONParser jsonParser = new JSONParser();
        try {
            String ruta = System.getProperty("user.dir") + "/src/test/java/resources/" + rutaRelativa;
            FileReader reader = new FileReader(ruta);

            //Read JSON file
            Object parseObject = jsonParser.parse(reader);
            JSONArray  data = (JSONArray) parseObject;

            JSONObject current = (JSONObject) data.get(index);
            String response = (String) current.get(param);

            return response;
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
            return null;
        } catch (ParseException e) {
            System.out.println("[ERROR] " + e.getMessage());
            return null;
        }
    }


}
