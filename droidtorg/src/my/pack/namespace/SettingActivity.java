package my.pack.namespace;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingActivity extends Activity {
	protected Context cx;
	protected String settingName = "PathUpdate";
	protected String keyName = "RadioButtion";
	protected SharedPreferences setting;
	protected SharedPreferences.Editor shareEditor;
	protected RadioGroup rg;
	protected RadioButton rbSdCard, rbSystem;

	protected void rbOnClick(View v) {
		v.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				switch (v.getId()) {
				case R.id.rbSdcard:
					shareEditor.putInt(keyName, 1);
					Toast.makeText(cx, "���������� ����� ����������� �� sd-����� � �������� bluetooth",
							Toast.LENGTH_LONG).show();
					break;
				case R.id.rbSystem:
					shareEditor.putInt(keyName, 2);
					Toast.makeText(cx, "���������� ����� ����������� � ��������� ��������. ",
							Toast.LENGTH_LONG).show();
					break;

				default:
					break;
				}
				shareEditor.commit();

			}
		});
	}

	// protected void radioButtonClick(View v) {
	// rbSdCard = (RadioButton) v;
	// Toast.makeText(cx, rbSdCard.getText() + " wos chosen.",
	// Toast.LENGTH_SHORT)
	// .show();
	//
	// }

	protected void initWidget() {
		cx = this.getApplicationContext();
		rg = (RadioGroup) findViewById(R.id.rgPath);
		rbSdCard = (RadioButton) findViewById(R.id.rbSdcard);
		rbSystem = (RadioButton) findViewById(R.id.rbSystem);
		rbOnClick(rbSdCard);
		rbOnClick(rbSystem);
		// rbSdCard.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// radioButtonClick(v);
		//
		// }
		// });
		// rg.get
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		initWidget();
		setting = getSharedPreferences(settingName, MODE_PRIVATE);
		shareEditor = setting.edit();
		int rbValue = setting.getInt(keyName, 2);
		if (rbValue == 1) {
			// ����� ������
			rbSdCard.setChecked(true);
		} else {
			// ��������� �������
			rbSystem.setChecked(true);

		}

	}
}
