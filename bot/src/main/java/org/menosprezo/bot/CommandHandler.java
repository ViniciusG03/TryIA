package org.menosprezo.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

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

        message.setText("Comando não reconhecido. Use /help para ver os comandos disponíveis.");
        return message;
    }

    public SendMessage validateCallbackAnswer(String chatId, String userAnswer) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (activeExercises.containsKey(chatId)) {
            String correctAnswer = activeExercises.get(chatId);

            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                message.setText("✅ Parabéns! Sua resposta está correta.");
            } else {
                message.setText("❌ Resposta incorreta. A resposta correta é: " + correctAnswer);
            }
            activeExercises.remove(chatId);
        } else {
            message.setText("Nenhum exercício ativo encontrado. Use /exercise para gerar um novo.");
        }

        return message;
    }

    public SendMessage handleExerciseCommand(String chatId, String level) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        try {
            String[] exerciseData = exerciseGenerator.generateExercise(level);
            String question = exerciseData[0];
            String[] options = exerciseData[1].split("/");
            String correctAnswer = exerciseData[2]; // Resposta correta

            // Embaralha as opções
            List<String> optionsList = Arrays.asList(options);
            Collections.shuffle(optionsList);

            message.setText("Aqui está seu exercício:\n" + question);

            // Configura os botões inline
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for (String option : optionsList) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(option.trim());
                button.setCallbackData(option.trim());
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(button);
                rows.add(row);
            }

            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);

            // Salva a resposta correta
            activeExercises.put(chatId, correctAnswer.trim());

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Erro ao gerar exercício. Tente novamente.");
        }
        return message;
    }
}
