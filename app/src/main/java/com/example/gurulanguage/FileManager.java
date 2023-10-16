package com.example.gurulanguage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileManager {
    Context ctx;
    File bookFolder,offlineJson;
    JSONObject offlineData;
    byte[] BUFFER_SIZE;
    OkHttpClient client;
    FileManager(Context context){
        ctx = context;
        bookFolder = new File(ctx.getFilesDir(), "books");
        offlineJson = new File(bookFolder,"offline.json");
        client = getUnsafeOkHttpClient();
        if(!bookFolder.exists()){
            initializeBookFolder();
        }else{
            try {
                offlineData = new JSONObject(readFromFile(offlineJson));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void initializeBookFolder(){
        try {
            bookFolder.mkdir();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("books", new JSONObject());
            offlineData = jsonObject;
            updateOfflineJson(jsonObject);
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOfflineJson(JSONObject jsonObject){
        try {
            FileWriter writer = new FileWriter(offlineJson);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String getData(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBitmap(String posterPath){
        try {
            File poster = new File(posterPath);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(poster));
            return b;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void download(String fileUrl, String fileName, String filePath,final FileSuccess callback){
        BUFFER_SIZE = new byte[1024];


        Request request = new Request.Builder()
                .url(fileUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }
                File dir=new File(ctx.getFilesDir(), filePath);

                if(!dir.exists()){
                    dir.mkdirs();
                }
                int length;
                InputStream fileData = response.body().byteStream();
                if(fileData!=null){
                    try {
                        FileOutputStream outputStream = new FileOutputStream(new File(dir, fileName));
                        long totalFileSize = response.body().contentLength();
                        long totalBytesRead = 0;
                        while((length = fileData.read(BUFFER_SIZE)) > 0){
                            outputStream.write(BUFFER_SIZE,0,length);
                            totalBytesRead += length;

                            int progress = (int) ((totalBytesRead * 100) / totalFileSize);
                            callback.onProgress(progress);
                        }

                        fileData.close();

                        callback.onSuccess();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    public static String readFromFile(File file) {
        StringBuilder books = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                books.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books.toString();
    }
    public String getTab(String book_id, int tab_id){
        try {
            if (tab_id == 0) offlineData = new JSONObject(readFromFile(offlineJson));
            JSONObject books = offlineData.getJSONObject("books");
            JSONObject book = books.getJSONObject(book_id);
            String bookPath = book.getString("path");
            File to = new File(bookFolder,bookPath+"/tab_"+tab_id+".txt");
            return readFromFile(to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public void setLastReadingBook(String id){
        try {
            offlineData.put("continueReading", id);
            updateOfflineJson(offlineData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    public String getBookDescription(String id, String url){
        try {
            JSONObject books_data = offlineData.getJSONObject("books");
            if(books_data.has(id)){
                String bookPath = books_data.getJSONObject(id).getString("path");
                File to = new File(bookFolder,bookPath+"/description.txt");
                return readFromFile(to);
            }else{
                return getData(url);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
    public void unzipBook(String _zipFile) {
        try {
            File to = new File(bookFolder, _zipFile + ".zip");
            File unzip_path = new File(bookFolder,_zipFile);
            if(!unzip_path.exists()){
                unzip_path.mkdirs();
            }
            FileInputStream fin = new FileInputStream(to);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                FileOutputStream fout = new FileOutputStream(new File(unzip_path, ze.getName()));
                byte b[] = new byte[1024];
                int n;
                while ((n = zin.read(b)) != -1) {
                    fout.write(b,0,n);
                }
                zin.closeEntry();
                fout.close();
            }
            zin.close();
            to.delete();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void downloadBook(String id,String lang, FileSuccess callback){
        Request request = new Request.Builder()
                .url("http://english.ho.ua/get_info.php?id="+id+"&lang="+lang)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    JSONObject bookData = new JSONObject(result);
                    String image = bookData.getString("poster");
                    String bookPath = bookData.getString("path");
                    download("https://english.ho.ua/admin/books/"+ bookPath + "/" + bookPath + ".zip", bookPath + ".zip", "books", new FileSuccess() {
                        @Override
                        public void onSuccess() {
                            unzipBook(bookPath);
                            try {
                                FileWriter writeDescription = new FileWriter(new File(bookFolder,bookPath+"/description.txt"));
                                writeDescription.write(getBookDescription(id, "http://english.ho.ua/admin/books/" + bookPath + "/description_" + lang + ".txt"));
                                writeDescription.flush();
                                writeDescription.close();

                                bookData.put("poster", new File(ctx.getFilesDir(), image).getAbsolutePath());
                                bookData.put("lastTab", 0);
                                offlineData.getJSONObject("books").put(String.valueOf(id),bookData);

                                updateOfflineJson(offlineData);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setLastReadingBookTab(String book_id, Integer tab_id){
        try {
            JSONObject book = offlineData.getJSONObject("books").getJSONObject(book_id);
            if(book.getInt("lastTab") < tab_id) {
                book.put("lastTab", tab_id);
                updateOfflineJson(offlineData);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public String continueReading(){
        String continueReadingData = "empty";
        try {
            if(offlineData.has("continueReading")){
                continueReadingData = String.valueOf(getBook(offlineData.getString("continueReading")));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return continueReadingData;
    }
    public boolean hasBook(String id){
        try {
            if(offlineData.getJSONObject("books").has(id))return true;
            else return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public JSONObject getBook(String id){
        try {
            JSONObject books = offlineData.getJSONObject("books");
            return books.getJSONObject(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getBooks(String url) {
        String data = getData(url);
        if (data == null) {
            try {
                JSONObject booksObj = offlineData.getJSONObject("books"); // Отримуємо об'єкт books з вихідного JSON об'єкту
                JSONArray booksArray = new JSONArray(); // Створюємо пустий масив JSON об'єктів

                // Ітеруємося по всіх книгах у об'єкті books
                Iterator<String> keys = booksObj.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    JSONObject book = booksObj.getJSONObject(key);
                    booksArray.put(book); // Додаємо об'єкт книги до масиву JSON об'єктів
                }

                data = booksArray.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return data;
    }
    public interface FileSuccess{
        void onSuccess();
        void onFailure();
        void onProgress(int progress);
    }
}
