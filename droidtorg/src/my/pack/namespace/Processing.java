package my.pack.namespace;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Processing extends Main {
	protected EditText etStartDate;
	protected EditText etEndDate;
	protected Button btSelectPeriod;
	private Context cx;
	private final static int PERIOD_DIALOG = 2;

	private RadioGroup rbGroup;
	private RadioButton rbBlockDoc;
	private RadioButton rbUnBlockDoc;
	private RadioButton rbDeleteDoc;

	private Button btExecute;

	private AlertDialog.Builder builder;

	protected void DeleteAll(db DB) {
		String[] tblName = new String[] { DbField.tblDocTable,
				DbField.tblDocHead, DbField.tblDebitorka, DbField.tblTovKr,
				DbField.tblTovarSetka, DbField.tblPartnerPrice,
				DbField.tblEdIzm, DbField.tblEdIzmKr, DbField.tblMag,
				DbField.tblPartner, DbField.tblTovar };
		int i = 0;
		for (String name : tblName) {
			DB.setField(name, null, null, null, null, null, null);
			i = DB.doDelete();
			Utils.ShowLog(name + " ��������", i + "");
		}
		Utils.ShowMsg(cx, "���������� �������.");

	}

	protected void DeleteDoc(db DB) {
		String where = DbField.fOut + " = ?";
		String[] where_arg = new String[] { "X" };
		DB.setField(DbField.tblDocHead, new String[] { DbField._id }, where,
				where_arg, null, null, null);

		Cursor cur = DB.doSelect();
		String idsDoc = "";
		int i = 0;
		if (cur.moveToFirst()) {
			do {
				idsDoc += cur.getString(cur.getColumnIndex(DbField._id)) + ", ";
			} while (cur.moveToNext());
			idsDoc = idsDoc.substring(0, idsDoc.length() - 2);
			where = DbField.fKeyZ + " IN (" + idsDoc + ")";
			DB.setField(DbField.tblDocTable, null, where, null, null, null,
					null);
			i = DB.doDelete();

			where = DbField._id + " IN (" + idsDoc + ")";
			DB.setField(DbField.tblDocHead, null, where, null, null, null, null);
			i = DB.doDelete();

		}
		Utils.ShowMsg(cx, "������� ������: " + i + "");

	}

	protected void BlockDoc(db _db, String flag) {
		String startDate = etStartDate.getText().toString().trim();
		String endDate = etEndDate.getText().toString().trim();
		String where = DbField.fDate + " BETWEEN ? AND ?";
		String[] where_arg = new String[] { startDate + " 00:00:00",
				endDate + " 23:59:59" };

		_db.clearCV();
		_db.addCV(DbField.fOut, flag);
		_db.setField(DbField.tblDocHead, null, where, where_arg, null, null,
				null);
		int i = _db.doUpdate();
		Utils.ShowMsg(cx, "��������� ������: " + i + "");

	}

	protected void StartProcess() {
		db DB = new db(cx);
		DB.open();
		switch (rbGroup.getCheckedRadioButtonId()) {
		case R.id.rb_block_doc:
			BlockDoc(DB, "X");
			break;

		case R.id.rb_unblock_doc:
			BlockDoc(DB, "");
			break;

		case R.id.rb_delete_doc:
			DeleteDoc(DB);
			// Utils.ShowMsg(cx, "�������� ������ �� ��������");
			break;

		case R.id.rb_delete_all:

			DeleteAll(DB);
			break;
		default:
			break;
		}

		DB.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PERIOD_DIALOG:
				etStartDate.setText(data.getStringExtra(START_DATE));
				etEndDate.setText(data.getStringExtra(END_DATE));
				break;
			default:
				break;
			}
		}

	}

	protected void openPeriodDialog() {
		Intent intent = new Intent(cx, DialogPeriodActivity.class);
		startActivityForResult(intent, PERIOD_DIALOG);
	}

	protected void doSetOnClickButton(Button bt) {
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btSelectPeriod:
					openPeriodDialog();
					break;

				case R.id.bt_execute:
					// StartProcess();
					switch (rbGroup.getCheckedRadioButtonId()) {

					case R.id.rb_block_doc:
						builder.setTitle("������������� ������.");
						builder.setMessage("������ ����� ������������� �� ��������� ������.");
						break;

					case R.id.rb_unblock_doc:
						builder.setTitle("�������������� ������.");
						builder.setMessage("������ ����� �������������� �� ��������� ������.");
						break;

					case R.id.rb_delete_doc:
						builder.setTitle("������� ������.");
						builder.setMessage("�������� ���������������(������������) ������ �� ������.");
						break;

					case R.id.rb_delete_all:
						builder.setTitle("������� ���.");
						//builder.setIcon(android.R.drawable.ic_delete);
						builder.setMessage("��������! ����� ������� ��� ���������� - ������, �����������. ����� ���� ��������� ���� ��������� ����������!");
						break;
					}
					builder.show();
					break;
				default:
					break;
				}
			}
		});

	}

	protected void doSetEvent() {

	}

	protected void initWidget() {
		cx = this.getApplicationContext();
		etStartDate = (EditText) findViewById(R.id.etStartDate);
		etEndDate = (EditText) findViewById(R.id.etEndDate);
		btSelectPeriod = (Button) findViewById(R.id.btSelectPeriod);

		rbGroup = (RadioGroup) findViewById(R.id.rb_group_processing);
		rbBlockDoc = (RadioButton) findViewById(R.id.rb_block_doc);
		rbUnBlockDoc = (RadioButton) findViewById(R.id.rb_unblock_doc);
		rbDeleteDoc = (RadioButton) findViewById(R.id.rb_delete_doc);

		btExecute = (Button) findViewById(R.id.bt_execute);

		doSetOnClickButton(btExecute);
		doSetOnClickButton(btSelectPeriod);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.processing);
		initWidget();

		etStartDate.setText(Utils.getCurentDate("yyyy-MM-dd"));
		etEndDate.setText(Utils.getCurentDate("yyyy-MM-dd"));

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface paraDialog, int paramButton) {
				switch (paramButton) {
				case DialogInterface.BUTTON_POSITIVE:
					StartProcess();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// Put the code here which should be executed on right
					// button click
					break;

				}
			}
		};

		builder = new AlertDialog.Builder(this);
		builder.setMessage("��������� ��������� ?")
				.setPositiveButton("��", dialogClickListener)
				.setNegativeButton("���", dialogClickListener);

	}

}
