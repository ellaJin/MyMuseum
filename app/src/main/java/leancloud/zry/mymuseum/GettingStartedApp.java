package leancloud.zry.mymuseum;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;

/**
 * Created by BinaryHB on 16/9/13.
 */
public class GettingStartedApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AVOSCloud.initialize(this,"ATjSteU8rtNJsXBq96FBzhoS-gzGzoHsz", "MkBHGzglYPOh53UhnGJVFJ5H");
    AVOSCloud.setDebugLogEnabled(true);
    AVAnalytics.enableCrashReport(this, true);
  }
}
