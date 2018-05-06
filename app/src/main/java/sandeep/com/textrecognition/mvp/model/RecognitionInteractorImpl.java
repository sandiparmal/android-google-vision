package sandeep.com.textrecognition.mvp.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import sandeep.com.textrecognition.App;
import sandeep.com.textrecognition.mvp.presenter.RecognitionPresenterImpl;

/**
 * Created by sandy on 5/3/2018.
 */

public class RecognitionInteractorImpl implements RecognitionInteractor {
    /**
     * Extract text from image
     */
    @Override
    public void recognizeImage(final Bitmap bitmap, final RecognitionPresenterImpl recognitionPresenter) {


// Create new thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Image inputImage = new Image();
                inputImage.encodeContent(byteArray);

                Feature desiredFeature = new Feature();
                desiredFeature.setType("TEXT_DETECTION");
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));

                BatchAnnotateImagesRequest batchRequest =
                        new BatchAnnotateImagesRequest();

                batchRequest.setRequests(Arrays.asList(request));

                BatchAnnotateImagesResponse batchResponse =
                        null;
                try {
                    batchResponse = App.getVisionClient().images().annotate(batchRequest).execute();
                    final TextAnnotation text = batchResponse.getResponses()
                            .get(0).getFullTextAnnotation();
                    recognitionPresenter.onRecognizeSuccess(text.toString());
                } catch (IOException e) {
                    recognitionPresenter.onRecognizeFailure();
                }
            }
        });



    }
}
