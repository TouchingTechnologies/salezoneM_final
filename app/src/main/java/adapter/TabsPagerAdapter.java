package adapter;

import fragments.ReviewlogFragment;
import fragments.UploadFragment;
import fragments.ScanFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new ScanFragment();
		case 1:
			// Games fragment activity
			return new UploadFragment();
		//case 2:
			// Movies fragment activity
			//return new UploadFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
