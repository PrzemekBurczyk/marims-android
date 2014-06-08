package pl.edu.agh.marims.screenstreamer.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import pl.edu.agh.marims.screenstreamer.lib.screen.ScreenIntercepter;

public class MainActivity extends Activity {

    private ScreenIntercepter screenIntercepter;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setImageResource(R.drawable.cat);

        screenIntercepter = new ScreenIntercepter(this);

        findViewById(R.id.bt_initialize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenIntercepter.intercept();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        screenIntercepter.initialize();
    }

    @Override
    protected void onPause() {
        screenIntercepter.stop();
        super.onPause();
    }
}
