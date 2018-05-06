package sandeep.com.textrecognition;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sandeep.com.textrecognition.mvp.base.BaseActivity;
import sandeep.com.textrecognition.mvp.model.RecognitionInteractorImpl;
import sandeep.com.textrecognition.mvp.presenter.RecognitionPresenterImpl;
import sandeep.com.textrecognition.mvp.view.RecognitionContract;

public class MainActivity extends BaseActivity implements RecognitionContract.RecognitionView, View.OnClickListener {

    private static final int PERMISSION_ALL = 1;
    public static final int REQUEST_CODE_IMAGE_CHOOSER = 200;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private Uri picUri;
    private ImageButton imbCapture;
    private ImageView imvImage;
    private RecognitionContract.RecognitionPresenter mRecognitionPresenter;


    /**
     * Layout resource to be inflated
     *
     * @return layout resource
     */
    @Override
    protected int getContentResource() {
        return R.layout.activity_main;
    }

    /**
     * Initialisations
     *
     * @param savedState
     */
    @Override
    protected void init(Bundle savedState) {

        imbCapture = (ImageButton) findViewById(R.id.imbCapture);
        imvImage = (ImageView) findViewById(R.id.imageView);

        imbCapture.setOnClickListener(this);

        // Initialize Presenter
        mRecognitionPresenter = new RecognitionPresenterImpl(new RecognitionInteractorImpl());
        mRecognitionPresenter.onAttach(this);

    }

    /**
     * Show progress dialog while fetching details
     */
    @Override
    public void showWait() {

    }

    /**
     * hide progress dialog when fetching complete or error occur
     */
    @Override
    public void hideWait() {

    }

    void openCamera() {
        if (hasPermissions(MainActivity.this, PERMISSIONS)) {
            startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE_IMAGE_CHOOSER);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Create a chooser intent to select the source to get image from.
     * <p>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).
     * <p>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CODE_IMAGE_CHOOSER) {
                if (getPickImageResultUri(data) != null) {
                    Log.d("Crop", "Gallery");
                    picUri = getPickImageResultUri(data);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                        imvImage.setImageBitmap(bitmap);

                        mRecognitionPresenter.getTextFromImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("Crop", "Camera");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imvImage.setImageBitmap(bitmap);
                    mRecognitionPresenter.getTextFromImage(bitmap);
                }
            }
        }
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.
     * <p>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    public void showExtractedText(final String extractedText) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Extracted text : " + extractedText, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.imbCapture :
                openCamera();
                break;
        }
    }
}
