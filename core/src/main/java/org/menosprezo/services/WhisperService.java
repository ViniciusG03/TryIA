package org.menosprezo.services;

import okhttp3.*;
import org.menosprezo.config.OpenAiConfig;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class WhisperService {

    private final OkHttpClient client = new OkHttpClient();

    public String transcribe(File audioFile) throws IOException {
        if (!audioFile.exists() || audioFile.length() == 0) {
            throw new IOException("Arquivo de áudio inválido ou inexistente.");
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(audioFile, MediaType.parse("audio/ogg")))
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .url(OpenAiConfig.WHISPER_URL)
                .header("Authorization", "Bearer " + OpenAiConfig.API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                System.out.println("Transcrição bem-sucedida: " + jsonResponse.getString("text"));
                return jsonResponse.getString("text");
            } else {
                String errorDetails = response.body() != null ? response.body().string() : "Sem detalhes.";
                System.err.println("Erro ao transcrever áudio: " + response.message() + " - " + errorDetails);
                throw new IOException("Erro na API Whisper.");
            }
        }
    }

    public String processVoice(String fileId) throws IOException {
        // Define o caminho do arquivo
        String filePath = "downloads/" + fileId + ".ogg";
        File audioFile = new File(filePath);

        // Verifica se o arquivo existe
        System.out.println("Verificando existência do arquivo: " + filePath);
        if (!audioFile.exists() || audioFile.length() == 0) {
            System.out.println("Erro: Arquivo não encontrado ou vazio.");
            throw new IOException("Arquivo de áudio não encontrado: " + filePath);
        } else {
            System.out.println("Arquivo encontrado e pronto para transcrição: " + audioFile.getAbsolutePath());
        }

        // Transcreve o arquivo
        return transcribe(audioFile);
    }
}
