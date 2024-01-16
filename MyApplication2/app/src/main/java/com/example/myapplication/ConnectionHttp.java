package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.Environment;

//import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.file.Files;
import java.util.stream.Stream;



public class ConnectionHttp {

    public static void start() throws Exception {
        System.out.println("null");
        String port = "http://192.168.1.104:8888/test/";
        onSend(port, "drawable/file.png");
    }

    public static Bitmap byteToBitmap(byte[] bytes) throws IOException {
        Bitmap bitmap = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        bitmap = BitmapFactory.decodeStream(bis);
        bis.close();
        return bitmap;
    }

    public static void onSend(String path , String file) throws Exception {
        String text = "ClientRequest";

        // Установка URL-адреса для отправки изображения
        URL url = new URL(path);

        // Открытие соединения
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();


        // Установка метода запроса на POST
        connection.setRequestMethod("POST");

        // Установка заголовка потока
        //connection.setRequestProperty("User-Agent", "Client");
        connection.setRequestProperty("DeviceType", "Client");

        // Установка флага указывающего, что запрос будет содержать данные
        connection.setDoOutput(true);

        // Создание потока для записи данных в запрос
        OutputStream outputStream = connection.getOutputStream();

        //Чтение изображения из file и его преобразование в byte[]
        //connection.getHeaderField(text);
        File imageFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        FileInputStream fileInputStream = new FileInputStream(imageFile);
        byte[] imageData = Files.readAllBytes(imageFile.toPath());
        //byte[] imageData = new byte[(int) imageFile.length()];
        fileInputStream.read(imageData);

        // Запись изображения в тело запроса
        outputStream.write(imageData);

        // Закрытие соединения и потока
        outputStream.close();
        fileInputStream.close();

        connection.connect();
        // Получение HTTP-ответа
        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode);
        if(responseCode == HttpURLConnection.HTTP_OK){
            // Получение входного потока для чтения данных от сервера
            Thread.sleep(2000);
            InputStream is = connection.getInputStream();

            //Получение данных из потока байтов
            int imageLength = connection.getContentLength();
            byte[] niger = new byte[imageLength];
            System.out.println(niger.length);
            int bytesRead = 0;
            while (bytesRead < imageLength) {
                int n = is.read(niger, bytesRead,imageLength-bytesRead);
                //if(n<=0) ;
                bytesRead += n;
            }
            int x = is.read(niger);
            Bitmap bniger = BitmapFactory.decodeByteArray(niger, 0, niger.length);

            // Преобразование byte[] в Bitmap
            outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "result.jpg"));

            // Сохранение полученной картинки в файл
            bniger.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Закрываем входной поток и соединение
            outputStream.close();
            is.close();
            Thread.sleep(100);
            connection.disconnect();
        }
        else{
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
    }

}
