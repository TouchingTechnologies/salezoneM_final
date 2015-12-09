package fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dbstuff.Codesdb;
import touch.salezone.com.salezonem.R;
import util.Vars;

public class ReviewlogFragment extends Fragment {
	TextView test;
	Vars vars;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reviewlog, container, false);
		vars = new Vars(getActivity());
		List<Codesdb> codes = Codesdb.listAll(Codesdb.class);
		test = (TextView) rootView.findViewById(R.id.test);
		test.setText("loca=="+vars.location+"longi=="+vars.longitude+"latitude===="+vars.latitudes+"number of codes===="+codes.size());
		return rootView;
	}
}
