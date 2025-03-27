package karate.util;

import org.apache.commons.io.FileUtils;
import org.json.*;

import java.util.Base64;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.nio.file.*;

public class Cucumber {

    public String output(String path) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(path), new String[] {"json"}, true);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        return join(jsonPaths);
    }

    private String join(List<String> jsonPath) {
        List<JSONArray> data = new ArrayList<>();

        jsonPath.forEach(path -> {
            try {
                String content = new String(Files.readAllBytes(Paths.get(path)));
                data.add(new JSONArray(content));
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        });

        JSONArray cucumberjson = new JSONArray();

        data.forEach(json -> json.forEach(o -> cucumberjson.put(o)));

        cucumberjson.forEach(item -> {
            JSONObject scenario = (JSONObject) item;
            JSONArray elements = scenario.getJSONArray("elements");
        
            elements.forEach(elementItem -> {
                JSONObject element = (JSONObject) elementItem;
                JSONArray steps = element.getJSONArray("steps");
        
                steps.forEach(stepItem -> {
                    JSONObject step = (JSONObject) stepItem;
                    if (step.has("doc_string")) {
                        JSONObject docString = step.getJSONObject("doc_string");
                        JSONObject embeddings = new JSONObject();
        
                        embeddings.put("mime_type", "text/plain");
                        String value = docString.getString("value");
                        String base64Value = Base64.getEncoder().encodeToString(value.getBytes());
                        embeddings.put("data", base64Value);
        
                        JSONArray embeddingsArray;
                        if (step.has("embeddings")) {
                            embeddingsArray = step.getJSONArray("embeddings");
                        } else {
                            embeddingsArray = new JSONArray();
                            step.put("embeddings", embeddingsArray);
                        }
                        embeddingsArray.put(embeddings);
                    }
                });
            });
        });

        // Mejora en la escritura del archivo resultante
        String ruta = System.getProperty("user.dir") + "/target/karate-reports/cucumber.json";
        try (FileWriter file = new FileWriter(ruta)) {
            file.write(cucumberjson.toString());
            System.out.println("By Cucumber Report");
            System.out.println("===================================================================");
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        return cucumberjson.toString();
    }
}
