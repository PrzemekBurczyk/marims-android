package pl.edu.agh.marims.screenstreamer.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import pl.edu.agh.marims.screenstreamer.lib.Marims;

public class MainActivity extends Activity {

    private static final String SERVER_URL = "http://ec2-54-93-32-50.eu-central-1.compute.amazonaws.com";

    private Marims marims;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Manipulate");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PresentationPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        View view = findViewById(android.R.id.content);

        marims = new Marims(this, view, SERVER_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        marims.onResume();
    }

    @Override
    protected void onPause() {
        marims.onPause();
        super.onPause();
    }
}
