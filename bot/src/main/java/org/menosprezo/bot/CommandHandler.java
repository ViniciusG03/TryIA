package org.menosprezo.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommandHandler {

    private static final String WELCOME_MESSAGE = "Bem-vindo ao bot de treinamento de inglês!";
    private static final String HELP_MESSAGE = "Comandos disponíveis:\n/start - Inicia o bot\n/exercise - Receber um exercício\n/help - Mostrar ajuda";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Comando não reconhecido. Tente /help para ver a lista de comandos.";

    private static final List<String> EXERCISES = List.of(
            "Complete a frase: 'I ___ to the park every day.' (go/goes/going)",
            "Escolha a opção correta: 'She always ___ coffee in the morning.' (drink/drinks/drinking)",
            "Complete a frase: 'We ___ at the library yesterday.' (was/were)",
            "Traduza para o inglês: 'Eu gosto de estudar inglês todos os dias.'",
            "Escolha a opção correta: 'They ___ football on weekends.' (play/plays)"
    );

    private static final Map<String, String> COMMAND_RESPONSES = Map.of(
            "/start", "Bem-vindo ao bot de treinamento de inglês!",
            "/help", "Comandos disponíveis:\n/start - Inicia o bot\n/exercise - Receber um exercício\n/help - Mostrar ajuda"
    );

    public SendMessage handleCommand(String command, String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        // Verifica se o comando está no mapa
        if (COMMAND_RESPONSES.containsKey(command.toLowerCase())) {
            message.setText(COMMAND_RESPONSES.get(command.toLowerCase()));
            return message;
        }

        // Lida com comandos específicos fora do mapa
        String[] parts = command.split(" ");
        String baseCommand = parts[0];
        String parameter = parts.length > 1 ? parts[1] : "";

        switch (baseCommand.toLowerCase()) {
            case "/exercise":
                if (parameter.equalsIgnoreCase("easy")) {
                    message.setText("Exercício fácil: Complete a frase: 'He ___ at home.' (is/are)");
                } else if (parameter.equalsIgnoreCase("hard")) {
                    message.setText("Exercício difícil: Escolha a opção correta: 'They ___ going to the park.' (is/are)");
                } else {
                    message.setText("Aqui está seu exercício:\n" + getRandomExercise());
                }
                break;
            default:
                message.setText("Comando não reconhecido. Tente /help para ver a lista de comandos.");
                break;
        }

        return message;
    }


    private String getRandomExercise() {
        Random random = new Random();
        return EXERCISES.get(random.nextInt(EXERCISES.size()));
    }
}
