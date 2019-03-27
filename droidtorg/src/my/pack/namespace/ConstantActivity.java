package my.pack.namespace;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ConstantActivity extends Activity {
	protected final static String TAG = "CONSTANT";
	protected Context cx;
	protected db DB;
	protected ListView lvConstant;
	protected String[] from;
	protected int[] to;
	protected Cursor cur;
	protected SimpleCursorAdapter adap;
	protected View lay;
	protected LayoutInflater layInf;
	protected EditText etValue;
	protected Button butOK, butCencel;
	protected Builder dialogEditText;
	protected String nameFild, val;
	protected String row_id;

	// 1 - диалог изменения констант
	protected int DIALOG_EDIT_TEXT = 1;

	protected void doClick(Button bt) {
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.d("НАЖАТИЕ_КНОПКИ_ОК_ОТМЕНА", DIALOG_EDIT_TEXT + "");

				switch (v.getId()) {
				case R.id.btOK:
					val = etValue.getText().toString();
					Log.d("Alert", "" + DB.fConstant[1] + " " + row_id + "");
					if (val.equals("")) {
						Toast.makeText(cx, "Значение пустое", 2000);
						return;
					}

					Log.d("Alert", "" + DB.fConstant[1] + " " + row_id + " "
							+ val + "");
					// int index = cur.getColumnIndex(DB.fConstant[0]);
					// String fFiledVal = cur.getString(index);

					DB.clearCV();
					DB.addCV(DB.fConstant[1], row_id);
					DB.addCV(DB.fConstant[3], val);

					DB.insertConst(row_id);

					refreshCursor();
					adap.changeCursor(cur);

					// adap.getCursor().requery();

					// startManagingCursor(cur);

					ConstantActivity.this.dismissDialog(DIALOG_EDIT_TEXT);
					break;
				case R.id.btCancel:
					Log.d("Alert", "Кнопка OK");
					ConstantActivity.this.dismissDialog(DIALOG_EDIT_TEXT);
					break;
				default:
					break;
				}

			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d("СОЗДАЕМ_ДИАЛОГ", DIALOG_EDIT_TEXT + "");
		dialogEditText = new AlertDialog.Builder(this);
		dialogEditText.setView(lay);
		switch (id) {
		case 1:

			dialogEditText.setTitle("Введите значение");
			/*
			 * dialogEditText.setMessage("Введите значение").setCancelable(false)
			 * 
			 * ;
			 * 
			 * 
			 * dialogEditText.setPositiveButton("OK", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) {
			 * 
			 * } });
			 * 
			 * dialogEditText.setNegativeButton("Отмена", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) {
			 * 
			 * } });
			 */
			return dialogEditText.create();

		default:
			break;
		}

		return super.onCreateDialog(id);
	}

	protected void do_setOnItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d("СОБЫТИЕ_НА_LIST", DIALOG_EDIT_TEXT + "");
				cur.moveToPosition(arg2);

				int indexID = cur.getColumnIndex(DB.fConstant[1]);
				int index = cur.getColumnIndex(DB.fConstant[3]);

				row_id = cur.getString(indexID);
				val = cur.getString(index);

				int it = InputType.TYPE_CLASS_NUMBER;
				if ((row_id.equalsIgnoreCase("host"))
						|| (row_id.equalsIgnoreCase("resources"))) {
					it = InputType.TYPE_CLASS_TEXT;
				} else if (row_id.equalsIgnoreCase("pass")) {
					//it = InputType.TYPE_CLASS_TEXT
					//		| InputType.TYPE_TEXT_VARIATION_PASSWORD;
				}

				etValue.setInputType(it);

				etValue.setText(val);
				etValue.selectAll();

				showDialog(DIALOG_EDIT_TEXT);
			}
		});
	}

	protected void refreshCursor() {
		stopManagingCursor(cur);
		cur = DB.getAllData("tblConstant");
		startManagingCursor(cur);
	}

	protected void intiWidget() {
		cx = this.getApplicationContext();
		lvConstant = (ListView) findViewById(R.id.lvConstant);
		layInf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		lay = layInf.inflate(R.layout.dialog_change_value,
				(ViewGroup) findViewById(R.id.LinAlertDialog));

		etValue = (EditText) lay.findViewById(R.id.etValue);
		butOK = (Button) lay.findViewById(R.id.btOK);
		butCencel = (Button) lay.findViewById(R.id.btCancel);
		doClick(butOK);
		doClick(butCencel);
		do_setOnItemClick(lvConstant);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.constant);
		intiWidget();
		Log.d("ВЫЗЫВАЕМ_ДИАЛОГ", DIALOG_EDIT_TEXT + "");

		TextView txID = (TextView) findViewById(R.id.textView1);
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		txID.setText(tm.getDeviceId());
		
		DB = new db(cx);
		DB.open();
		from = new String[] { DB.fConstant[2], DB.fConstant[3] };
		to = new int[] { android.R.id.text1, android.R.id.text2 };
		refreshCursor();
		adap = new SimpleCursorAdapter(cx, android.R.layout.simple_list_item_2,
				cur, from, to);
		lvConstant.setAdapter(adap);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		DB.close();
		ConstantActivity.this.removeDialog(DIALOG_EDIT_TEXT);
		super.onDestroy();
	}

}
