package coursera.khanacademy.martya.numorder;

import android.app.Application;
import android.os.Build;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An application app that handles application level stuff for Falling Numbers
 */
public class MainApplication extends Application {

    private Tracker tracker;
//    private TagManager tagManager;
//    private ContainerHolder containerHolder;
//
//    private List<Runnable> onContainerHolderInitRunnables = new ArrayList<>();

    public boolean startTracking() {
        if (tracker == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
                return false;

            GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
            this.tracker = googleAnalytics.newTracker(R.xml.track_app);

            googleAnalytics.enableAutoActivityReports(this);

        }

        return true;

//        if (tagManager == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            this.tagManager = TagManager.getInstance(this);
//
//            tagManager.loadContainerPreferFresh(
//                    getString(R.string.tag_manager_tracking_id),
//                    R.raw.gmt_default
//            ).setResultCallback(new ResultCallback<ContainerHolder>() {
//                @Override
//                public void onResult(ContainerHolder containerHolder) {
//                    if (!containerHolder.getStatus().isSuccess())
//                        return;
//
//                    containerHolder.refresh(); // Take this out when publishing, I think
//
//                    MainApplication.this.containerHolder = containerHolder;
//
//                    for (Runnable runnable : onContainerHolderInitRunnables)
//                        runnable.run();
//
//                }
//            }, 2, TimeUnit.SECONDS);
//
//        }

    }

//    public void startTracking(Runnable runnable) {
//        if (containerHolder == null) {
//            onContainerHolderInitRunnables.add(runnable);
//
//            startTracking();
//        }
//        else
//            runnable.run();
//
//    }

    public void sendScreenView(final String screenName) {
//        startTracking(new Runnable() {
//            @Override
//            public void run() {
//                tagManager.getDataLayer().pushEvent("Change Screen", DataLayer.mapOf("screen-name", screenName));
//            }
//        });

        if (startTracking()) {
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

    }

    public void stopScreenView() {
        if (startTracking())
            tracker.setScreenName(null);

    }

    public void sendEvent(String category, String action, String label) {
        if (startTracking()) {
            tracker.send(new HitBuilders.EventBuilder()
                            .setCategory(category)
                            .setAction(action)
                            .setLabel(label)
                            .build()
            );
        }

    }

//    public void sendEvent(final String event) {
//        startTracking(new Runnable() {
//            @Override
//            public void run() {
//                tagManager.getDataLayer().pushEvent(event, DataLayer.mapOf());
//            }
//        });
//    }

//    public void putInDataLayer(final String key, final String value) {
//        startTracking(new Runnable() {
//            @Override
//            public void run() {
//                tagManager.getDataLayer().push(key, value);
//            }
//        });
//    }

}
