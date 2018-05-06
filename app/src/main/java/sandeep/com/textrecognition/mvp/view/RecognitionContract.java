package sandeep.com.textrecognition.mvp.view;

import android.graphics.Bitmap;

import sandeep.com.textrecognition.mvp.base.BasePresenter;
import sandeep.com.textrecognition.mvp.base.BaseView;

/**
 * Created by sandy on 5/3/2018.
 */

public interface RecognitionContract {

    public interface RecognitionView extends BaseView {
        void showExtractedText(String extractedText);
    }

    public interface RecognitionPresenter extends BasePresenter {
        void getTextFromImage(Bitmap bitmap);
    }
}
