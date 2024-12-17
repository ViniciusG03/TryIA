package org.menosprezo.services;

import org.menosprezo.config.PollyConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextToSpeechService {

    private final PollyClient pollyClient;

    public TextToSpeechService(String accessKeyId, String secretAccessKey, Region region) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(PollyConfig.ACESS_KEY, PollyConfig.SECRET_KEY);
        this.pollyClient = PollyClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public String synthesizeSpeech(String text, String outputFilePath) throws IOException {
        SynthesizeSpeechRequest synthesizeSpeechRequest = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId("Matthew")
                .outputFormat(OutputFormat.MP3)
                .build();

        try (ResponseInputStream<SynthesizeSpeechResponse> synthesizeSpeechResponse = pollyClient.synthesizeSpeech(synthesizeSpeechRequest);
             InputStream inputStream = synthesizeSpeechResponse;
             FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        File generatedFile = new File(outputFilePath);
        if (!generatedFile.exists() || generatedFile.length() == 0) {
            throw new IOException("Falha ao gerar o áudio.");
        }

        System.out.println("Áudio gerado com sucesso: " + outputFilePath);
        return outputFilePath;
    }


    public void close() {
        pollyClient.close();
    }
}