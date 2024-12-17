package org.menosprezo.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final ExerciseGenerator exerciseGenerator = new ExerciseGenerator();
    private final Map<String, String> activeExercises = new HashMap<>();

    public SendMessage handleCommand(String command, String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (command.equalsIgnoreCase("/start")) {
            message.setText("Bem-vindo ao bot de treinamento de inglês! Use /exercise para começar.");
            return message;
        }

        if (command.toLowerCase().startsWith("/exercise")) {
            String level = command.split(" ").length > 1 ? command.split(" ")[1] : "fácil";
            return handleExerciseCommand(chatId, level);
        }

        // Verificar se é uma tentativa de resposta
        if (activeExercises.containsKey(chatId)) {
            String correctAnswer = activeExercises.get(chatId);
            return validateAnswer(command, correctAnswer, chatId);
        }

        message.setText("Comando não reconhecido. Use /help para ver os comandos disponíveis.");
        return message;
    }

    private SendMessage handleExerciseCommand(String chatId, String level) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        try {
            String[] exerciseData = exerciseGenerator.generateExercise(level);
            String cleanExercise = exerciseData[0];
            String correctAnswer = exerciseData[1];

            if (correctAnswer != null) {
                activeExercises.put(chatId, correctAnswer); // Salva a resposta correta
                message.setText("Aqui está seu exercício (" + level + "):\n" + cleanExercise);
            } else {
                message.setText("Erro ao gerar o exercício. Tente novamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Erro ao gerar o exercício. Tente novamente.");
        }

        return message;
    }


    private SendMessage validateAnswer(String userAnswer, String correctAnswer, String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (userAnswer.trim().equalsIgnoreCase(correctAnswer)) {
            message.setText("✅ Parabéns! Sua resposta está correta.");
        } else {
            message.setText("❌ Resposta incorreta. A resposta correta é: " + correctAnswer);
        }

        activeExercises.remove(chatId); // Remove o exercício ativo
        return message;
    }
}
