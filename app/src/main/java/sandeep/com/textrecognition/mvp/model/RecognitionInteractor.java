package sandeep.com.textrecognition.mvp.model;

import android.graphics.Bitmap;

import sandeep.com.textrecognition.mvp.presenter.RecognitionPresenterImpl;

/**
 * Created by sandy on 5/3/2018.
 */

public interface RecognitionInteractor {

    interface OnRecognizeFinishListener {
        void onRecognizeSuccess(String extractedText);

        void onRecognizeFailure();
    }

    /**
     * Extract text from image
     */
    void recognizeImage(Bitmap bitmap, RecognitionPresenterImpl recognitionPresenter);
}
