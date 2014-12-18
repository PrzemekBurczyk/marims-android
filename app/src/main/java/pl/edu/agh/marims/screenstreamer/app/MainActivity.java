package pl.edu.agh.marims.screenstreamer.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import pl.edu.agh.marims.screenstreamer.lib.Marims;
import pl.edu.agh.marims.screenstreamer.lib.measurement.Statistics;
import pl.edu.agh.marims.screenstreamer.lib.network.sender.SenderType;
import pl.edu.agh.marims.screenstreamer.lib.screen.intercepter.StatisticsCallback;

public class MainActivity extends Activity {

    private static final String SERVER_URL = "http://ec2-54-93-32-50.eu-central-1.compute.amazonaws.com";

    private Marims marims;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("TCP");
        menu.add("UDP");
        menu.add("HTTP");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals("TCP")) {
            if (marims != null) {
                marims.setSenderType(SenderType.TCP);
            }
            return true;
        } else if (item.getTitle().equals("UDP")) {
            if (marims != null) {
                marims.setSenderType(SenderType.UDP);
            }
            return true;
        } else if (item.getTitle().equals("HTTP")) {
            if (marims != null) {
                marims.setSenderType(SenderType.HTTP);
            }
            return true;
        }
        return false;
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

        ActionBar actionBar = getActionBar();
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View statisticsLayout = inflator.inflate(R.layout.statistics_layout, null);
        final TextView tvStatisticsFps = (TextView) statisticsLayout.findViewById(R.id.tvStatisticsFps);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(statisticsLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        marims = new Marims(this, view, SERVER_URL);
        marims.setStatisticsCallback(new StatisticsCallback() {
            @Override
            public void onNewStatistics(Statistics statistics) {
                tvStatisticsFps.setText(String.format("%.2f", statistics.getSuccessfulSendsPerSecond()));
            }
        });
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
