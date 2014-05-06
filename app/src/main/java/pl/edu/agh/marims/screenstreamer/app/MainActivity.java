package pl.edu.agh.marims.screenstreamer.app;

import android.app.Activity;
import android.os.Bundle;

import pl.edu.agh.marims.screenstreamer.lib.screen.ScreenIntercepter;

public class MainActivity extends Activity {

    private ScreenIntercepter screenIntercepter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        screenIntercepter = new ScreenIntercepter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenIntercepter.intercept();
    }
}
