package karate.util.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;

public class QMetry {
    private static final String QMETRY_URL = "https://qtmcloud.qmetry.com/rest/api/automation/importresult";
    private static final String URL_QMETRY_AUTH_TOKEN_GET = "https://qtmcloud.qmetry.com/rest/api/ui/apikey/AUTOMATION/";
    private static final String URL_QMETRY_AUTH_TOKEN_POST = "https://qtmcloud.qmetry.com/rest/api/ui/apikey/generate";

    public HttpResponse<String> postResult(String apiKey, JSONObject bodyString, String cucumberJson){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QMETRY_URL))
                .header("Content-Type", "application/json")
                .header("apiKey", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(bodyString.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<String> putUploadResult(String url, String apiKey, String cucumberJson){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "multipart/form-data")
                        .header("apiKey", apiKey)
                        .PUT(HttpRequest.BodyPublishers.ofString(cucumberJson))
                        .build();
        
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            System.out.println("QMetry upload successful");
            return response;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAPIKey(String jiraProjectId,String jiraContextJwt) {
        String url = URL_QMETRY_AUTH_TOKEN_GET + jiraProjectId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "jwt " + jiraContextJwt) 
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonObj = new JSONObject(responseBody);
                String claimKey = jsonObj.getString("key");
                return claimKey;

            } else {
                System.out.println("Error Getting Api Key: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String generateApiKey(String jiraProjectId, String jiraContextJwt) {
    HttpClient client = HttpClient.newHttpClient();
    String jsonInputString = String.format("""
            {
                "locale":"es_ES",
                "timezone":"America/Bogota",
                "label":"AUTOMATE",
                "apiKeyPurpose":"AUTOMATION",
                "projectId":"%s"
            }
            """, jiraProjectId);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(URL_QMETRY_AUTH_TOKEN_POST))
            .header("Authorization", "jwt " + jiraContextJwt)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
            .build();

    try {
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String responseBody = response.body();
            JSONObject jsonObj = new JSONObject(responseBody);
            String claimKey = jsonObj.getString("key");
            return claimKey;
        } else {
            System.out.println("Error generating ApiKey: " + response.statusCode());
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }
        return "";
    }
    
}
