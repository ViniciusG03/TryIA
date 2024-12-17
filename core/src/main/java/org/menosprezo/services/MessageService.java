package org.menosprezo.services;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService {
    private final OpenAiService openAiService = new OpenAiService();
    private final WhisperService whisperService = new WhisperService();

    // Histórico de conversas por usuário
    private final Map<String, List<JSONObject>> userConversations = new HashMap<>();

    private final String systemPrompt = "Você é um professor de inglês. Sua tarefa é corrigir os erros gramaticais, " +
            "explicar detalhadamente o motivo da correção, traduzir a frase para português e continuar a conversa " +
            "de forma amigável em inglês. Sempre use o seguinte formato para sua resposta:" +
            "\nCorreção: [frase corrigida]\n" +
            "\nExplicação: [explique o que foi corrigido e o porquê]\n" +
            "\nTradução: [tradução para o português]\n" +
            "\nResposta: [uma resposta amigável e relevante somente em inglês].";

    public SendMessage processMessage(String userMessage, String chatId) {
        if (userMessage == null || chatId == null) {
            throw new IllegalArgumentException("userMessage e chatId não podem ser nulos.");
        }

        userConversations.putIfAbsent(chatId, new ArrayList<>());
        List<JSONObject> conversationHistory = userConversations.get(chatId);

        if (conversationHistory.isEmpty()) {
            conversationHistory.add(new JSONObject().put("role", "system").put("content", systemPrompt));
        }

        try {
            conversationHistory.add(new JSONObject().put("role", "user").put("content", userMessage));
            if (conversationHistory.size() > 20) {
                conversationHistory.remove(1);
            }

            String botResponse = openAiService.getResponseFromOpenAI(conversationHistory);
            conversationHistory.add(new JSONObject().put("role", "assistant").put("content", botResponse));

            return new SendMessage(chatId, botResponse);
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem para chat ID: " + chatId);
            e.printStackTrace();
            return new SendMessage(chatId, "Erro ao processar sua mensagem. Tente novamente.");
        }
    }


    public String processMessageText(String transcribedText, String chatId) {
        try {
            // Reutiliza o método processMessage para processar o texto
            SendMessage response = processMessage(transcribedText, chatId);
            return response.getText(); // Retorna apenas o texto da resposta
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao processar a mensagem transcrita.";
        }
    }


    public String processVoiceMessage(File audioFile, String chatId) {
        if (!audioFile.exists() || audioFile.length() == 0) {
            return "Erro: Arquivo de áudio inválido ou inexistente.";
        }

        try {
            // Transcreve o áudio
            String transcribedText = whisperService.transcribe(audioFile);

            // Processa o texto transcrito
            SendMessage response = processMessage(transcribedText, chatId);
            return response.getText();
        } catch (IOException e) {
            System.err.println("Erro ao transcrever áudio: " + e.getMessage());
            e.printStackTrace();
            return "Erro ao processar o áudio.";
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            return "Erro inesperado ao processar o áudio.";
        }
    }
}
