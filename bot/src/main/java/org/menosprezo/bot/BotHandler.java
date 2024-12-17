package org.menosprezo.bot;

import org.menosprezo.config.BotConfig;
import org.menosprezo.config.PollyConfig;
import org.menosprezo.services.MessageService;
import org.menosprezo.services.TextToSpeechService;
import org.menosprezo.utils.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BotHandler extends TelegramLongPollingBot {

    private final MessageService messageService = new MessageService();
    private final CommandHandler commandHandler = new CommandHandler();
    private static final String DOWNLOADS_DIR = Paths.get(System.getProperty("user.dir"), "downloads").toString();
    private final TextToSpeechService textToSpeechService = new TextToSpeechService(PollyConfig.ACESS_KEY, PollyConfig.SECRET_KEY, PollyConfig.REGION);


    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                String chatId = update.getMessage().getChatId().toString();

                if (update.getMessage().hasText()) {
                    String messageText = update.getMessage().getText();

                    if (messageText != null && messageText.startsWith("/")) {
                        SendMessage response = commandHandler.handleCommand(messageText, chatId);
                        execute(response);
                    } else {
                        handleTextMessage(chatId, messageText);
                    }
                }

                else if (update.getMessage().hasVoice()) {
                    String fileId = update.getMessage().getVoice().getFileId();
                    handleVoiceMessage(chatId, fileId);
                }

                else {
                    SendMessage unsupportedMessage = new SendMessage(chatId, "Tipo de mensagem não suportado. Envie texto ou áudio.");
                    execute(unsupportedMessage);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleTextMessage(String chatId, String userMessage) throws TelegramApiException {
        if ("/start".equals(userMessage)) {
            sendWelcomeMessage(chatId);
        } else {
            SendMessage response = messageService.processMessage(userMessage, chatId);
            execute(response);
        }
    }

    private void handleVoiceMessage(String chatId, String fileId) {
        try {
            FileUtils.createDirectoryIfNotExists(Path.of(DOWNLOADS_DIR));

            String filePath = execute(new GetFile(fileId)).getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
            String localPath = Paths.get(DOWNLOADS_DIR, fileId + ".ogg").toString();

            File audioFile = FileUtils.downloadFile(fileUrl, localPath);
            String botResponse = messageService.processVoiceMessage(audioFile, chatId);

            // Extrair "Resposta" e gerar áudio
            String responseForAudio = extractResponse(botResponse);
            String audioFilePath = "downloads/response.mp3";
            textToSpeechService.synthesizeSpeech(responseForAudio, audioFilePath);

            sendAudioResponse(chatId, botResponse, audioFilePath);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                SendMessage errorMessage = new SendMessage(chatId, "Erro ao processar o áudio. Por favor, tente novamente.");
                execute(errorMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String extractResponse(String botResponse) {
        int startIndex = botResponse.indexOf("Resposta:");
        if (startIndex != -1) {
            return botResponse.substring(startIndex + 9).trim(); // Remove "Resposta:" e aplica trim
        }
        return botResponse; // Retorna a resposta completa caso o campo não seja encontrado
    }

    private void sendAudioResponse(String chatId, String botResponse, String audioFilePath) throws TelegramApiException {
        File audioFile = new File(audioFilePath);
        if (!audioFile.exists() || audioFile.length() == 0) {
            throw new TelegramApiException("Erro: Arquivo de áudio não gerado corretamente.");
        }

        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setAudio(new InputFile(audioFile));
        execute(sendAudio);

        SendMessage responseMessage = new SendMessage(chatId, botResponse);
        execute(responseMessage);
    }


    private void sendWelcomeMessage(String chatId) throws TelegramApiException {
        SendMessage welcomeMessage = new SendMessage(chatId, "Bem-vindo ao TryIA! Envie uma mensagem ou áudio para começar.");
        execute(welcomeMessage);
    }
}
