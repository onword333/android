package my.pack.namespace;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PasswordActivity extends Main {
	private final static String TAG = "PasswordActivity";
	protected final HashMap<String, String> map = new HashMap<String, String>();
	private Context cx;
	private EditText et;
	private Button bOK;
	private Button bNO;
	private EditText etPassword;
	private String sPassword = "";
	

	private void doSetOnClick(Button b) {
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btOK:
					String sInputPassword = etPassword.getText().toString();
					if (sInputPassword.equalsIgnoreCase(sPassword)) {
						Intent intent = new Intent(cx, ConstantActivity.class);
						startActivity(intent);
						finish();
					} else {
						Utils.ShowMsg(cx, "Доступ закрыт!");
					}
					Log.d(TAG, "btOK");
					break;
				case R.id.btCancel:
					finish();
					Log.d(TAG, "btNO");
					break;

				default:
					break;
				}
			}
		});
	}

	private void initOnClick() {
		doSetOnClick(bOK);
		doSetOnClick(bNO);
	}

	private void initWidgets() {
		cx = this.getApplicationContext();
		et = (EditText) findViewById(R.id.etValue);
		bOK = (Button) findViewById(R.id.btOK);
		bNO = (Button) findViewById(R.id.btCancel);
		etPassword = (EditText) findViewById(R.id.etValue);
		initOnClick();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_change_value);
		initWidgets();

		db _db = new db(cx);
		_db.open();

		_db.setField(DbField.tblConstant, null, null, null, null, null, null);
		Cursor cur = _db.doSelect();
		String key, val;
		if (cur.moveToFirst()) {
			map.clear();
			do {
				key = cur.getString(cur.getColumnIndex(DbField.fProgID));
				val = cur.getString(cur.getColumnIndex(DbField.fValue));
				map.put(key, val);
				Log.d(TAG,"key: " + key + " val: " + val + "");
			} while (cur.moveToNext());
			sPassword = map.get("pass");
			
		}
		cur.close();
		_db.close();
	}

}
