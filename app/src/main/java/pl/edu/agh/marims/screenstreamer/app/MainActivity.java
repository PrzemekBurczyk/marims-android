package pl.edu.agh.marims.screenstreamer.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Map;

import pl.edu.agh.marims.screenstreamer.lib.intent.IntentReader;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.ScreenIntercepter;
import pl.edu.agh.marims.screenstreamer.lib.screen.manipulator.ScreenManipulator;

public class MainActivity extends Activity {

    private static final String SERVER_URL = "http://ec2-54-93-32-50.eu-central-1.compute.amazonaws.com";

    private ScreenIntercepter screenIntercepter;
    private ScreenManipulator screenManipulator;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Manipulate");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (screenManipulator != null) {
            screenManipulator.manipulate(null);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(5);
        adapter = new PresentationPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        Map<String, String> intentParams = IntentReader.readIntentParams(getIntent());

        showIntentToast(intentParams);

        View view = findViewById(android.R.id.content);

        screenIntercepter = new ScreenIntercepter(this, view, SERVER_URL, intentParams);
        screenManipulator = new ScreenManipulator(this, view, SERVER_URL, intentParams);

    }

    private void showIntentToast(Map<String, String> intentParams) {
        if (!intentParams.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), IntentReader.printIntentParams(intentParams), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenIntercepter.initialize();
        screenIntercepter.intercept();
//        screenManipulator.initialize();
    }

    @Override
    protected void onPause() {
        screenIntercepter.stop();
//        screenManipulator.stop();
        super.onPause();
    }
}
