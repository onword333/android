package my.pack.namespace;

import java.text.DecimalFormat;

import my.pack.adapter.DocTableAdapter;
//import android.R;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddOrder extends Main {
	private final static String TAG = "AddOrder";
	public TextView tvAddOrderTovar, tvAddOrderSum;
	public EditText etCoast, etquantity;
	public Button bt_save;

	protected Spinner spEdIzm;
	public Context contextAddOrder;
	protected db DB;
	protected Cursor cur;

	// id �������
	protected int idClient;

	// ��� ����
	protected int typePrice;

	protected int ID_TOV, ID_EDIZM, ID_EDIZMKR;
	protected float KRATNOST;
	double SUM, PRICE, COUNT;
	private int isEdit;
	private int idDoc;
	private int idRow;

	public static DecimalFormat getQtyFormat(Context context, String suffix) {
		return new DecimalFormat("#,##0.## '" + suffix + "'");
	}

	public static void hideKeyBoard(IBinder bidner, Context context)

	{

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInputFromInputMethod(bidner, 0);

	}

	protected void setClieckEditText(EditText et) {
		/* et.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { ((EditText) v).selectAll();
		 * 
		 * } }); */
		et.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				EditText et = ((EditText) v);
				switch (v.getId()) {
				case R.id.etAddOrder_quantity:
					et.selectAll();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
					return true;

				default:
					break;
				}

				return false;
			}
		});
	}

	protected void calculateSum() {

		SUM = (double) 0;
		String str_price = etCoast.getText().toString();
		PRICE = Double.parseDouble(str_price);

		String str_count = etquantity.getText().toString();

		if ((str_count.equalsIgnoreCase(""))
				|| (str_count.equalsIgnoreCase("."))) {
			str_count = "0.000";
		} else {

		}
		COUNT = Double.parseDouble(str_count) * KRATNOST;

		Log.d("CAlCULATE", "" + COUNT + "");
		// SUM = (COUNT * KRATNOST) * PRICE;
		SUM = COUNT * PRICE;
		SUM = Double.parseDouble(Utils.formatNumber(SUM, 2).toString());
		// tvAddOrderSum.setText(Float.toString(SUM));
		// DecimalFormat nf = new DecimalFormat("#,##0.00");
		// String x = nf.format(str_price)

		// showLog(TAG, a + "");
		// tvAddOrderSum.setText(nf.format(SUM));
		tvAddOrderSum.setText(Utils.formatNumber(SUM, "#,##0.00"));
	}

	protected void do_addTextChanged(EditText et) {
		et.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// etquantity.setFocusable(true);
				// etquantity.setSelection(0, 2);
				// TODO Auto-generated method stub
				// showLog(TAG, "onTextChanged");
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// etquantity.selectAll();
				// TODO Auto-generated method stub
				// showLog(TAG, "beforeTextChanged");

			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				calculateSum();
				// showLog(TAG, "afterTextChanged");

			}
		});
	}

	protected void do_onItemSelected(final Spinner sp) {
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				cur.moveToPosition(arg2);

				int index = cur.getColumnIndex(DB.EDMKR_KEYEDIZM);

				ID_EDIZM = cur.getInt(index);
				ID_EDIZMKR = (int) arg3;

				index = cur.getColumnIndex(DB.EDMKR_KR);
				KRATNOST = cur.getInt(index);
				// Log.d("do_onItemSelected", etquantity.getText().toString());
				calculateSum();
				// Toast.makeText(contextAddOrder, "" + arg3 + "", 2000).show();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	// protected setSum() {

	// }

	public void do_click(Button bButton) {
		bButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (v.getId()) {
				case R.id.btDocSave:
					showLog(TAG, "��������� ��������");

					DB.clearCV();
					if (idRow > 0) {
						DB.addCV(DB.fID(), idRow);
					}
					DB.ID_DOC = idDoc;
					DB.addCV(DB.fIdDoc(), idDoc);
					DB.addCV(DB.fIdTov(), ID_TOV);
					DB.addCV(DB.fCount(), COUNT);
					DB.addCV(DB.fPrice(), PRICE);
					DB.addCV(DB.fSum(), SUM);
					DB.addCV(DB.fIdEdIzm(), ID_EDIZM);
					DB.addCV(DB.fIdEdIzmKr(), ID_EDIZMKR);
					DB.addCV(DB.fKrat(), KRATNOST);

					if (isEdit == ZERO) {
						showMassage(contextAddOrder, "���������");
						DB.insertRow(DB.tblDT());

						// ��������� ����� ���������
						DB.clearCV();
						DB.addCV(DB.fSum(), DB.getDtSum());
						DB.setField(DB.tblDH(), null, DB.fID() + " = ?",
								new String[] { "" + idDoc + "" }, null, null,
								null);
						DB.doUpdate();

					} else {
						showMassage(contextAddOrder, "���������");
						DB.updateRow(DB.tblDT());

						// ��������� ����� ���������
						DB.clearCV();
						DB.addCV(DB.fSum(), DB.getDtSum());
						DB.setField(DB.tblDH(), null, DB.fID() + " = ?",
								new String[] { "" + idDoc + "" }, null, null,
								null);
						DB.doUpdate();
					}

					Intent intent = new Intent();
					intent.putExtra(IS_EDIT, ONE);
					setResult(RESULT_OK, intent);
					/* Intent intent = new Intent(); //
					 * intent.putExtra(DB.DT_EDIZM, ID_EDIZM);
					 * intent.putExtra(DB.DT_EDIZMKR, ID_EDIZMKR);
					 * intent.putExtra(DB.DT_COUNTKR, KRATNOST);
					 * intent.putExtra(DB.DT_COUNT, COUNT);
					 * 
					 * intent.putExtra(DB.DT_PRICE, PRICE);
					 * intent.putExtra(DB.DT_SUM, SUM); */

					/* Log.d(TAG, "idRow = " + idRow + "\r\n" + "idDoc = " +
					 * idDoc + "\r\n" + DB.DT_EDIZM + " = " + ID_EDIZM + "\r\n"
					 * + DB.DT_EDIZMKR + " = " + ID_EDIZMKR + "\r\n" +
					 * DB.DT_PRICE + " = " + PRICE + "\r\n" + DB.DT_COUNT +
					 * " = " + COUNT + "\r\n" + DB.DT_COUNTKR + " = " + KRATNOST
					 * + "\r\n" + DB.DT_SUM + " = " + SUM + ""); */
					// setResult(RESULT_OK, intent);

					finish();
					break;

				default:
					break;
				}
			}
		});
	}

	public void getValFromIntent() {
		Intent intent = getIntent();

		isEdit = intent.getIntExtra(IS_EDIT, ZERO);
		if (isEdit == ONE) {
			bt_save.setText("aa");
		}

		idDoc = intent.getIntExtra(DB.fID() + DB.tblDH(), ZERO);

		idRow = intent.getIntExtra(DB.fID(), ZERO);

		ID_TOV = intent.getIntExtra(DB.fID() + DB.tblTovar(), 0);
		KRATNOST = intent.getFloatExtra(DB.fKrat(), 1.00f);

		String fName = intent.getStringExtra(DB.fName());
		tvAddOrderTovar.setText(fName);

		Utils.ShowLog(TAG, "GET INTENT VAL: " + ID_TOV + " fName: " + fName);

		float price = intent.getFloatExtra(DB.fPrice(), 0.00f);
		etCoast.setText(Float.toString(price));

		// double fCount = intent.getDoubleExtra(DB.fCount(), 1.000d) /
		// KRATNOST;
		double fCount = intent.getDoubleExtra(DB.fCount(), 1.000f) / KRATNOST;
		// etCoast.setText(Double.toString(fCount));
		// float fCount = intent.getFloatExtra(DB.fCount(), 1.000f);
		// double fCount = 1.000;

		etquantity.setText(Double.toString(fCount));
		// etquantity.setText("" + 7 + "");

		// Log.d("getValFromIntent_2", "" + "77 " + fCount + "");
		// Log.d("getValFromIntent_2", "" + "77 " +
		// etquantity.getText().toString() + "");
	}

	public void doEventWidget() {

		do_click(bt_save);
		do_onItemSelected(spEdIzm);
		do_addTextChanged(etquantity);

	}

	public void init_widget() {
		contextAddOrder = this.getApplicationContext();
		tvAddOrderTovar = (TextView) findViewById(R.id.tv_add_order_tovarname);
		tvAddOrderSum = (TextView) findViewById(R.id.tvAddOrderSum);
		bt_save = (Button) findViewById(R.id.btDocSave);
		etCoast = (EditText) findViewById(R.id.etAddOrder_coast);
		etquantity = (EditText) findViewById(R.id.etAddOrder_quantity);
		spEdIzm = (Spinner) findViewById(R.id.spEdIzm);

		// ��� Android 4 ����� ���������� OnTochListene ��� ����, ����� ���
		// ������� ����� ���������
		setClieckEditText(etquantity);

		// �������, ��� ���������� ����� ����� ������� ������ ����
		// etquantity.setKeyListener(DigitsKeyListener.getInstance(true,true));
		// //���� �����
		etquantity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
				| android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); // ����
																	// �����

		// etquantity.setFilters(new InputFilter[] { new MaskEditText() });
		// Log.d("init_widget", etquantity.getText().toString());

	}

	private void getPriceForTovar() {
		Cursor tempCur = null;
		float price = 0.00f;
		float fParcent = 0.00f;
		int fHow = 0;

		// �������� id ������� �� ������
		DB.setField(DbField.tblDocHead, new String[] { DbField.fKeyPartner },
				"_id = ?", new String[] { String.valueOf(idDoc) }, null, null,
				null);
		tempCur = DB.doSelect();
		if (tempCur.moveToFirst()) {
			idClient = tempCur.getInt(tempCur
					.getColumnIndex(DbField.fKeyPartner));
		}
		tempCur.close();

		// �������� ��� ���� ��� �������� idClient
		DB.setField(DbField.tblPartner, null, "_id = ?",
				new String[] { String.valueOf(idClient) }, null, null, null);
		tempCur = DB.doSelect();
		if (tempCur.moveToFirst()) {
			typePrice = tempCur.getInt(tempCur
					.getColumnIndex(DbField.fTypePrice));
		}
		tempCur.close();

		Utils.ShowLog("�������� ���� ", DB.definePrice(typePrice));

		// �������� ���� ������ �� ���� ���� ������� (typePrice)
		DB.setField(DbField.tblTovar, new String[] { DB.definePrice(typePrice)
				+ " AS " + DbField.fPrice }, "_id = ?",
				new String[] { String.valueOf(ID_TOV) }, null, null, null);
		tempCur = DB.doSelect();
		if (tempCur.moveToFirst()) {
			price = tempCur.getFloat(tempCur.getColumnIndex(DbField.fPrice));
		}
		tempCur.close();

		// �������� � ����� fCodeParent ������. ��� ����� ��� ����, ����� �����
		// �� ����� ���� ����� ����������. (��� ������� ������� � �����������
		// �������� ������� ����, ��
		// ��� ������� ������� ����� ���� ��������� ������ ���� �� �����/������)
		int fCodeParent = ID_TOV;
		String listID = "" + fCodeParent;
		while (fCodeParent > 0) {
			DB.setField(DbField.tblTovar, new String[] { DbField.fCodeParent },
					"_id = ?", new String[] { String.valueOf(fCodeParent) },
					null, null, null);
			tempCur = DB.doSelect();

			if (tempCur.moveToFirst()) {
				fCodeParent = tempCur.getInt(tempCur
						.getColumnIndex(DbField.fCodeParent));
				if (fCodeParent > 0) {
					listID += "," + fCodeParent;
				}

			} else {
				fCodeParent = 0;
			}
			tempCur.close();
		}

		// ������� ������ ��� ����������� ���� �� ������� � ������ ��� �����. ������/������
		DB.setField(DbField.tblPartnerPrice, null, DbField._id + " = ? AND "
				+ DbField.fkTovar + " IN (" + listID + ")",
				new String[] { String.valueOf(idClient) }, null, null, null);
		tempCur = DB.doSelect();
		if (tempCur.moveToFirst()) {
			// �������
			fParcent = tempCur.getFloat(tempCur
					.getColumnIndex(DbField.fParcent));
			// ��� ������� ����
			fHow = tempCur.getInt(tempCur.getColumnIndex(DbField.fHow));

			// ��� ����
			typePrice = tempCur.getInt(tempCur.getColumnIndex(DbField.fBase));

		}

		float res = 0.00f;

		switch (fHow) {
		case 1:

			break;
		case 2:

			break;
		case 3:
			// �������� ���� ������ �� ���� ���� (typePrice)
			DB.setField(DbField.tblTovar,
					new String[] { DB.definePrice(typePrice) + " AS "
							+ DbField.fPrice }, "_id = ?",
					new String[] { String.valueOf(ID_TOV) }, null, null, null);
			tempCur = DB.doSelect();
			if (tempCur.moveToFirst()) {
				price = tempCur
						.getFloat(tempCur.getColumnIndex(DbField.fPrice));
			}
			tempCur.close();
			res = price * (fParcent / 100);
			/*if (res < 0.00f) {
				res = res * (-1);
			}*/
			price = price + res;
			break;
		default:
			break;
		}

		//Utils.ShowLog(TAG, "���. ID: " + listID + " parcent = " + price
		//		+ " parsent:" + Utils.formatNumber((price), 2));
		etCoast.setText(Utils.formatNumber((price), 2).toString());

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_order);
		
		init_widget();

		// ������� ��������� ������ ��� ������ � ����� ������
		DB = new db(contextAddOrder);
		DB.open();

		// �������� ������ �� ������������� Intent
		getValFromIntent();

		// �������� ����
		getPriceForTovar();

		// ������������� �������������� ��� widget
		doEventWidget();

		String sqr_str = "";

		if (KRATNOST == 1.00f) {
			sqr_str = "SELECT * FROM " + DB.TBL_EDMKR + " a INNER JOIN "
					+ DB.TBL_TOVARKR + " b ON a." + DB.EDMKR_ID + " = b."
					+ DB.TOVKR_KEYKR + " WHERE b." + DB.TOVKR_KEYTOV + " = "
					+ ID_TOV + " ORDER BY " + DB.EDMKR_KR + "";
		} else {
			sqr_str = "SELECT * FROM " + DB.TBL_EDMKR + " a INNER JOIN "
					+ DB.TBL_TOVARKR + " b ON a." + DB.EDMKR_ID + " = b."
					+ DB.TOVKR_KEYKR + " WHERE b." + DB.TOVKR_KEYTOV + " = "
					+ ID_TOV + " AND a." + DB.EDMKR_KR + " = " + KRATNOST
					+ " ORDER BY " + DB.EDMKR_KR + "";
		}

		Utils.ShowLog(TAG + " QUE ", sqr_str);

		cur = DB.customQuery(sqr_str);

		String[] from = { DB.EDM_NAME, DB.EDMKR_KR };
		int[] to = { R.id.tvEdIzm, R.id.tvEdIzmKr };

		DocTableAdapter adap = new DocTableAdapter(contextAddOrder,
				R.layout.edizmer, cur, from, to);
		spEdIzm.setAdapter(adap);

		if (DB.IS_LOG == 1) {
			Log.d(TAG + "_onCreate", "SELECT * FROM " + DB.TBL_EDMKR
					+ " a INNER JOIN " + DB.TBL_TOVARKR + " b ON a."
					+ DB.EDMKR_ID + " = b." + DB.TOVKR_KEYKR + " WHERE b."
					+ DB.TOVKR_KEYTOV + " = " + ID_TOV + " ORDER BY "
					+ DB.EDMKR_KR + "\r\n" + KRATNOST);
		}
		etquantity.selectAll();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cur.close();
		DB.close();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showLog(TAG, "������ Back �������");
		super.onBackPressed();

	}

}
