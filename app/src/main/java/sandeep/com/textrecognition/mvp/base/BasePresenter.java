package sandeep.com.textrecognition.mvp.base;

import sandeep.com.textrecognition.mvp.view.RecognitionContract;

/**
 * Created by sandy on 5/3/2018.
 */

public interface BasePresenter {

    /**
     * Called when the view is created and wants to attach its presenter
     */
    void onAttach(RecognitionContract.RecognitionView recognitionView);

    /**
     * Called when the view is destroyed to get rid of its presenter
     */
    void detach();
}
