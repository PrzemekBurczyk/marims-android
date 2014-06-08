package pl.edu.agh.marims.screenstreamer.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by Przemek on 2014-06-08.
 */
public class PresentationPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGES_COUNT = 6;

    public PresentationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainFragment();
            case 1:
                return new TitleFragment();
            case 2:
                return new DescriptionFragment();
            default:
                return new PageFragment();
        }
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

}
