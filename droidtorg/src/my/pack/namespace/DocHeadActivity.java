package my.pack.namespace;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DocHeadActivity extends NewZayavkaActivity {
	private final static int REQUEST_DATE = 1;
	private final static int REQUEST_CLIENT = 2;
	private final static int REQUEST_MAG = 3;
	private final static String TAG = "DocHeadActivity";
	public Spinner spPartner, spDate, spMagazin;
	public EditText etNote;
	public Context contextDocHead;
	public db DBDocHead;
	private Cursor curDocHead;
	public String table_name = "tblCounteragent";
	public String colName = "fName";
	public String colSum = "fSum";

	protected Calendar cal = Calendar.getInstance();
	protected int DIALOG_DATE = 1;
	protected int dateYear, dateMonth, dateDay;

	protected int ID_CLIENT = 0;
	protected int ID_MAG = 0;
	// адаптеры для списков шапки документа
	protected ArrayAdapter<String> spinnersAdapter;

	// @SuppressWarnings("unchecked")
	protected void addItemToAdapter(String value, Spinner sp) {
		ArrayAdapter<String> adapter;
		adapter = (ArrayAdapter<String>) sp.getAdapter();

		adapter.clear();
		if (value == "") {
			// не добовляем
		} else {
			adapter.add(value);
		}

	}

	// public void setAdapterForSpinner(Spinner sp, ArrayAdapter<String> adap) {
	public void setAdapterForSpinner(Spinner sp) {
		// String[] selectItem = { "aaa" };
		ArrayList<String> selectItem = new ArrayList<String>();
		// selectItem.add("sdsd");
		spinnersAdapter = new ArrayAdapter<String>(contextDocHead,
				android.R.layout.simple_spinner_item, selectItem);
		spinnersAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(spinnersAdapter);
		// showLog(TAG, "" + spinnersAdapter.getCount() + "");

	}

	public String setDateFormat(String tempFormat) {
		Time dateFormat = new Time();
		dateFormat.set(dateDay, dateMonth, dateYear);
		long dt = dateFormat.toMillis(true);
		String strFormat = (String) DateFormat.format(tempFormat, dt);
		return strFormat;
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_DATE) {
			DatePickerDialog tpd = new DatePickerDialog(this, dateCallBack,
					dateYear, dateMonth, dateDay);
			return tpd;
		}
		return super.onCreateDialog(id);
	}

	private DatePickerDialog.OnDateSetListener dateCallBack = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			dateYear = year;
			dateMonth = monthOfYear;
			dateDay = dayOfMonth;

			// showLog(TAG, "");

			addItemToAdapter(setDateFormat("yyyy-MM-dd"), spDate);
			// setAdapterForSpinner(spDate, setDateFormat("yyyy-MM-dd"));
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String[] selectItem = { "" };
		if (resultCode == RESULT_OK) {

			String fName = data.getStringExtra("fName");

			switch (requestCode) {
			case REQUEST_DATE:
			//	showLog(TAG, "date");

				break;

			case REQUEST_CLIENT:

				// получаем из другой формы ID контрагента
				ID_CLIENT = (int) data.getLongExtra("_id", 0);

				addItemToAdapter(fName, spPartner);

				// очищаем инф. о выбранном магазине, т.к. изменился клиент
				addItemToAdapter("", spMagazin);
				ID_MAG = 0;

				break;

			case REQUEST_MAG:
				ID_MAG = (int) data.getLongExtra("_id", 0);

				addItemToAdapter(fName, spMagazin);
				// showLog(TAG, "пришли данные о выборе магазина " + ID_MAG +
				// "");
				break;

			default:
				// showLog(TAG, "не известно от кого пришли данные");
				break;
			}
			// showLog(TAG, "Место после break");
		} else {

		}

	}

	// @Override
	public void but(View v) {
		Toast.makeText(contextDocHead, "hello", 1000).show();
	}

	public void refreshCursor() {
		stopManagingCursor(curDocHead);
		curDocHead = DBDocHead.getAllData(table_name);
		startManagingCursor(curDocHead);
	}

	public void doSetOnTouchListener(Spinner spSpinner) {
		spSpinner.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent selectedActivity = null;

				switch (v.getId()) {
				case R.id.spPartnerDocHead:
					selectedActivity = new Intent(contextDocHead,
							CounteragentActivity.class);
					startActivityForResult(selectedActivity, REQUEST_CLIENT);
					break;

				case R.id.spMagazinDocHead:
					// showLog(TAG, "" + ID_CLIENT + "");

					if (ID_CLIENT == 0) {
						break;
					}
					selectedActivity = new Intent(contextDocHead,
							MagazinActivity.class);
					selectedActivity.putExtra("ID_CLIENT", ID_CLIENT);
					startActivityForResult(selectedActivity, REQUEST_MAG);

					break;

				case R.id.spDateDocHead:
					// showLog(TAG, "date");
					// DateDialog date = new DateDialog(contextDocHead);
					showDialog(DIALOG_DATE);
					// selectedActivity = new Intent(contextDocHead,
					// DateDialog.class);
					// startActivity(selectedActivity);

					break;
				default:
					// showLog(TAG, "нет описания для " + v.toString());
					break;
				}
				// showLog(TAG, "start activity");

				return false;
			}
		});

	}

	public void do_setOnClickListener(Spinner myview) {
		myview.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(contextDocHead, "click", 1000).show();
			}
		});
	}

	public void do_setOnItemClickListener(Spinner spSpinner) {

		spSpinner.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(contextDocHead, "ee", 1000).show();
			}

		});
	}

	public void do_setOnItemSelectedListener(Spinner spSpinner) {
		spSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(contextDocHead, "" + arg2 + "", 1000).show();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void init_widget() {

		contextDocHead = this.getApplicationContext();
		spPartner = (Spinner) findViewById(R.id.spPartnerDocHead);
		spMagazin = (Spinner) findViewById(R.id.spMagazinDocHead);
		spDate = (Spinner) findViewById(R.id.spDateDocHead);
		etNote = (EditText) findViewById(R.id.etDocHeaderNote);
		// etNote.setScroller(new Scroller(contextDocHead));
		// etNote.setMaxLines(5);
		// etNote.setVerticalScrollBarEnabled(true);
		// etNote.setMovementMethod(new ScrollingMovementMethod());

		dateYear = cal.get(Calendar.YEAR);
		dateMonth = cal.get(Calendar.MONTH);
		dateDay = cal.get(Calendar.DAY_OF_MONTH);

		setAdapterForSpinner(spDate);
		addItemToAdapter(setDateFormat("yyyy-MM-dd"), spDate);
		setAdapterForSpinner(spPartner);
		setAdapterForSpinner(spMagazin);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.doc_header);

		init_widget();
		doSetOnTouchListener(spDate);
		doSetOnTouchListener(spPartner);
		doSetOnTouchListener(spMagazin);

	}
}
