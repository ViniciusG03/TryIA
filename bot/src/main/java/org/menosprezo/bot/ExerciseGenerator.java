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
                "Crie um exercício de inglês nível %s no seguinte formato:\n" +
                        "Complete: 'She ___ to school.' (is/am/are) - is\n" +
                        "Forneça as opções entre parênteses, separadas por '/'.\n" +
                        "Inclua a resposta correta após o traço '-' como no exemplo.", level);

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
            JSONObject jsonResponse = new JSONObject(responseBody);

            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (!choices.isEmpty()) {
                String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

                // Extrai os componentes: pergunta, opções e resposta correta
                String question = content.split("\\(")[0].trim();
                String options = content.substring(content.indexOf("(") + 1, content.indexOf(")")).trim();
                String answer = content.substring(content.indexOf("-") + 1).trim();

                return new String[]{question, options, answer};
            }
        }
        throw new IOException("Falha ao gerar exercício com a API.");
    }
}
