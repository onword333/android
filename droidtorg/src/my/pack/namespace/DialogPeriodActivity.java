package my.pack.namespace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

public class DialogPeriodActivity extends Main {
	public final static String TAG = "DialogPeriodActivity";

	// диалог для начальной даты
	public final static int DIALOG_DATA_PICKER_START = 1;

	// диалог для конечной даты
	public final static int DIALOG_DATA_PICKER_END = 2;

	// выбранный диалог, по умолчанию - начальная дата
	public int curentDialog = DIALOG_DATA_PICKER_START;

	// кнопка подтверждения выбора даты
	public Button btOk;

	// контекст класса
	private Context c;

	// суда будет записываться начальная дата
	private final ArrayList<String> alSpStart = new ArrayList<String>();
	// суда будет записываться конечная дата
	private final ArrayList<String> alSpEnd = new ArrayList<String>();

	private int year, monthOfYear, dayOfMonth;
	private int year2, monthOfYear2, dayOfMonth2;

	// начальная и конечная дата
	private Spinner spStartDate, spEndDate;

	private void parseDate(String s, String e, String mask)
			throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(mask);
		Date d1 = df.parse(s);
		Date d2 = df.parse(e);

		Calendar c = Calendar.getInstance();
		c.setTime(d1);

		year = c.get(Calendar.YEAR);
		monthOfYear = c.get(Calendar.MONTH);
		dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

		showLog(TAG, year + " " + monthOfYear + " " + dayOfMonth + " " + year2
				+ " " + monthOfYear2 + " " + dayOfMonth2);
		c.setTime(d2);
		year2 = c.get(Calendar.YEAR);
		monthOfYear2 = c.get(Calendar.MONTH);
		dayOfMonth2 = c.get(Calendar.DAY_OF_MONTH);
		setDefaultDate();

