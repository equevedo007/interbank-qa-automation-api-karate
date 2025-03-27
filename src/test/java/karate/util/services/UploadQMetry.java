package karate.util.services;

import karate.util.config.ConfigReader;
import karate.util.config.Settings;

import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadQMetry {
    
    private static final Logger logger = Logger.getLogger(UploadQMetry.class.getName());

    
    private static final String PRIORITY = "Medium";
    private static final String STATUS = "Done";
    private static final String AUTOMATED_GENERATED = "Automated generated ";
    private static final String AUTOMATIZABLE = "Automatizable";
    private static final String SI = "Si";
    private static final String TERMINADO = "Terminado";
    private static final String FUNCIONAL = "Funcional";

    private String userStory;
    private String components;
    private String jiraUserId;
    private String plataforma;
    private String quarter;
    private String sprint;
    private String squad;
    private String cycleFolderId;
    private String testCaseFolderId;
    private String environment;
    private QMetry qmetry = new QMetry();


    public void uploadQMetry(String cucumberJson,String plannedStartDate,String plannedEndDate, String today) {
        logger.log(Level.INFO, "Uploading to QMetry");

        ConfigReader.readConfig();

        Jira jira = new Jira(Settings.jiraUserMail, Settings.jiraAPIToken);
        String jiraContextJwt = jira.getJiraContextJwt();

        String apiKey=getApiKeyForProyect(jiraContextJwt,Settings.jiraProjectId);
        
        if (apiKey == "" ) {
            logger.log(Level.SEVERE, "Error getting ApiKey Token");
            return;
        }

        userStory = Settings.userStory;
        components = Settings.components;
        jiraUserId=Settings.jiraUserId;
        plataforma = Settings.plataforma;
        quarter = Settings.quarter;
        sprint = Settings.sprint;
        squad =Settings.squad;
        cycleFolderId = Settings.cycleFolderId;
        testCaseFolderId = Settings.testCaseFolderId;
        environment = Settings.environment;
        JSONObject bodyString = getQmetryJson(plannedStartDate,plannedEndDate,today);
        
        uploadResults(apiKey, bodyString, cucumberJson);
    }

    private String getApiKeyForProyect(String jiraContextJwt, String jiraProjectId) {
        String apiKey = "";

        if (!jiraContextJwt.isEmpty()) {
            apiKey = qmetry.getAPIKey(jiraProjectId, jiraContextJwt);
            if (apiKey.isEmpty()) {
                apiKey = qmetry.generateApiKey(jiraProjectId, jiraContextJwt);
            }  
        } else {
            logger.log(Level.SEVERE, "Error getting Jira Token");
        }
        return apiKey;
    }

    private void uploadResults( String apiKey, JSONObject bodyString, String cucumberJson) {
        try {
            HttpResponse<String> response = qmetry.postResult(apiKey, bodyString, cucumberJson);
            if (response == null || response.statusCode() != 200) {
                logger.log(Level.SEVERE, "QMetry upload failed: Invalid response or status code");
                return;
            }

            JSONObject jsonObj = new JSONObject(response.body());
            String trackingId = jsonObj.getString("trackingId");
            String url = jsonObj.getString("url");
            logger.log(Level.INFO, "Tracking ID: {0}", trackingId);

            HttpResponse<String> response2 = qmetry.putUploadResult(url, apiKey, cucumberJson);
            if (response2 != null && response2.statusCode() == 200) {
                logger.log(Level.INFO, "QMetry upload successful");
            } else {
                logger.log(Level.SEVERE, "QMetry upload failed: Invalid response or status code");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing the response", e);
        }
    }

    private JSONObject getQmetryJson( String plannedStartDate, String plannedEndDate,String today) {
        validateInputs(userStory, jiraUserId, components, plannedStartDate, plannedEndDate, plataforma, quarter, sprint, squad, today, cycleFolderId, testCaseFolderId, environment);

        JSONObject qmetry = new JSONObject();
        qmetry.put("format", "cucumber");
        qmetry.put("attachFile", true);
        qmetry.put("zip", false);
        qmetry.put("environment", environment);
        qmetry.put("matchTestSteps", true);

        JSONObject fields = new JSONObject();
        fields.put("testCycle", createTestCycle( plannedStartDate, plannedEndDate, today));
        fields.put("testCase", createTestCase());
        fields.put("testCaseExecution", createTestCaseExecution());

        qmetry.put("fields", fields);
        return qmetry;
    }

    private void validateInputs(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.isEmpty()) {
                throw new IllegalArgumentException("Input parameters cannot be null or empty");
            }
        }
    }
    private JSONObject createTestCycle(String plannedStartDate, String plannedEndDate, String today) {
        JSONObject testCycle = new JSONObject();
        testCycle.put("components", new JSONArray("["+components+"]"));
        testCycle.put("priority", PRIORITY);
        testCycle.put("status", STATUS);
        testCycle.put("folderId", cycleFolderId);
        testCycle.put("summary", userStory + "_" + components + "_" + today);
        testCycle.put("description", AUTOMATED_GENERATED + "Test Cycle");
        testCycle.put("assignee", jiraUserId);
        testCycle.put("reporter", jiraUserId);
        testCycle.put("plannedStartDate", plannedStartDate);
        testCycle.put("plannedEndDate", plannedEndDate);

        JSONArray customFieldsArray = new JSONArray();
        customFieldsArray.put(new JSONObject().put("name", "Plataforma").put("value", plataforma));
        customFieldsArray.put(new JSONObject().put("name", "Quarter").put("value", quarter));
        customFieldsArray.put(new JSONObject().put("name", "Sprint (Personalizado)").put("value", sprint));
        customFieldsArray.put(new JSONObject().put("name", "Squad").put("value", squad));

        testCycle.put("customFields", customFieldsArray);
        return testCycle;
    }

    private JSONObject createTestCase() {
        JSONObject testCase = new JSONObject();
        testCase.put("components",  new JSONArray("["+components+"]"));
        testCase.put("priority", PRIORITY);
        testCase.put("status", STATUS);
        testCase.put("folderId", testCaseFolderId);
        testCase.put("description", AUTOMATED_GENERATED + "Test Case");
        testCase.put("assignee", jiraUserId);
        testCase.put("reporter", jiraUserId);

        JSONArray customFieldsArray = new JSONArray();
        customFieldsArray.put(new JSONObject().put("name", AUTOMATIZABLE).put("value", SI).put("cascadeValue", TERMINADO));
        customFieldsArray.put(new JSONObject().put("name", "Tipo de Prueba").put("value", FUNCIONAL));

        testCase.put("customFields", customFieldsArray);
        return testCase;
    }

    private JSONObject createTestCaseExecution() {
        JSONObject testCaseExecution = new JSONObject();
        testCaseExecution.put("comment", AUTOMATED_GENERATED + "Test Execution");
        testCaseExecution.put("assignee", jiraUserId);

        JSONArray customFieldsArray = new JSONArray();
        customFieldsArray.put(new JSONObject().put("name", "Plataforma").put("value", plataforma));

        testCaseExecution.put("customFields", customFieldsArray);
        return testCaseExecution;
    }
}
