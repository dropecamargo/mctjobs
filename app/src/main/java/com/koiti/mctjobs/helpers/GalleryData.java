package com.koiti.mctjobs.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.models.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GalleryData {

    private Context context;

    public GalleryData(Context context){
        this.context = context;
    }

    public ArrayList<Image> getImagesGallery( Step step ) {
        ArrayList<Image> imagesArray = new ArrayList<Image>();
        Uri gallery_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if(gallery_uri != null){
            String where = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? AND "
                    + MediaStore.Images.Media.DISPLAY_NAME + " LIKE ? ";
            String[] args = { Constants.GALLERY_NAME, ( step.getId_work() + "-" + step.getId() + "-%" ) };

            String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN };
            Cursor cursor = context.getContentResolver().query(gallery_uri ,projection, where, args, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            if(cursor != null){
                if (cursor.moveToFirst()) {
                    int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    int creationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                    do {
                        String name = cursor.getString(nameColumn);
                        String album = cursor.getString(albumColumn);
                        String path = cursor.getString(pathColumn);
                        Long creation = cursor.getLong(creationColumn);

                        Image image = new Image(name, album, path, true);
                        image.setCreation(creation);
                        imagesArray.add(image);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        return imagesArray;
    }

    public void createGallery() {
        File gallery_dir = Environment.getExternalStoragePublicDirectory(Constants.GALLERY_NAME);
        if (gallery_dir != null) {
            if(!gallery_dir.isDirectory()){
                gallery_dir.mkdirs();
            }
        }
    }

    public String getFileName(Step step) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        return step.getId_work() + "-" + step.getId() + "-" + timeStamp;
    }

    public File createImageFile(Step step) throws IOException {
        // Valid gallery
        GalleryData.this.createGallery();

        // Create an image file name
        File storageDir = Environment.getExternalStoragePublicDirectory( Constants.GALLERY_NAME );
        File file = new File( storageDir, getFileName(step));

        return file;
    }

    public File createTempImageFile(Step step) throws IOException {
        // Valid gallery
        GalleryData.this.createGallery();

        // File name
        String name = getFileName(step);

        // Create an image file name
        File storageDir = Environment.getExternalStoragePublicDirectory( Constants.GALLERY_NAME );
        File image = File.createTempFile(name, Constants.JPEG_FILE_SUFFIX, storageDir);
        return image;
    }

    public String getRealPathFromURI(Uri currImageURI) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(currImageURI ,proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
