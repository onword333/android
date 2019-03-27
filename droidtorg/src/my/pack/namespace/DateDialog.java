package my.pack.namespace;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class DateDialog extends Main {
	int DIALOG_DATE = 1;
	int myYear = 2011;
	int myMonth = 02;
	int myDay = 03;
	Context contextDate;

	
	
	
	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info);
		showDialog(DIALOG_DATE);
	}

	public void onclick(View view) {
		showDialog(DIALOG_DATE);
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_DATE) {
			DatePickerDialog tpd = new DatePickerDialog(this, myCallBack,
					myYear, myMonth, myDay);
			return tpd;
		}
		return super.onCreateDialog(id);
	}

	OnDateSetListener myCallBack = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			myYear = year;
			myMonth = monthOfYear;
			myDay = dayOfMonth;
			//finish();
		}
	};

}
