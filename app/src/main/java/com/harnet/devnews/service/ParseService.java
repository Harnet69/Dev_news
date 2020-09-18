package com.harnet.devnews.service;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ParseService {
    private WebContentDownloader webContentDownloader = new WebContentDownloader();

    //get list of articles URL
    public List<URL> getArticlesURLs(String url){
        List<URL> articlesURLs = new ArrayList<>();

        // get content from a page
        String content = null;
        try {
            content =  new WebContentDownloader().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // get articles id
        if(content != null){
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i=0; i< jsonArray.length(); i++){
                    int articleId = Integer.parseInt(jsonArray.getString(i));
                    // TODO think about do it final or move to ENUM
                    URL atricleURL = new URL("https://hacker-news.firebaseio.com/v0/item/"+ articleId + ".json?print=pretty");
                    articlesURLs.add(atricleURL);
                }
            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            throw new RuntimeException("Can't get a content");
        }

        return articlesURLs;
    }

    // get an article details
    public String parse(String urlString){
        StringBuilder site = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char current = (char) data;
                site.append(current);
                data = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return site.toString();
    }
}
