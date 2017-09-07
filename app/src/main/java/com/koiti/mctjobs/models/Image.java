package com.koiti.mctjobs.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.Base64;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Image {
    private String name;
    private String album;
    private String path;
    private boolean controled;
    private Long creation;

    public Image (String name, String album, String path, Boolean controled) {
        this.name = name;
        this.album = album;
        this.path = path;
        this.controled = controled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isControled() {
        return controled;
    }

    public void setControled(boolean controled) {
        this.controled = controled;
    }

    public String getCreation() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(creation);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  dateFormat.format(calendar.getTime());
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public static String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
