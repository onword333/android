package my.pack.namespace;

import my.pack.adapter.JournalAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DocJournal extends Main {
	protected final static String TAG = "DocJournal";
	protected final static String sharePrefStartDate = "start_date";
	protected final static String sharePrefEndDate = "end_date";
	protected Context cxDocJournal;
	protected ListView docJournalList;
	protected db DB;
	protected JournalAdapter journalAdapter;
	protected Cursor journalCursor;
	protected String[] from;
	protected int[] to;
	protected String isBlocking;
	private TextView tvJournalPeriod;

	// период журнала
	// startDate - начальная дата, endDate - конечная дата
	private String startDate;
	private String endDate;
	private TextView tvJournalTotalSum;

	private final static int INTENT_SELECT_PERIOD = 1;

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itOpen:

			break;

		case R.id.itDelete:
			DB.deleteDoc();
			refreshCursor();
			journalAdapter.changeCursor(journalCursor);
			break;
		case R.id.itBlock:
			DB.clearCV();
			if (isBlocking.equalsIgnoreCase("")) {
				DB.addCV(DB.getFDocHead(4), "X");
				Utils.ShowMsg(cxDocJournal, "Документ заблокрован!");
			} else {
				DB.addCV(DB.getFDocHead(4), "");
				Utils.ShowMsg(cxDocJournal, "Документ разблокирован!");
			}
			DB.updateForID(DB.tblDH());

			// journalCursor.requery(); обновляет ListView
			// journalAdapter.notifyDataSetChanged();
			refreshCursor();

			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.journal, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	protected void do_setOnItemLognClick(ListView lv) {
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				journalCursor.moveToPosition(arg2);
				
				int indexJournal = journalCursor.getColumnIndex(DB.fID());
				int _id = journalCursor.getInt(indexJournal);
				DB.ID_DOC = _id;
				int ind_blocking = journalCursor.getColumnIndex(DB
						.getFDocHead(4));
				isBlocking = journalCursor.getString(ind_blocking);
				// journalAdapter.changeCursor(journalCursor);

				Log.d("Журнал", "Долгое нажатие " + _id + " " + arg3 + "");

				return false;
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			return;
		}

		startDate = data.getStringExtra(START_DATE);
		endDate = data.getStringExtra(END_DATE);
		String period = "Период c " + startDate + " по " + endDate + "";
		savePref(sharePrefStartDate, startDate);
		savePref(sharePrefEndDate, endDate);
		switch (requestCode) {
		case INTENT_SELECT_PERIOD:
			tvJournalPeriod.setText(period);
			/*
			 * String leftJoin = "LEFT JOIN " + DB.tblClient() + " b ON a." +
			 * DB.fkPartner() + " = b." + DB.fID() + "";
			 * 
			 * String[] columns = new String[] { "a." + DB.fID() + " AS " +
			 * DB.fID(), "a." + DB.fkPartner() + " AS " + DB.fkPartner(),
			 * "strftime('%Y-%m-%d', a." + DB.fDate() + ") AS " + DB.fDate(),
			 * "b." + DB.fName() + " AS " + DB.fName(), "a." + DB.fSum() +
			 * " AS " + DB.fSum(), "a." + DB.getFDocHead(4) + " AS " +
			 * DB.getFDocHead(4) };
			 * 
			 * String where = "a." + DB.fDate() + " BETWEEN ? AND ?";
			 * 
			 * DB.setField(DB.tblDH() + " a " + leftJoin, columns, where, new
			 * String[] { "" + startDate + " 00:00:00", "" + endDate +
			 * " 23:59:59" }, null, null, "a." + DB.fID() + " DESC");
			 * 
			 * journalCursor = DB.doSelect();
			 * 
			 * Log.d(TAG, "Кол-во записей " + journalCursor.getCount() + " '" +
			 * data.getStringExtra(START_DATE) + "'");
			 * journalAdapter.changeCursor(journalCursor);
			 */
			refreshCursor();
			refreshTotalSum();
			break;
		default:
			break;
		}
	}

	private void doOnClick(Button bt) {
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.journal_bt_choice_period:

					Intent intent = new Intent(cxDocJournal,
							DialogPeriodActivity.class);
					intent.putExtra(START_DATE, startDate);
					intent.putExtra(END_DATE, endDate);
					startActivityForResult(intent, INTENT_SELECT_PERIOD);

					break;

				default:
					break;
				}
			}
		});
	}

	protected void do_setOnItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ViewParent par = arg1.getParent();
				ListView list = (ListView) par;
				switch (list.getId()) {
				case R.id.docJournalList:
					int index = journalCursor.getColumnIndex(DB.fID());
					int _id = journalCursor.getInt(index);

					Intent intent = new Intent(cxDocJournal,
							NewZayavkaActivity.class);
					intent.putExtra("action",
							getResources().getString(R.string.str_zayavka));
					intent.putExtra(DB.fID() + DB.tblDH(), _id);
					startActivity(intent);
					break;

				default:
					break;
				}

			}

		});

	}

	private void refreshTotalSum() {

		String where = "a." + DB.fDate() + " BETWEEN ? AND ?";
		String[] where_arg = new String[] { startDate + " 00:00:00",
				endDate + " 23:59:59" };
		DB.setField(DB.tblDH() + " a", new String[] { "SUM(a." + DB.fSum()
				+ ") AS " + DB.fSum() }, where, where_arg, null, null, null);
		Cursor curTotalSum = DB.doSelect();

		if (curTotalSum.moveToFirst()) {
			int indTotalSum = curTotalSum.getColumnIndex("fSum");
			double totalSum = curTotalSum.getFloat(indTotalSum);

			tvJournalTotalSum.setText("Итого сумма: "
					+ Utils.formatNumber(totalSum, "#,##0.00"));

		}
		curTotalSum.close();

	}

	// процедура обновляет ListView
	protected void refreshCursor() {
		String leftJoin = "LEFT JOIN " + DB.tblClient() + " b ON a."
				+ DB.fkPartner() + " = b." + DB.fID() + "";

		String[] columns = new String[] { "a." + DB.fID() + " AS " + DB.fID(),
				"a." + DB.fkPartner() + " AS " + DB.fkPartner(),
				"strftime('%Y-%m-%d', a." + DB.fDate() + ") AS " + DB.fDate(),
				"b." + DB.fName() + " AS " + DB.fName(),
				"a." + DB.fSum() + " AS " + DB.fSum(),
				"a." + DB.getFDocHead(4) + " AS " + DB.getFDocHead(4) };

		String where = "a." + DB.fDate() + " BETWEEN ? AND ?";
		String[] where_arg = new String[] { startDate + " 00:00:00",
				endDate + " 23:59:59" };

		DB.setField(DB.tblDH() + " a " + leftJoin, columns, where, where_arg,
				null, null, "a." + DB.fID() + " DESC");
		stopManagingCursor(journalCursor);
		journalCursor = DB.doSelect();

		// journalCursor = DB.getJournal();
		// меняем курсор, позволяет обновить TextView (цвет)
		if (journalAdapter != null) {
			journalAdapter.changeCursor(journalCursor);
		}
		startManagingCursor(journalCursor);
	}

	protected void prepareField() {
		// from = new String[] { DB.fName(), DB.fID(), DB.fDate(), DB.fSum(),
		// DB.getFDocHead(4) };
		// to = new int[] { R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5 };
		from = new String[] { DB.fName(), DB.fID(), DB.fDate(), DB.fSum() };

		to = new int[] { R.id.tv1, R.id.tv3, R.id.tv5, R.id.tv7 };
	}

	/**
	 * инициализация виджетов
	 */
	protected void init_widget() {
		cxDocJournal = this.getApplicationContext();
		docJournalList = (ListView) findViewById(R.id.docJournalList);
		tvJournalPeriod = (TextView) findViewById(R.id.journal_tv_period);
		tvJournalTotalSum = (TextView) findViewById(R.id.journal_tv_total_sum);

		Button bt_period = (Button) findViewById(R.id.journal_bt_choice_period);
		doOnClick(bt_period);
		do_setOnItemClick(docJournalList);
		do_setOnItemLognClick(docJournalList);
		registerForContextMenu(docJournalList);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doc_journal);
		init_widget();
		//startDate = Utils.getCurentDate("yyyy-MM-dd");
		//endDate = startDate;
		startDate = loadText(sharePrefStartDate, Utils.getCurentDate("yyyy-MM-dd"));
		endDate = loadText(sharePrefEndDate, Utils.getCurentDate("yyyy-MM-dd"));
		tvJournalPeriod.setText("Период c " +startDate + " по " + endDate);
		DB = new db(cxDocJournal);
		DB.open();
		prepareField();
		refreshCursor();
		journalAdapter = new JournalAdapter(cxDocJournal,
				R.layout.doc_journal_item, journalCursor, from, to);
		docJournalList.setAdapter(journalAdapter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		DB.close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		showLog(TAG, "onRess");
		super.onResume();
	}

	@Override
	protected void onStart() {
		refreshTotalSum();
		showLog(TAG, "onStart");
		super.onStart();
	}

}
