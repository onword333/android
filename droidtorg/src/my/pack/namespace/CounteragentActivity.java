package my.pack.namespace;

import my.pack.adapter.ClientAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CounteragentActivity extends Main {
	public String table_name = "tblCounteragent";
	public String colName = "fName";
	public String colSum = "fSum";

	public Context contextCounterAgent;
	private db DB;
	private Cursor curCounter;
	public ListView lvCounterAgent_list;
	public ClientAdapter adap;

	// определяет с какой активити пришли,
	// по умолчанию с заявки - 1, 0 - с главной активити
	protected int openFrom = 1;
	private String tbl_name, where, order_by, group_by, having;
	private String[] columns, where_arg;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.itSearch:
			Context context = getApplicationContext();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(lvCounterAgent_list,
					InputMethodManager.SHOW_FORCED);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.counteragent, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void doItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (curCounter.isClosed()) {
					Log.d("ISCLOSED", "CLOSSED " + arg3 + "");
				}
				curCounter.moveToPosition(arg2);
				int colIndex = curCounter.getColumnIndex("fName");
				String fName = curCounter.getString(colIndex);

				int fTypePriceIndex = curCounter.getColumnIndex(DB.fTypePrice());
				int fTypePrice = curCounter.getInt(fTypePriceIndex);

				if (openFrom == 1) {
					Intent intent = new Intent();
					intent.putExtra("_id", arg3);
					intent.putExtra("fName", fName);
					intent.putExtra(DB.fTypePrice(), fTypePrice);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

	}

	public void refreshCursor() {
		stopManagingCursor(curCounter);
		curCounter = DB.doSelect();
		startManagingCursor(curCounter);
	}

	private void init_widget() {
		contextCounterAgent = this;
		lvCounterAgent_list = (ListView) findViewById(R.id.lvCounterAgent_list);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counteragent);
		init_widget();
		Intent intent = getIntent();
		openFrom = intent.getIntExtra("openFrom", 1);
		DB = new db(contextCounterAgent);
		// инициализация запроса
		tbl_name = DB.TBL_CLIENT;
		columns = null;
		where = "" + DB.fName() + " LIKE ?";
		where_arg = new String[1];
		where_arg[0] = "%%";
		order_by = DB.fName();
		group_by = null;
		having = null;
		DB.setField(tbl_name, columns, where, where_arg, group_by, having,
				order_by);

		DB.open();

		String[] from = { DB.fName(), DB.fOur(), DB.fThem() };
		int[] to = { R.id.tvCounterAgent_name, R.id.tvCounterAgent_sumOur,
				R.id.tvCounterAgent_sumThem };

		refreshCursor();
		// Adapter adap = new SimpleCursorAdapter(contextCounterAgent,
		// R.layout.counter_agent_linear, curCounter, from, to);

		adap = new ClientAdapter(contextCounterAgent,
				R.layout.counter_agent_linear, curCounter, from, to);

		lvCounterAgent_list.setAdapter((ListAdapter) adap);
		doItemClick(lvCounterAgent_list);

		/*
		 * final EditText itemName = (EditText) findViewById(R.id.etSearch);
		 * itemName.addTextChangedListener(new TextWatcher() {
		 * 
		 * public void afterTextChanged(Editable s) { }
		 * 
		 * public void beforeTextChanged(CharSequence s, int start, int count,
		 * int after) { }
		 * 
		 * public void onTextChanged(CharSequence s, int start, int before, int
		 * count) { Log.d("CONTER", s.toString());
		 * adap.getFilter().filter(s.toString()); } });
		 */

		adap.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				where_arg[0] = "%" + constraint.toString().toUpperCase() + "%";
				DB.setField(tbl_name, columns, where, where_arg, group_by,
						having, order_by);
				if (!constraint.toString().equalsIgnoreCase("")) {
					curCounter = DB.fetchCountriesByName(constraint.toString());
				}
				// return DB.fetchCountriesByName(constraint.toString());
				return curCounter;
			}
		});

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		/*
		 * Log.d("onTextChanged", "Filter:" + s); if (adap != null) {
		 * adap.getFilter().filter(s); }
		 */
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DB.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
