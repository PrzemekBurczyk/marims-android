package pl.edu.agh.marims.screenstreamer.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by Przemek on 2014-06-08.
 */
public class PresentationPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGES_COUNT = 5;

    public PresentationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new PageFragment();
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

}
