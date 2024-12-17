package org.menosprezo.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    public static void createDirectoryIfNotExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            System.out.println("Diretório criado: " + directory.toAbsolutePath());
        } else {
            System.out.println("Diretório já existe: " + directory.toAbsolutePath());
        }
    }

    public static File downloadFile(String fileUrl, String localPath) throws IOException {
        System.out.println("Baixando arquivo de: " + fileUrl);
        System.out.println("Salvando em: " + localPath);

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configurações de timeout
        connection.setConnectTimeout(10000); // 10 segundos para conexão
        connection.setReadTimeout(15000);   // 15 segundos para leitura

        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, Path.of(localPath), StandardCopyOption.REPLACE_EXISTING);
        } finally {
            connection.disconnect();
        }

        return new File(localPath);
    }
}
