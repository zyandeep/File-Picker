package project.mca.androidfilepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MY-APP";
    private static final int FILE_REQUEST_CODE1 = 1;
    private static final int FILE_REQUEST_CODE2 = 2;
    private static final int FILE_REQUEST_CODE3 = 3;


    ImageView imageView1, imageView2, imageView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.image_view1);
        imageView2 = findViewById(R.id.image_view2);
        imageView3 = findViewById(R.id.image_view3);
    }


    public void chooseFile(View view) {
        // using SAF
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});


        if (view.getId() == R.id.button1) {
            startActivityForResult(intent, FILE_REQUEST_CODE1);
        }
        else if (view.getId() == R.id.button2) {
            startActivityForResult(intent, FILE_REQUEST_CODE2);
        }
        else if (view.getId() == R.id.button3) {
            startActivityForResult(intent, FILE_REQUEST_CODE3);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK & data != null) {

            Uri uri = data.getData();

            if (getContentResolver().getType(uri).equals("application/pdf")) {
                Toast.makeText(this, "Not done...", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (requestCode) {
                case FILE_REQUEST_CODE1:
                    executeAsyncTask(uri, FILE_REQUEST_CODE1);
                    break;

                case FILE_REQUEST_CODE2:
                    executeAsyncTask(uri, FILE_REQUEST_CODE2);
                    break;

                case FILE_REQUEST_CODE3:
                    executeAsyncTask(uri, FILE_REQUEST_CODE3);
                    break;
            }
        }
    }


    private void executeAsyncTask(Uri uri, final int requestCode) {
        // run the async task to generate the temp file

        new AsyncTask<Uri, Void, File>() {
            @Override
            protected File doInBackground(Uri... uris) {
                String mimeType = getContentResolver().getType(uris[0]);

                Log.d(TAG, "uri: " + uris[0]);
                Log.d(TAG, "mime type: " + mimeType);

                // get the temp file
                // and compress it
                File tempFile = null;

                try {
                    tempFile = new Compressor(getApplicationContext())
                            .compressToFile(
                                    MyFileUtil.createTempFile(getApplicationContext(), uris[0])
                            );
                }
                catch (Exception ex) { }

                return tempFile;            // return the compressed file
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);

                displayImage(file, requestCode);
            }

        }.execute(uri);
    }


    private void displayImage(File imageFile, int requestCode) {

        Log.d(TAG, "compressed: " + (imageFile.length() / 1024) + "KB");

        if (imageFile != null) {
            // choose the imageView
            ImageView imageView = null;

            if (requestCode == FILE_REQUEST_CODE1) {
                imageView = imageView1;
            }
            else if (requestCode == FILE_REQUEST_CODE2) {
                imageView = imageView2;
            }
            else if (requestCode == FILE_REQUEST_CODE3) {
                imageView = imageView3;
            }


            //Loading image from file location into imageView
            Glide.with(MainActivity.this)
                    .load(imageFile)
                    .override(200, 200)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(imageView);
        }
    }
}