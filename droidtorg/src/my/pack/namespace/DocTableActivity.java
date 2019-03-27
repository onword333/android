package my.pack.namespace;

import my.pack.adapter.DocTableAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class DocTableActivity extends NewZayavkaActivity {
	protected int destroyActivity = 1;
	public final static String TAG = "DocTableActivity";
	public final static int REQUEST_CHOICE = 1;
	public final static int REQUEST_INPUT = 2;
	public Context contextDocTable;
	public ListView OrderList;
	public Button btChoice, btDocTable;
	protected db dbDocTable;
	protected Cursor cursorDocTable;
	public DocTableAdapter docTableAdapter;
	public Spinner spDocTableFilter;
	protected String[] from;
	protected int[] to;

	public void refreshCursor(String tblName) {
		stopManagingCursor(cursorDocTable);
		cursorDocTable = dbDocTable.getAllData(tblName);
		startManagingCursor(cursorDocTable);
	}

	protected void do_setOnItemSelected(Spinner sp) {
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				// showLog(TAG, "выбрано значение");
				// showLog(TAG, "" + arg0 + "");
				prepareField();
				docTableAdapter
						.changeCursorAndColumns(cursorDocTable, from, to);

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				// showLog(TAG, "не выбрано");
			}
		});
	}

	protected void do_setOnItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// Intent inputOrder = new Intent(contextDocTable,
				// InputOrderActivity.class);
				// startActivity(inputOrder);

				cursorDocTable.moveToPosition(arg2);
				int tovarIndex = cursorDocTable
						.getColumnIndex(dbDocTable.DT_KEYTOV);
				int priceIndex = cursorDocTable
						.getColumnIndex(dbDocTable.TOV_PRICE);

				int key = cursorDocTable.getInt(tovarIndex);
				float price = cursorDocTable.getFloat(priceIndex);
				Intent addOrder = new Intent(contextDocTable, AddOrder.class);
				addOrder.putExtra("tovarName", key);
				addOrder.putExtra("price", price);
				startActivity(addOrder);

			}
		});

	}

	protected void do_click(Button bButton) {
		bButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// switch (v.getId()) {
				// case R.id.btDocTable:
				// destroyActivity = 0;
				// TestActivity test = new TestActivity();
				//
				// // Intent intent = new Intent(contextDocTable,
				// // TestActivity.class);
				// // startActivity(intent);
				// // destroyActivity = 0;
				// break;
				//
				// default:
				// break;
				// }
			}
		});

	}

	protected void fillDefaultValue(Spinner sp) {
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this,
				R.array.arr_doc_table, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setSelection(1);
	}

	public void init_widget() {
		contextDocTable = this.getApplicationContext();
		OrderList = (ListView) findViewById(R.id.docTableOrderList);
		// spDocTableFilter = (Spinner) findViewById(R.id.spDocTableFilter);
		// btDocTable = (Button) findViewById(R.id.btDocTable);

		do_click(btDocTable);
		do_setOnItemClick(OrderList);
		fillDefaultValue(spDocTableFilter);
		do_setOnItemSelected(spDocTableFilter);
		// showLog(TAG, "" + spDocTableFilter.getSelectedItem() + "");

	}

	protected void prepareField() {
		String tblName = dbDocTable.TBL_TOVAR;
		String filter = spDocTableFilter.getSelectedItem().toString();
		Resources res = getResources();

		if (filter.equals(res.getString(R.string.str_view_tovar))) {
			from = new String[] { dbDocTable.TOV_NAME, dbDocTable.TOV_KEYEDIZM,
					dbDocTable.TOV_PRICE, dbDocTable.TOV_QUANTITY };
			to = new int[] { R.id.tv1Myitem, R.id.tv4Myitem, R.id.tv5Myitem };
		} else {
			tblName = dbDocTable.TBL_DT;
			from = new String[] { dbDocTable.DT_KEYTOV, dbDocTable.DT_EDIZM,
					dbDocTable.DT_PRICE, dbDocTable.DT_COUNT, dbDocTable.DT_SUM };
			to = new int[] { R.id.tv1Myitem, R.id.tv4Myitem, R.id.tv5Myitem };

		}
		// showLog(TAG, tblName);
		refreshCursor(tblName);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doc_table);
		init_widget();

		dbDocTable = new db(contextDocTable);
		dbDocTable.open();
		prepareField();
		docTableAdapter = new DocTableAdapter(contextDocTable, R.layout.myitem,
				cursorDocTable, from, to);

		OrderList.setAdapter(docTableAdapter);
		// showLog(TAG, "onCreate11111");
		// dbDocTable.close();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dbDocTable.close();

		super.onDestroy();
		if (destroyActivity == 1) {
			// showLog(TAG, "onDestroy");
		}
		destroyActivity = 1;

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		// showLog(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// showLog(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showLog(TAG, "onResume");
		super.onResume();
	}

}
