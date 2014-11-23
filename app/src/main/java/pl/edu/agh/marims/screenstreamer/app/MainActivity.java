package pl.edu.agh.marims.screenstreamer.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import pl.edu.agh.marims.screenstreamer.lib.screen.ScreenIntercepter;

public class MainActivity extends Activity {

    //    private static final String SERVER_URL = "http://marims-backend.herokuapp.com/upload";
    private static final String SERVER_URL = "http://192.168.0.14/upload";

    private ScreenIntercepter screenIntercepter;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PresentationPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        screenIntercepter = new ScreenIntercepter(this, findViewById(android.R.id.content), SERVER_URL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        screenIntercepter.initialize();
        screenIntercepter.intercept();
    }

    @Override
    protected void onPause() {
        screenIntercepter.stop();
        super.onPause();
    }
}
