package sandeep.com.textrecognition;

import android.app.Application;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;

/**
 * Created by sandy on 5/3/2018.
 */

public class App extends Application {

    private static Vision vision;

    @Override
    public void onCreate() {
        super.onCreate();

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
               new VisionRequestInitializer("Your-API-Key"));

        vision = visionBuilder.build();
    }

    public static Vision getVisionClient(){
        return vision;
    }
}
