package my.pack.namespace;

import my.pack.adapter.ClientAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class MagazinActivity extends Main {
	public final static String TAG = "Magazin";
	public Context contextMag;
	protected ListView magazinList;
	protected String[] from;
	protected int[] to;
	protected db dbMag;
	protected Cursor cursorMag;
	protected CursorAdapter magAdapter;
	protected int ID_CLIENT = 0;

	protected void doItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				cursorMag.moveToPosition(arg2);
				int colIndex = cursorMag.getColumnIndex("fName");
				String fName = cursorMag.getString(colIndex);

				Intent intent = new Intent();
				intent.putExtra("_id", arg3);
				intent.putExtra("fName", fName);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	public void refreshCursor(String tblName) {
		Intent intent = getIntent();
		int ID_CLIENT = intent.getIntExtra("ID_CLIENT", 0);
		showLog(TAG, " id clienta " + ID_CLIENT + "");
		stopManagingCursor(cursorMag);
		cursorMag = dbMag.customQuery("SELECT * FROM " + dbMag.tblMag()
				+ " WHERE " + dbMag.fkPartner() + " = " + ID_CLIENT + "");
		startManagingCursor(cursorMag);
	}

	public void init_widget() {
		contextMag = this.getApplicationContext();
		magazinList = (ListView) findViewById(R.id.lvCounterAgent_list);
		doItemClick(magazinList);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counteragent);
		init_widget();
		dbMag = new db(contextMag);
		dbMag.open();
		from = new String[] { dbMag.fName(), dbMag.fOur(), dbMag.fThem() };
		to = new int[] { R.id.tvCounterAgent_name, R.id.tvCounterAgent_sumOur,
				R.id.tvCounterAgent_sumThem };
		refreshCursor(dbMag.tblMag());
		magAdapter = new ClientAdapter(contextMag,
				R.layout.counter_agent_linear, cursorMag, from, to);
		magazinList.setAdapter(magAdapter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dbMag.close();
		super.onDestroy();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		showLog(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		showLog(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		showLog(TAG, "onResume");
		super.onResume();
	}
}
