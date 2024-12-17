package org.menosprezo.bot;

import org.menosprezo.config.BotConfig;
import org.menosprezo.config.PollyConfig;
import org.menosprezo.services.MessageService;
import org.menosprezo.services.TextToSpeechService;
import org.menosprezo.services.WhisperService;
import org.menosprezo.utils.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageProcessor extends TelegramLongPollingBot {

    private final String BOT_USERNAME = BotConfig.BOT_USERNAME;
    private final String BOT_TOKEN = BotConfig.BOT_TOKEN;
    private final MessageService messageHandler = new MessageService();
    private final WhisperService whisperService = new WhisperService();
    private final TextToSpeechService textToSpeechService = new TextToSpeechService(PollyConfig.ACESS_KEY, PollyConfig.SECRET_KEY, PollyConfig.REGION);

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String chatId = update.getMessage().getChatId().toString();

            try {
                if (update.getMessage().hasText()) {
                    String messageText = update.getMessage().getText();
                    SendMessage response = messageHandler.processMessage(messageText, chatId);
                    execute(response);
                }

                else if (update.getMessage().hasVoice()) {
                    processVoiceMessage(update.getMessage().getVoice().getFileId(), chatId);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void processVoiceMessage(String fileId, String chatId) {
        try {
            SendMessage processingMessage = new SendMessage(chatId, "Recebi seu áudio! Estou processando, por favor, aguarde...");
            execute(processingMessage);

            Path downloadPath = Paths.get("downloads");
            Files.createDirectories(downloadPath);

            String filePath = execute(new GetFile(fileId)).getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
            String localPath = downloadPath.resolve(fileId + ".ogg").toString();

            File downloadedFile = FileUtils.downloadFile(fileUrl, localPath);

            if (!downloadedFile.exists() || downloadedFile.length() == 0) {
                throw new IOException("Falha ao baixar o arquivo de áudio.");
            }

            String transcribedText = whisperService.transcribe(downloadedFile);

            String botResponse = messageHandler.processMessageText(transcribedText, chatId);

            // Extrair apenas o campo "Resposta"
            String responseForAudio = extractResponse(botResponse);

            String audioFilePath = "downloads/response.mp3";
            textToSpeechService.synthesizeSpeech(responseForAudio, audioFilePath);

            sendAudioResponse(chatId, botResponse, audioFilePath);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                SendMessage errorMessage = new SendMessage(chatId, "Desculpe, ocorreu um erro ao processar o áudio. Por favor, tente novamente.");
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

        // Enviar o texto completo da resposta
        SendMessage responseMessage = new SendMessage(chatId, botResponse);
        execute(responseMessage);
    }

}
