package org.menosprezo.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

public class CommandHandler {

    private final ExerciseGenerator exerciseGenerator = new ExerciseGenerator();

    private static final String WELCOME_MESSAGE = "Bem-vindo ao bot de treinamento de inglês!";
    private static final String HELP_MESSAGE = "Comandos disponíveis:\n/start - Inicia o bot\n/exercise - Receber um exercício\n/help - Mostrar ajuda";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Comando não reconhecido. Tente /help para ver a lista de comandos.";

    private static final Map<String, String> COMMAND_RESPONSES = Map.of(
            "/start", WELCOME_MESSAGE,
            "/help", HELP_MESSAGE
    );

    public SendMessage handleCommand(String command, String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (COMMAND_RESPONSES.containsKey(command.toLowerCase())) {
            message.setText(COMMAND_RESPONSES.get(command.toLowerCase()));
            return message;
        }

        String[] parts = command.split(" ");
        String baseCommand = parts[0];
        String parameter = parts.length > 1 ? parts[1] : "";

        switch (baseCommand.toLowerCase()) {
            case "/exercise":
                return handleExerciseCommand(chatId, parameter);
            default:
                message.setText(UNKNOWN_COMMAND_MESSAGE);
                return message;
        }
    }

    private SendMessage handleExerciseCommand(String chatId, String level) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        try {
            String difficulty = level.isEmpty() ? "fácil" : level;
            String exercise = exerciseGenerator.generateExercise(difficulty);
            message.setText("Aqui está seu exercício (" + difficulty + "):\n\n" + exercise);
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Erro ao gerar o exercício. Tente novamente.");
        }

        return message;
    }
}
