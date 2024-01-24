package si.fri.rso.aichat.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import si.fri.rso.aichat.lib.ChatMetadata;
import si.fri.rso.aichat.models.converters.ChatMetadataConverter;
import si.fri.rso.aichat.models.entities.ChatMetadataEntity;

//za getAiResponse
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import java.io.FileInputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;


@RequestScoped
public class ChatMetadataBean {

    private Logger log = Logger.getLogger(ChatMetadataBean.class.getName());

    @Inject
    private EntityManager em;

    public List<ChatMetadata> getChatMetadata() {

        TypedQuery<ChatMetadataEntity> query = em.createNamedQuery(
                "ChatMetadataEntity.getAll", ChatMetadataEntity.class);

        List<ChatMetadataEntity> resultList = query.getResultList();

        return resultList.stream().map(ChatMetadataConverter::toDto).collect(Collectors.toList());

    }









    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }





    public ChatMetadata createAiMetadata(ChatMetadata chatMetadata) {
        ChatMetadataEntity chatMetadataEntity = ChatMetadataConverter.toEntity(chatMetadata);
        String response = getAiResponse(chatMetadata.getUserText());
        String userResponse = chatMetadata.getUserText();
        String userCreated = chatMetadata.getUserCreated();

        chatMetadataEntity.setText(response);
        chatMetadataEntity.setUserText(userResponse);
        chatMetadataEntity.setUserCreated(userCreated);

        //cas povratka
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        String formattedTime = currentTime.format(formatter);

        chatMetadataEntity.setCreated(formattedTime);

        try {
            beginTx();
            em.persist(chatMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (chatMetadataEntity.getChatId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ChatMetadataConverter.toDto(chatMetadataEntity);
    }





    public String getAiResponse(String query) {
        String retValue = "";
        try {
            // Set the API endpoint URL
            URL url = new URL("https://discoveryengine.googleapis.com/v1alpha/projects/988656779720/locations/global/collections/default_collection/dataStores/podatki-ai_1704796506958/servingConfigs/default_search:search");

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the request headers
            connection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams for reading/writing data
            connection.setDoOutput(true);

            // Set the request payload
            //String query = "What is docker";
            String payload = "{\"query\":\"" + query + "\",\"pageSize\":10,\"queryExpansionSpec\":{\"condition\":\"AUTO\"},\"spellCorrectionSpec\":{\"mode\":\"AUTO\"},\"contentSearchSpec\":{\"summarySpec\":{\"summaryResultCount\":5,\"ignoreAdversarialQuery\":true,\"includeCitations\":true},\"snippetSpec\":{\"returnSnippet\":true},\"extractiveContentSpec\":{\"maxExtractiveAnswerCount\":1}}}";
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(payload);
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject summaryObject = jsonObject.getAsJsonObject("summary");
                //potrebna dodelava!!! - včasih ne vrne nič
                if (summaryObject != null) {
                    JsonObject summaryWithMetadata = summaryObject.getAsJsonObject("summaryWithMetadata");

                    if (summaryWithMetadata != null) {
                        JsonElement summaryElement = summaryWithMetadata.get("summary");
                        if (summaryElement != null) {
                            String summary = summaryElement.getAsString();
                            System.out.println("Response: " + summary);
                            retValue = summary;
                        } else {
                            retValue = "Nimam odgovora na to vprašanje, oz ni vzpostavljena povezava!";
                        }
                    } else {
                        retValue = "Nimam odgovora na to vprašanje, oz ni vzpostavljena povezava!";
                    }
                } else {
                    retValue = "Nimam odgovora na to vprašanje, oz ni vzpostavljena povezava!";
                }
            }

            // Close the connection
            connection.disconnect();
        } catch (IOException e) {
            // Handle or log the IOException
            e.printStackTrace();
        }
        return retValue;
    }
    private static String getAccessToken() {
        // Implement the logic to retrieve the access token (e.g., using Google Cloud SDK or another method)
        // Replace the following line with your actual logic to get the access token
        //gcloud auth print-access-token
        String accessTokenValue = "";
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/root/.config/gcloud/application_default_credentials.json"));
            credentials.refreshIfExpired();
            AccessToken accessToken = credentials.getAccessToken();
            accessTokenValue = accessToken.getTokenValue();
            //System.out.println("koda" + accessTokenValue);
        } catch (FileNotFoundException e) {
            // Handle or log the FileNotFoundException
            e.printStackTrace();
        } catch (IOException e) {
            // Handle or log the IOException
            e.printStackTrace();
        }

        //return "ya29.a0AfB_byCdCrb-gPH5aqmwGcnPNvgkDgM7aQCHYtVHi9UOVRsN02zCs86QugcYy1S49B8K4kObYKJl9epPGsg0EwcytJ4oye7AJVZAIIddHQrFgDiEKxqX5r9C2h8Oaw1yLYltSsjZ6oNlcsorMwwmbkQRMLixK31inLf5ur8OSFwaCgYKASMSARMSFQHGX2Mi__15S9d9hGccYrGUdpD4Ng0178";
        return accessTokenValue;
    }
}
