package sandeep.com.textrecognition.mvp.base;

/**
 * Created by sandy on 5/3/2018.
 */

public interface BaseView {

    /**
     * Show progress dialog while fetching details
     */
    void showWait();

    /**
     * hide progress dialog when fetching complete or error occur
     */
    void hideWait();

}
