package project.mca.androidfilepicker;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MyFileUtil {

    private static final String TAG = "MY-APP";


    public static File createTempFile(Context context, Uri uri) {

        File tempFile = null;

        try {
            // DO FILE I/O IN A BACKGROUND THREAD

            InputStream is = context.getContentResolver().openInputStream(uri);

            // getApplicationContext().getCacheDir()        # App's temporary cache directory

            // create a temp file
            tempFile = File.createTempFile("bob", null, context.getCacheDir());

            FileOutputStream fos = new FileOutputStream(tempFile);

            long byteCopied = IOUtils.copyLarge(is, fos);


            Log.d(TAG, "original: " + (tempFile.length() / 1024) + "KB");

            // close both the streams when done
            is.close();
            fos.close();

            // delete the temp file at the end

        }
        catch (Exception ex) {
            Log.d(TAG, "Error: " + ex.getMessage());
        }

        // return the temp file
        return tempFile;
    }



    public static boolean closeTempFile(Context context, String fileName) {
        if (context != null) {
            return context.deleteFile(fileName);
        }

        return false;
    }
}
