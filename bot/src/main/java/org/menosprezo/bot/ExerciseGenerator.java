package org.menosprezo.bot;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.regex.*;

import java.io.IOException;

public class ExerciseGenerator {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("API_KEY");

    private String cleanExercise(String content) {
        return content.replaceAll("\\s*\\([^)]*\\)", "").trim();
    }

    public String generateExercise(String level) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = String.format("Crie um exercício de inglês nível %s com respostas. Exemplo: Complete: 'I ___ to school.' (go/goes)", level);

        String jsonBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]"
                + "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (!choices.isEmpty()) {
                String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
                return cleanExercise(content);
            } else {
                throw new IOException("Resposta vazia da API.");
            }
        } else {
            throw new IOException("Falha ao chamar a API: " + response.code());
        }
    }
}