		showLog(TAG, year + " " + monthOfYear + " " + dayOfMonth + " " + year2
				+ " " + monthOfYear2 + " " + dayOfMonth2);
	}

	/**
	 * Описание - проверяет корректность введенного периода
	 * 
	 * @param startDate - начальная дата
	 * @param endDate - конечная дата
	 * @return true - период задан коректно, false - период задан не правильно
	 * @throws ParseException
	 */
	private boolean checkDate(String startDate, String endDate)
			throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = df.parse(startDate);
		Date d2 = df.parse(endDate);

		// если начальная дата меньше или равна конечной,
		// тогда период задан верно иначе нет
		if ((d1.before(d2)) || (d1.equals(d2))) {
			return true;
		} else {
			return false;
		}
	}

	private DatePickerDialog.OnDateSetListener dateSet = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// в зависимости от далога устанавливаем дату в нужный Spinner
			switch (curentDialog) {
			case DIALOG_DATA_PICKER_START:
				alSpStart.clear();
				alSpStart.add(setDateFormat("yyyy-MM-dd", dayOfMonth,
						monthOfYear, year));
				doSetAdapterToSpinner(spStartDate);
				break;
			case DIALOG_DATA_PICKER_END:
				alSpEnd.clear();
				alSpEnd.add(setDateFormat("yyyy-MM-dd", dayOfMonth,
						monthOfYear, year));
				doSetAdapterToSpinner(spEndDate);
				break;
			default:
				break;
			}

		}
	};

	protected void onPrepareDialog(int id, Dialog dialog) {
		// определяем какой был вызван диалог
		curentDialog = id;
		switch (id) {
		case DIALOG_DATA_PICKER_START:

			break;
		case DIALOG_DATA_PICKER_END:

			break;
		default:
			break;
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_DATA_PICKER_START:
			dialog = new DatePickerDialog(this, dateSet, year, monthOfYear,
					dayOfMonth);

			break;
		case DIALOG_DATA_PICKER_END:
			dialog = new DatePickerDialog(this, dateSet, year2, monthOfYear2,
					dayOfMonth2);

			break;
		default:
			break;
		}

		// return super.onCreateDialog(id);
		return dialog;
	}

	private void addValueToAdapter(Spinner sp) {

		// sp.setAdapter(adapter)
		// adapter.

	}

	private void doSetAdapterToSpinner(Spinner sp) {
		ArrayList<String> selectItem = alSpStart;
		switch (sp.getId()) {
		case R.id.dialog_period_sp_date_start:
			selectItem = alSpStart;
			break;
		case R.id.dialog_period_sp_date_end:
			selectItem = alSpEnd;
			break;
		default:
			break;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, selectItem);

		sp.setAdapter(adapter);
	}

	private void doSetOnToch(Spinner sp) {
		sp.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (v.getId()) {
				case R.id.dialog_period_sp_date_start:
					showDialog(DIALOG_DATA_PICKER_START);
					Log.d(TAG, "Нач дата");
					break;

				case R.id.dialog_period_sp_date_end:
					showDialog(DIALOG_DATA_PICKER_END);
					Log.d(TAG, "Кон дата");
					break;

				default:
					break;
				}
				return false;
			}
		});

	}

	private void doSetOnClick(Button bt) {
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.dialog_period_bt_ok:
					boolean isOk = false;
					try {
						isOk = checkDate(spStartDate.getItemAtPosition(0)
								.toString(), spEndDate.getItemAtPosition(0)
								.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (isOk) {
						Intent intent = new Intent();
						intent.putExtra(START_DATE, spStartDate
								.getItemAtPosition(0).toString());
						intent.putExtra(END_DATE, spEndDate
								.getItemAtPosition(0).toString());
						setResult(RESULT_OK, intent);
						finish();

						Log.d(TAG, "Период правильно задан");
					} else {
						showMassage(c, "Период задан не правильно!");
					}

					break;

				default:
					break;
				}

			}
		});
	}

	private String setDateFormat(String mask, int dayOfMonth, int monthOfYear,
			int year) {
		Time dateFormat = new Time();
		dateFormat.set(dayOfMonth, monthOfYear, year);

		long dt = dateFormat.toMillis(true);
		String strFormat = (String) DateFormat.format(mask, dt);
		return strFormat;
	}

	private void setDefaultDate() {
		String date = setDateFormat("yyyy-MM-dd", dayOfMonth, monthOfYear, year);
		String date2 = setDateFormat("yyyy-MM-dd", dayOfMonth2, monthOfYear2,
				year2);
		alSpStart.add(date);
		alSpEnd.add(date2);
	}

	/**
	 * Описание- определяет текущую дату
	 */
	private void getCurentDate() {
		final Calendar cal = Calendar.getInstance();

		year = cal.get(Calendar.YEAR);
		monthOfYear = cal.get(Calendar.MONTH);
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		year2 = year;
		monthOfYear2 = monthOfYear;
		dayOfMonth2 = dayOfMonth;

		setDefaultDate();
	}

	private void doSetAdapter() {
		doSetAdapterToSpinner(spStartDate);
		doSetAdapterToSpinner(spEndDate);
	}

	/**
	 * Описание - инициализация всех прослушивателей
	 */
	private void initOnclickListener() {
		doSetOnClick(btOk);
		doSetOnToch(spStartDate);
		doSetOnToch(spEndDate);
	}

	private void initWidget() {
		c = this.getApplicationContext();
		btOk = (Button) findViewById(R.id.dialog_period_bt_ok);
		spStartDate = (Spinner) findViewById(R.id.dialog_period_sp_date_start);
		spEndDate = (Spinner) findViewById(R.id.dialog_period_sp_date_end);

		Intent intent = getIntent();

		// получаем текущую дату, если дата не передана, то получаем текущую дату
		String startDate = intent.getStringExtra(START_DATE);
		String endDate = intent.getStringExtra(END_DATE);
		if ((startDate == null) || (endDate == null)) {
			getCurentDate();
		} else {
			try {
				parseDate(startDate, endDate, "yyyy-MM-dd");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// инициализация прослушивателей
		initOnclickListener();

		// устанавливаем адаптеры
		doSetAdapter();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_period);
		initWidget();

	}

}
