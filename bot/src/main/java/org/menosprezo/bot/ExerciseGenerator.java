package org.menosprezo.bot;

import okhttp3.*;

import java.io.IOException;

public class ExerciseGenerator {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("API_KEY");

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
            return response.body().string();
        } else {
            throw new IOException("Falha ao chamar a API: " + response.code());
        }
    }
}
