package org.menosprezo.bot;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class ExerciseGenerator {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("API_KEY");

    public String[] generateExercise(String level) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String prompt = String.format(
                "Gere um exercício de inglês nível %s no seguinte formato:\n" +
                        "Complete: 'She ___ to school.' (is/am/are)\n" +
                        "As opções devem ser fornecidas entre parênteses, como no exemplo.\n" +
                        "Não inclua explicações adicionais ou a resposta correta.", level);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.put(message);

        requestBody.put("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            System.out.println("Resposta da API: " + responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.optJSONArray("choices");

            if (choices != null && choices.length() > 0) {
                String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

                String cleanExercise = cleanExercise(content);
                String answer = extractAnswer(content);

                if (answer != null) {
                    return new String[]{cleanExercise, answer};
                }
            }
            throw new IOException("Resposta vazia ou mal formatada da API.");
        } else {
            throw new IOException("Falha ao chamar a API: " + response.code());
        }
    }

    private String cleanExercise(String content) {
        return content.replaceAll("\\s*-\\s*.*", "").trim();
    }

    private String extractAnswer(String content) {
        if (content.contains("-")) {
            return content.substring(content.lastIndexOf("-") + 1).trim();
        }
        return null;
    }
}
