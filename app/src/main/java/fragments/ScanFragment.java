package fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import touch.salezone.com.salezonem.MainActivity;
import touch.salezone.com.salezonem.R;
import util.Alert;
import util.IntentIntegrator;
import util.IntentResult;
import util.Vars;

public class ScanFragment extends Fragment implements View.OnClickListener {
	Button scan_button;
	Alert alert;
	Vars vars;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
		scan_button = (Button) rootView.findViewById(R.id.scan_button);
		scan_button.setOnClickListener(this);
		alert = new Alert(getActivity());
		vars = new Vars(getActivity());
		vars.log("code===============");
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.scan_button:
				IntentIntegrator scanIntegrator = new  IntentIntegrator(getActivity());
				scanIntegrator.initiateScan();
				break;
			default:
				break;

		}

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//retrieve scan result
		super.onActivityResult(requestCode, resultCode, data);
		IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());

		IntentResult scanningResult;
		scanningResult = intentIntegrator.parseActivityResult(requestCode, resultCode, data);
		vars.log("scanningResult===============" + scanningResult.toString());
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();

			String scanFormat = scanningResult.getFormatName();

			if (scanContent!= null){
				//get application content to the next activity
				alert.alerterSuccess("Success","User valid");

			}else{
				Intent back = new Intent(getActivity(), MainActivity.class);
				startActivity(back);
			}
		}
		else{
			Toast toast = Toast.makeText(getActivity(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
			//Intent us = new Intent(this,MainActivity.class);
			//startActivity(us);
		}

	}
}
