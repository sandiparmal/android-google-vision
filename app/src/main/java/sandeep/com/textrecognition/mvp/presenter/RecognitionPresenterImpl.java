package sandeep.com.textrecognition.mvp.presenter;

import android.graphics.Bitmap;

import sandeep.com.textrecognition.mvp.model.RecognitionInteractor;
import sandeep.com.textrecognition.mvp.view.RecognitionContract;

/**
 * Created by sandy on 5/3/2018.
 */

public class RecognitionPresenterImpl implements RecognitionContract.RecognitionPresenter, RecognitionInteractor.OnRecognizeFinishListener {

    private RecognitionContract.RecognitionView recognitionView;
    private RecognitionInteractor recognitionInteractor;

    public RecognitionPresenterImpl(RecognitionInteractor recognitionInteractor){
        this.recognitionInteractor = recognitionInteractor;;
    }

    @Override
    public void onRecognizeSuccess(String extractedText) {
        recognitionView.hideWait();
        recognitionView.showExtractedText(extractedText);
    }

    @Override
    public void onRecognizeFailure() {
        recognitionView.hideWait();
        recognitionView.showExtractedText("Fail");
    }

    /**
     * Called when the view is created and wants to attach its presenter
     *
     * @param recognitionView RecognitionContract.RecognitionView
     */
    @Override
    public void onAttach(RecognitionContract.RecognitionView recognitionView) {
        this.recognitionView = recognitionView;
    }

    /**
     * Called when the view is destroyed to get rid of its presenter
     */
    @Override
    public void detach() {
        if(recognitionView != null){
            recognitionView = null;
        }
    }

    @Override
    public void getTextFromImage(Bitmap bitmap) {
        recognitionInteractor.recognizeImage(bitmap, this);
    }
}
