package org.vincent;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class App {
    public static void main(String[] args) {

        String api = "https://api.github.com/users/"+args[0]+"/events";
        HttpClient httpClient = HttpClient.newHttpClient();
        Gson gson = new Gson();

        //create the API GET request
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(api))
                    .header("Accept","application/vnd.github+json")
                    .GET()
                    .build();
            HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray resArr = gson.fromJson(res.body(), JsonArray.class);
            System.out.println(res.statusCode());

            if(res.statusCode() != 200){
                System.out.println("Something went wrong. Response Status code: " + res.statusCode());
            }
            //print the event type, repository and date it happened
            for(JsonElement element : resArr){
                JsonObject obj = element.getAsJsonObject();
                String[] eventArr = obj.get("type").getAsString().replace("Event","").split("(?=[A-Z])");
                String event = String.join(" ", eventArr);
                String repo = obj.getAsJsonObject("repo").get("name").getAsString();
                String createdAt = obj.get("created_at").getAsString();
                System.out.printf("- %s event happened on %s repository on %s%n", event,repo,createdAt);
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}