package my.pack.namespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ReportActivity extends Main {
	private final static String TAG = "ReportActivity";
	private Context c;
	private TextView tvPeriod, tvCount, tvSum;
	private Button btPeriod, btExecute;
	private Spinner spTovar;
	private SimpleAdapter sa;
	private ArrayList<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
	private Map<String, String> item = new HashMap<String, String>();
	private final static int TOVAR_ACTIVITY = 1;
	private final static int PERIOD_DIALOG = 2;

	// период
	private String startDate, endDate;

	// всего кол-во, всего сумма
	private double totalCount = 0.00d;
	private double totalSum = 0.00d;

	// класс для работы с БД
	private db DB;
	
	// параметры запроса
	private String[] column;
	private String innerJoin;
	private String nestedSQL;
	private String where;
	private String[] where_arg;

	// определяет тип выбранной позиции, 1 - группа, 0 - не явл. группой
	private int isGroup = 1;

	public void setTotal() {
		tvCount.setText("Количество: "
				+ Utils.formatNumber(totalCount, "#,##0.00"));
		tvSum.setText("Сумма: " + Utils.formatNumber(totalSum, "#,##0.00"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TOVAR_ACTIVITY:
				isGroup = data.getIntExtra(DB.fIsGroup(), 0);
				showLog(TAG, db.TOV_GROUP + " "+ isGroup + " " + data.getIntExtra(DB.fID() + DB.tblTov(), 0) + "");
				clearItem();
				addItem(DB.fID(), data.getIntExtra(DB.fID() + DB.tblTov(), 0) + "");
				addItem(DB.fName(), data.getStringExtra(DB.fName()));
				addItemToSpinner();
				break;
			case PERIOD_DIALOG:
				startDate = data.getStringExtra(START_DATE);
				endDate = data.getStringExtra(END_DATE);
				tvPeriod.setText("Дата: " + startDate + " по " + endDate + "");
				break;

			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doOnTouchSpinner(Spinner sp) {
		sp.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent(c, NomenclatureActivity.class);
				intent.putExtra(OPEN_FROM, REPORT);
				startActivityForResult(intent, TOVAR_ACTIVITY);

				showLog(TAG, event.toString() + " ");
				return false;
			}
		});
	}

	private void openPeriodDialog() {
		Intent intent = new Intent(c, DialogPeriodActivity.class);
		startActivityForResult(intent, PERIOD_DIALOG);
	}

	/**
	 * <b>Описание</b> - формирует строку запроса
	 * 
	 * @param DB - экземпляр класса для работы с БД
	 * @param isGroup - определяет тип выбранной позиции, 1-группа, 0 - не
	 *            является группой
	 */

	private void setStrSql(db DB) {
		// елси выбрана группа
		innerJoin = "INNER JOIN " + DB.tblDH() + " b ON a." + db.DT_KEYZ
				+ " = b." + DB.fID();
		column = new String[] { "SUM(a." + DB.fCount() + ") AS " + DB.fCount(),
				"SUM(a." + DB.fSum() + ") AS " + DB.fSum() };
		nestedSQL = "SELECT " + db._ID + " FROM " + DB.tblTovar() + " WHERE "
				+ DB.fCodeRoot() + " IN (?)";

		where = "a." + db.DT_KEYTOV + " IN (" + nestedSQL + ") AND b."
				+ DB.fDate() + " BETWEEN ? AND ?";
		where_arg = new String[] { item.get(db._ID), startDate + " 00:00:00",
				endDate + " 23:59:59" };

		// если выбран опред. товар, то модифицируем строку запроса
		if (isGroup == 0) {
			where = "a." + db.DT_KEYTOV + " = ? AND b." + DB.fDate()
					+ " BETWEEN ? AND ?";
			where_arg = new String[] { item.get(db._ID),
					startDate + " 00:00:00", endDate + " 23:59:59" };
		}
	}

	private void execute() {
		if (item.size() == 0) {
			showMassage(c, "Не выбран товар/группа!");
			return;
		}
		DB.open();
		/*
		 * String innerJoin = "INNER JOIN " + DB.tblDH() + " b ON a." +
		 * db.DT_KEYZ + " = b." + db._ID; String[] col = new String[] { "SUM(a."
		 * + DB.fCount() + ") AS " + DB.fCount(), "SUM(a." + DB.fSum() + ") AS "
		 * + DB.fSum() }; String nestedSQL = "SELECT " + db._ID + " FROM " +
		 * DB.tblTovar() // + " WHERE " + db.TOV_CODEPARENT + " IN (?)"; +
		 * " WHERE " + db.TOV_CODEROOT + " IN (?)"; String where = "a." +
		 * db.DT_KEYTOV + " IN (" + nestedSQL + ") AND b." + DB.fDate() +
		 * " BETWEEN ? AND ?"; String[] where_arg = new String[] {
		 * item.get(db._ID), startDate + " 00:00:00", endDate + " 23:59:59" };
		 */

		setStrSql(DB);
		DB.setField(DB.tblDT() + " a " + innerJoin, column, where, where_arg,
				null, null, null);
		Cursor cur = DB.doSelect();

		if (cur.moveToFirst()) {
			showLog(TAG, cur.getCount() + "");
			int index = cur.getColumnIndex("fCount");
			totalCount = cur.getDouble(index);

			index = cur.getColumnIndex("fSum");
			totalSum = cur.getDouble(index);
		}

		DB.close();
		setTotal();
	}

	private void doOnClickButton(Button b) {
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.report_bt_period:
					openPeriodDialog();
					showLog(TAG, "Выбор периода");
					break;
				case R.id.report_bt_execute:

					showLog(TAG, "Выполнить");
					execute();
					break;
				default:
					break;
				}
			}
		});
	}

	private void doSetOnclick(View v) {
		switch (v.getId()) {
		case R.id.report_bt_period:
			doOnClickButton((Button) v);
			break;

		case R.id.report_bt_execute:
			doOnClickButton((Button) v);
			break;
		case R.id.report_sp_tovar:
			doOnTouchSpinner((Spinner) v);
			break;

		default:
			break;
		}

	}

	private void clearItem() {
		item.clear();
	}

	private void addItem(String key, String val) {
		item.put(key, val);
	}

	private void addItemToSpinner() {
		listMap.clear();
		listMap.add(item);
		sa.notifyDataSetChanged();
	}

	private void setAdapterForSpinner(Spinner sp) {
		sa = new SimpleAdapter(c, listMap,
				android.R.layout.simple_spinner_item, new String[] { "fName" },
				new int[] { android.R.id.text1 });
		// sa.notifyDataSetChanged()
		sp.setAdapter(sa);
	}

	private void initWidget() {
		c = this.getApplicationContext();
		tvPeriod = (TextView) findViewById(R.id.report_tv_period);
		tvCount = (TextView) findViewById(R.id.report_tv_count);
		tvSum = (TextView) findViewById(R.id.report_tv_sum);

		btPeriod = (Button) findViewById(R.id.report_bt_period);
		btExecute = (Button) findViewById(R.id.report_bt_execute);

		spTovar = (Spinner) findViewById(R.id.report_sp_tovar);
		setAdapterForSpinner(spTovar);
		doSetOnclick(btPeriod);
		doSetOnclick(btExecute);
		doSetOnclick(spTovar);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports);
		
		initWidget();
		DB = new db(c);
		
		startDate = Utils.getCurentDate("yyyy-MM-dd");
		endDate = startDate;
		tvPeriod.setText("Дата: " + startDate + " по " + endDate + "");
		setTotal();
	}
}
