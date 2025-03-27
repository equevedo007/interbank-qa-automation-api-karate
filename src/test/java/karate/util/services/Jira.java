package karate.util.services;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Jira {
    private static final String URL_JIRA_APP = "https://innovacionpacifico.atlassian.net/plugins/servlet/ac/com.infostretch.QmetryTestManager/qtm4j-test-management";

    private String jiraUser = "";
    private String jiraApiToken = "";
    private String jiraContextJwt = "";

    public Jira(String jiraUser, String jiraApiToken) {
        this.jiraUser = jiraUser;
        this.jiraApiToken = jiraApiToken;

    }

    public String getJiraContextJwt() {
        oauth();
        return jiraContextJwt;
    }

    private void oauth() {
        HttpClient client = HttpClient.newHttpClient();
        String userCredentials = jiraUser + ":" + jiraApiToken;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_JIRA_APP))
                .header("Authorization", basicAuth)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.statusCode());
            }

            String jiraResponse = response.body();
            searchContextJwt(jiraResponse);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void searchContextJwt(String text) {
        List<String> matches = matchContextJwt(text);
        if (!matches.isEmpty()) {
            this.jiraContextJwt = matches.get(0);
        }
    }

    private List<String> matchContextJwt(String text) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"contextJwt\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }
}
