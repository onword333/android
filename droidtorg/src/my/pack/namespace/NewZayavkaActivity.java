package my.pack.namespace;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import my.pack.adapter.DocTableAdapter;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class NewZayavkaActivity extends Main {
	private final static String TAG = "NewZayavka";

	// был ли документ изменен: 0 - нет, 1 - был изменен
	private int is_changed = 0;

	/**
	 * Флаг определяет был ли создан новый документ true - да, false - нет
	 */
	private boolean isNewDoc = true;

	/**
	 * Возможно ли редактирование документа false - нет, true - да (по
	 * умолчанию)
	 */
	private boolean editIsPossible = true;

	private final static int REQUEST_DATE = 1;
	private final static int REQUEST_CLIENT = 2;
	private final static int REQUEST_MAG = 3;
	private final static int REQUEST_ADD_ORDER = 4;

	protected final static int DIALOG_DATE = 1;
	protected final static int DIALOG_SAVE = 2;

	// protected int DOC_NUMBER, ID_CLIENT, ID_MAG = 0;

	// ISEDIT - определяет нужно ли обновлять строку документа
	// 0 - добавляем новую строку, 1 - обновляем выбранную
	// _id - id строки обновления (tblDocTable), по умолчанию 0
	protected int ISEDIT = 0;
	protected int _id = 0;

	protected int ID_CLIENT, ID_MAG = 0;
	protected int ID_TOV, ID_EDIZM, ID_EDIZMKR;
	protected int fTypePrice = 0;

	float KRATNOST = 1.00f;

	protected double COUNT, PRICE, SUM, TOTAL_SUM = 0;

	public Context contextNewZayavka;
	protected TabHost tabHost;
	protected Spinner spDate, spPartner, spMagazin;
	protected ListView docTableOrderList, lvTovar_list;
	protected EditText etNote;
	protected TextView tvDocHeadDocNum, tvDebts, tvDocHeaderTotalSum;
	protected int dateYear, dateMonth, dateDay;
	protected Calendar cal = Calendar.getInstance();
	protected ArrayAdapter<String> spinnersAdapter;
	protected ArrayList<String> listItem = new ArrayList<String>();
	protected db DB;
	protected Cursor cursorHead, cursorOrder;// cursorDoc;
	protected DocTableAdapter orderAdapter, docTableAdapter;
	protected ContentValues cv;
	protected String[] from;
	protected int[] to;

	DecimalFormat nf = new DecimalFormat("#,##0.00");

	public Button btDocHeadSave;
	Intent intentHead;

	/**
	 * Описание - включает/отключает виджет
	 * @param flag; true - виджет активный, false - виджет не активный
	 */
	public void setEnable(boolean flag) {
		docTableOrderList.setEnabled(flag);
		spDate.setEnabled(flag);
		spMagazin.setEnabled(flag);
		spPartner.setEnabled(flag);
		etNote.setEnabled(flag);
	}

	protected void do_addTextChanged(EditText et) {

		et.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				is_changed = 1;
				// showLog(TAG, "onTextChanged");

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				is_changed = 1;
				// showLog(TAG, "beforeTextChanged");
			}

			public void afterTextChanged(Editable s) {
				is_changed = 1;
				// showLog(TAG, "afterTextChanged");
			}
		});
	}

	protected void saveDoc(String tblName) {
		if (DB.isExistsDH()) {
			DB.updateRow(tblName);
		} else {
			DB.insertRow(tblName);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.it_delete:
			DB.setField(DB.tblDT(), null, "" + DB.fID() + " = ?",
					new String[] { "" + _id + "" }, null, null, null);
			int delRows = DB.doDelete();

			is_changed = 1;
			// обновляем общую сумму т.к. была удалена позиция
			update_widget();

			// if (delRows > 0) {
			refreshOrder();
			// }
			showMassage(contextNewZayavka, "удалено строк: " + delRows);
			showLog(TAG, "onContextItemSelected" + " удалить " + _id);
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.document_context, menu);
	}

	protected void do_setOnItemLognClick(ListView lv) {
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				_id = (int) arg3;
				return false;
			}

		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.itSearch:

		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(contextNewZayavka.INPUT_METHOD_SERVICE);
		// imm.showSoftInput(lvTovar_list, InputMethodManager.SHOW_FORCED);
		// break;
		case R.id.it_choice:

			// если партнер не выбран, то запрещаем делать подбор
			if (spPartner.getCount() == 0) {
				Utils.ShowMsg(contextNewZayavka, "Партнер не выбран.");
				break;
			}
			Intent intent_ = new Intent(contextNewZayavka,
					NomenclatureActivity.class);
			intent_.putExtra(OPEN_FROM, DOCUMENT);
			intent_.putExtra(DB.fID(), DB.ID_DOC);
			intent_.putExtra(DB.fTypePrice(), fTypePrice);
			startActivity(intent_);

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// если запрещено редактирование, то не отображаем подбор
		if (editIsPossible == false) {
			return false;
		}
		getMenuInflater().inflate(R.menu.document, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/* if (is_changed == 1) { showDialog(DIALOG_SAVE); } else {
			 * NewZayavkaActivity.this.finish(); } */
			showDialog(DIALOG_SAVE);
			return false;

		}
		return super.onKeyDown(keyCode, event);
	}

	// protected update() {
	// cv.clear();
	//
	// }

	/**
	 * Описание: строка шапки документа - готовит строку для вставки в базу и
	 * возвращачет объект с привязкой к опред. полю.
	 */
	protected void docHeadField() {
		showLog(TAG, "docHeadField" + is_changed);
		// по умолчанию документ считается отправленным,
		// если док. был изменен то пометить его как неотправленный
		String stutusDoc = "";
		Utils.ShowLog(TAG + " isNewDoc", isNewDoc + " ");
		if (isNewDoc) {
			stutusDoc = "";
		}

		DB.clearCV();
		DB.addCV(DB.fID(), DB.ID_DOC);
		DB.addCV(DB.fkPartner(), ID_CLIENT);
		// DB.addCV(DB.fDate(), setDateFormat("yyyy-MM-dd"));
		String strDate = spDate.getItemAtPosition(0).toString() + " 00:00:00";
		try {
			strDate = Utils.convertDate(strDate, "yyyy-MM-dd HH:mm:ss",
					"yyyy-MM-dd HH:mm:ss");
			// Utils.ShowMsg(contextNewZayavka, "Успешное форматирования");
		} catch (ParseException e) {
			Utils.ShowMsg(contextNewZayavka,
					"Ошибка форматирования. " + e.getMessage());
			e.printStackTrace();
		}
		DB.addCV(DB.fDate(), strDate);
		DB.addCV(DB.fSum(), TOTAL_SUM);
		DB.addCV(DB.fOut(), stutusDoc);
		DB.addCV(DB.fNote(), etNote.getText().toString());
		DB.addCV(DB.fkMag(), ID_MAG);
	}

	/**
	 * Описание: строка таб. части документа - готовит строку для вставки в базу
	 * и возвращачет объект с привязкой к опред. полю.
	 */
	protected void docTableField() {
		// DB.clearCV();
		// if (ID_ROW > 0) {
		// DB.addCV(DB.fID(), ID_ROW);
		// }
		//
		// showLog("DOC_TABLE", ID_ROW + "");
		//
		// DB.addCV(DB.fIdDoc(), DB.ID_DOC);
		// DB.addCV(DB.fIdTov(), ID_TOV);
		// DB.addCV(DB.fCount(), COUNT);
		// DB.addCV(DB.fPrice(), PRICE);
		// DB.addCV(DB.fSum(), SUM);
		// DB.addCV(DB.fIdEdIzm(), ID_EDIZM);
		// DB.addCV(DB.fIdEdIzmKr(), ID_EDIZMKR);
		// DB.addCV(DB.fKrat(), KRATNOST);
		//
		// showLog("doc table",
		// "" + DB.fIdDoc() + " = " + DB.ID_DOC + "\r\n" + DB.fIdTov()
		// + " = " + ID_TOV + "\r\n"
		//
		// + DB.fIdEdIzm() + " = " + ID_EDIZM + "\r\n"
		// + DB.fIdEdIzmKr() + " = " + ID_EDIZMKR + "\r\n"
		// + DB.fPrice() + " = " + PRICE + "\r\n" + DB.fCount()
		// + " = " + COUNT + "\r\n" + DB.fKrat() + " = "
		// + KRATNOST + "\r\n" + DB.fSum() + " = " + SUM + "");
	}

	public void refreshOrder() {
		stopManagingCursor(cursorOrder);
		cursorOrder = DB.getOrder();
		if (orderAdapter != null) {
			orderAdapter.changeCursor(cursorOrder);
		}
		startManagingCursor(cursorOrder);
	}

	protected void prepareRowsOrder(ListView lv) {
		from = new String[] { DB.fName(), DB.fCount(), DB.fPrice(), DB.fSum() };
		to = new int[] { R.id.tv1Myitem, R.id.tv3Myitem, R.id.tv4Myitem,
				R.id.tv5Myitem };

		// showLog(TAG, tblName);
		refreshOrder();
		orderAdapter = new DocTableAdapter(contextNewZayavka, R.layout.myitem,
				cursorOrder, from, to);

		lv.setAdapter(orderAdapter);
	}

	public void refreshCursor(String tblName) {
		// stopManagingCursor(cursorDoc);
		// cursorDoc = DB.getTov("' '", "fName");
		// startManagingCursor(cursorDoc);
	}

	protected void prepareSelection(ListView lv) {
		// from = new String[] { DB.fName(), DB.fPrice(), DB.fCount() };
		// to = new int[] { R.id.tv1Myitem, R.id.tv4Myitem, R.id.tv5Myitem };
		//
		// // showLog(TAG, tblName);
		// refreshCursor(DB.tblTov());
		// docTableAdapter = new DocTableAdapter(contextNewZayavka,
		// R.layout.myitem, cursorDoc, from, to);
		//
		// lv.setAdapter(docTableAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// String[] selectItem = { "" };
		if (resultCode == RESULT_OK) {
			//
			double debts = 0.00d;
			String fName = data.getStringExtra("fName");
			//
			switch (requestCode) {
			case REQUEST_DATE:
				// showLog(TAG, "REQUEST_DATE date");
				//

				break;
			//
			case REQUEST_CLIENT:
				//
				// получаем из другой формы ID контрагента
				ID_CLIENT = (int) data.getLongExtra("_id", 0);
				fTypePrice = data.getIntExtra(DB.fTypePrice(), ZERO);
				addItemToAdapter(fName, spPartner);

				// очищаем инф. о выбранном магазине, т.к. изменился клиент
				addItemToAdapter("", spMagazin);
				ID_MAG = 0;

				// debts = getDebts(ID_CLIENT, ID_MAG);
				// tvDebts.setText(Utils.formatNumber(debts, "#,##0.00"));

				docHeadField();
				saveDoc(DB.tblDH());

				// showLog(TAG, "" + ID_CLIENT + "");
				is_changed = 1;
				break;
			//
			case REQUEST_MAG:
				ID_MAG = (int) data.getLongExtra("_id", 0);

				addItemToAdapter(fName, spMagazin);
				// showLog(TAG, "пришли данные о выборе магазина " + ID_MAG +
				// "");
				docHeadField();
				saveDoc(DB.tblDH());

				// debts = getDebts(ID_CLIENT, ID_MAG);
				// tvDebts.setText(Utils.formatNumber(debts, "#,##0.00"));
				is_changed = 1;
				break;
			//
			case REQUEST_ADD_ORDER:
				// docHeadField();
				// saveDoc(DB.tblDH());
				update_widget();
				is_changed = data.getIntExtra(IS_EDIT, 0);
				// ID_EDIZM = data.getIntExtra(DB.fIdEdIzm(), 0);
				// ID_EDIZMKR = data.getIntExtra(DB.fIdEdIzmKr(), 0);
				// PRICE = data.getDoubleExtra(DB.fPrice(), 0);
				// COUNT = data.getDoubleExtra(DB.fCount(), 0);
				// KRATNOST = data.getFloatExtra(DB.fKrat(), 1.00f);
				//
				// SUM = data.getDoubleExtra(DB.fSum(), 0);
				//
				// docTableField();
				// if (ISEDIT == 0) {
				// Log.d("ISEDIT", "Нвая строка");
				// DB.insertRow(DB.tblDT());
				// } else {
				// Log.d("ISEDIT", "обновление строки");
				// DB.updateRow(DB.tblDT());
				// ISEDIT = 0;
				// }
				//
				// update_widget();
				//
				// showLog(TAG,
				// "" + DB.fIdEdIzm() + " = " + ID_EDIZM + "\r\n"
				// + DB.fIdEdIzmKr() + " = " + ID_EDIZMKR + "\r\n"
				// + DB.fPrice() + " = " + PRICE + "\r\n"
				// + DB.fCount() + " = " + COUNT + "\r\n"
				// + DB.fKrat() + " = " + KRATNOST + "\r\n"
				// + DB.fSum() + " = " + SUM + "");
				// is_changed = 1;
				//
				// break;
				//
				// default:
				// // showLog(TAG, "не известно от кого пришли данные");
				// break;
				// }
				// // showLog(TAG, "Место после break");
				// } else {
				//
				// }
			}
			debts = getDebts(ID_CLIENT, ID_MAG);
			tvDebts.setText(Utils.formatNumber(debts, "#,##0.00"));
		}
		// update_widget();
	}

	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_DATE:
			DatePickerDialog tpd = new DatePickerDialog(this, dateCallBack,
					dateYear, dateMonth, dateDay);

			return tpd;

		case DIALOG_SAVE:
			Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setMessage(R.string.str_doc_close).setCancelable(false);
			saveDialog.setPositiveButton(R.string.str_save_yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							/* docHeadField(); if (DB.isExistsDH()) {
							 * DB.updateRow(DB.tblDH()); } else {
							 * DB.insertRow(DB.tblDH()); } */

							if (is_changed == 1) {
								docHeadField();
								saveDoc(DB.tblDH());
							}

							NewZayavkaActivity.this.finish();

							// было изменение в документе
							// is_changed = 1;
						}
					});
			/* saveDialog.setNeutralButton(R.string.str_save_cancel, new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) { // TODO
			 * Auto-generated method stub
			 * 
			 * } }); */
			saveDialog.setNegativeButton(R.string.str_save_no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							/* if (intentHead.getStringExtra("action").equals(
							 * res.getString(R.string.str_new_zayavka))) { //
							 * showLog(TAG, "Новый документа"); }
							 * DB.deleteDoc(); */

							// NewZayavkaActivity.this.finish();
						}
					});
			return saveDialog.create();
		}
		// if (id == DIALOG_DATE) {
		// DatePickerDialog tpd = new DatePickerDialog(this, dateCallBack,
		// dateYear, dateMonth, dateDay);
		// return tpd;
		// }
		return super.onCreateDialog(id);
	}

	private DatePickerDialog.OnDateSetListener dateCallBack = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			dateYear = year;
			dateMonth = monthOfYear;
			dateDay = dayOfMonth;

			addItemToAdapter(setDateFormat("yyyy-MM-dd"), spDate);
			// setAdapterForSpinner(spDate, setDateFormat("yyyy-MM-dd"));
			is_changed = 1;
			docHeadField();
			saveDoc(DB.tblDH());

		}
	};

	private Resources res;

	protected void do_setOnItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				cursorOrder.moveToPosition(arg2);
				int _id = (int) arg3;

				int idTovIndex = cursorOrder.getColumnIndex(DB.fIdTov());
				int idTov = cursorOrder.getInt(idTovIndex);

				int fNameIndex = cursorOrder.getColumnIndex(DB.fName());
				String fName = cursorOrder.getString(fNameIndex);

				int fCountIndex = cursorOrder.getColumnIndex(DB.fCount());
				double fCount = cursorOrder.getFloat(fCountIndex);

				int kratnostIndex = cursorOrder.getColumnIndex(DB.fKrat());
				float kratnost = cursorOrder.getFloat(kratnostIndex);

				int priceIndex = cursorOrder.getColumnIndex(DB.fPrice());
				float price = cursorOrder.getFloat(priceIndex);

				Intent addOrder = new Intent(contextNewZayavka, AddOrder.class);
				addOrder.putExtra(IS_EDIT, ONE);

				addOrder.putExtra(DB.fID(), _id);
				addOrder.putExtra(DB.fID() + DB.tblTovar(), idTov);
				addOrder.putExtra(DB.fID() + DB.tblDH(), DB.ID_DOC);
				addOrder.putExtra(DB.fName(), fName);
				addOrder.putExtra(DB.fCount(), fCount);
				addOrder.putExtra(DB.fKrat(), kratnost);
				addOrder.putExtra(DB.fPrice(), price);

				startActivityForResult(addOrder, REQUEST_ADD_ORDER);
			}
		});
	}

	public void doSetOnTouchListener(Spinner spSpinner) {
		spSpinner.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				Intent selectedActivity = null;

				switch (v.getId()) {
				case R.id.spPartnerDocHead:
					if (docTableOrderList.getCount() > 0) {
						Utils.ShowMsg(contextNewZayavka,
								"Запрещено выбирать клиента при наличии строк заказа!");
						return false;
					}
					selectedActivity = new Intent(contextNewZayavka,
							CounteragentActivity.class);
					startActivityForResult(selectedActivity, REQUEST_CLIENT);
					break;

				case R.id.spMagazinDocHead:
					// showLog(TAG, "" + ID_CLIENT + "");

					if (ID_CLIENT == 0) {
						break;
					}
					if (docTableOrderList.getCount() > 0) {
						Utils.ShowMsg(contextNewZayavka,
								"Запрещено выбирать магазин при наличии строк заказа!");
						return false;
					}
					selectedActivity = new Intent(contextNewZayavka,
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

	// @SuppressWarnings("unchecked")
	protected void addItemToAdapter(String value, Spinner sp) {
		ArrayAdapter<String> adapter;
		adapter = (ArrayAdapter<String>) sp.getAdapter();
		adapter.clear();

		if (value == null) {
			// не добовляем
			Log.d(TAG, "Восстоновление не добовляем " + value);
		} else {
			Log.d(TAG, "Восстоновление добовляем " + value);
			adapter.add(value);
		}
	}

	public String setDateFormat(String tempFormat) {
		Time dateFormat = new Time();
		dateFormat.set(dateDay, dateMonth, dateYear);

		long dt = dateFormat.toMillis(true);
		String strFormat = (String) DateFormat.format(tempFormat, dt);
		return strFormat;
	}

	public void setAdapterForSpinner(Spinner sp) {

		ArrayList<String> selectItem = new ArrayList<String>();
		// selectItem.add("sdsd");
		spinnersAdapter = new ArrayAdapter<String>(contextNewZayavka,
				android.R.layout.simple_spinner_item, selectItem);
		spinnersAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(spinnersAdapter);
		// showLog(TAG, "" + spinnersAdapter.getCount() + "");

	}

	/** Описание: проверяет корректность заполнения полей документа */
	protected void checkDocField() {
		if (spPartner.getCount() == 0) {
			Utils.ShowMsg(contextNewZayavka, "Не выбран клиент!");
			tabHost.setCurrentTab(0);
		}
	}

	protected void doOnTabChanged(TabHost tab) {
		tab.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				checkDocField();
			}
		});
	}

	public void init_tabHost() {
		TabHost.TabSpec spec;
		res = getResources();
		tabHost = (TabHost) findViewById(R.id.TabHost1);
		tabHost.setup();

		spec = tabHost.newTabSpec("head").setIndicator(
				res.getString(R.string.str_doc_head));
		getLayoutInflater().inflate(R.layout.doc_header,
				tabHost.getTabContentView(), true);
		/* TextView v = new TextView(getApplicationContext());
		 * v.setText("Hello"); spec.setIndicator(v); */
		spec.setContent(R.id.linDocHead);

		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("table").setIndicator(
				res.getString(R.string.str_doc_table));
		getLayoutInflater().inflate(R.layout.doc_table,
				tabHost.getTabContentView(), true);

		spec.setContent(R.id.linDocTab);
		tabHost.addTab(spec);

		// проходим по вкладкам и меняем высоту
		for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
			// tabHost.getTabWidget().getChildAt(i).getLayoutParams().height =
			// 60;
		}

		/* spec = tabHost.newTabSpec("selection").setIndicator(
		 * res.getString(R.string.str_view_tovar));
		 * getLayoutInflater().inflate(R.layout.nomenclature,
		 * tabHost.getTabContentView(), true);
		 * spec.setContent(R.id.linNomenclature); tabHost.addTab(spec); */

		doOnTabChanged(tabHost);

		// tabHost.getTabWidget().getChildAt(0)
		// .setLayoutParams(new LinearLayout.LayoutParams(100, 40));
		// tabHost.getTabWidget().getChildAt(1)
		// .setLayoutParams(new LinearLayout.LayoutParams(100, 40));
		// tabHost.setCurrentTab(0);
	}

	protected void update_widget() {
		TOTAL_SUM = DB.getDtSum();
		tvDocHeaderTotalSum.setText(nf.format(TOTAL_SUM));
	}

	public void init_widget() {
		init_tabHost();
		// отключаем автофокус EditText
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		cv = new ContentValues();
		contextNewZayavka = this.getApplicationContext();
		tvDocHeadDocNum = (TextView) findViewById(R.id.tvDocHeadDocNum);
		tvDocHeaderTotalSum = (TextView) findViewById(R.id.tvDocHeaderTotalSum);
		tvDebts = (TextView) findViewById(R.id.tvDocHeadDebts);

		spDate = (Spinner) findViewById(R.id.spDateDocHead);
		spPartner = (Spinner) findViewById(R.id.spPartnerDocHead);
		spMagazin = (Spinner) findViewById(R.id.spMagazinDocHead);

		docTableOrderList = (ListView) findViewById(R.id.docTableOrderList);
		// lvTovar_list = (ListView) findViewById(R.id.lvTovar_list);

		etNote = (EditText) findViewById(R.id.etDocHeaderNote);
		do_addTextChanged(etNote);

		// btDocHeadSave = (Button) findViewById(R.id.btDocHeadSave);

		dateYear = cal.get(Calendar.YEAR);
		dateMonth = cal.get(Calendar.MONTH);
		dateDay = cal.get(Calendar.DAY_OF_MONTH);

		setAdapterForSpinner(spDate);
		setAdapterForSpinner(spPartner);
		setAdapterForSpinner(spMagazin);

		addItemToAdapter(setDateFormat("yyyy-MM-dd"), spDate);

		doSetOnTouchListener(spDate);
		doSetOnTouchListener(spPartner);
		doSetOnTouchListener(spMagazin);

		do_setOnItemClick(docTableOrderList);
		do_setOnItemLognClick(docTableOrderList);
		registerForContextMenu(docTableOrderList);
		// do_setOnItemClick(lvTovar_list);

		// do_setOnClickListener(btDocHeadSave);
	}

	/**
	 * Описание - получает долг в разрезе партнера, магазина
	 * 
	 */
	protected double getDebts(int idClient, int idMag) {

		double debts = 0.00d;
		String tbl = DbField.tblPartner;
		if (idMag > 0) {
			tbl = DbField.tblMag;
			idClient = idMag;

		}
		String col[] = new String[] { DbField.fRestThem };
		String where = DbField._id + " = " + idClient;
		DB.setField(tbl, col, where, null, null, null, null);
		Cursor tempCur = DB.doSelect();
		if (tempCur.moveToFirst()) {
			debts = tempCur
					.getDouble(tempCur.getColumnIndex(DbField.fRestThem));
		}
		tempCur.close();
		return debts;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_zayavka);
		res = getResources();
		intentHead = getIntent();
		// if (intentHead.getStringExtra(OPEN_FROM) == JOURNAL) {
		// newDocume
		// }

		// // нужно ли создавать новый документ
		// if (intentHead.getStringExtra("action").equals(
		// res.getString(R.string.str_new_zayavka))) {
		// // showLog(TAG, "Новый документа");
		// }

		init_widget();
		DB = new db(contextNewZayavka);
		DB.open();
		// cursorDoc = DB.getAllData(DB.TBL_DH);

		// получаем _id документа, иначе нужно получить номер нового документа.
		DB.ID_DOC = intentHead.getIntExtra(DB.fID() + DB.tblDH(),
				DB.idDoc() + 1);

		String str_num = getResources().getString(R.string.str_doc_num) + " "
				+ DB.ID_DOC;

		prepareRowsOrder(docTableOrderList);
		// prepareSelection(lvTovar_list);

		tvDocHeadDocNum.setText(str_num);
		// update_widget();

		// если в tblDocHead есть запись с DB.ID_DOC, то восстоновим ее
		cursorHead = DB.getDocHead();
		String val = "";
		int fk_id = 0;

		if (cursorHead.getCount() > 0) {
			cursorHead.moveToFirst();
			// отмечаем, что это существующий документ
			isNewDoc = false;

			// дата
			int ind = cursorHead.getColumnIndex(DB.fDate());
			val = cursorHead.getString(ind);

			try {
				val = Utils.convertDate(val, "yyyy-MM-dd", "yyyy-MM-dd");
				// Utils.ShowMsg(contextNewZayavka,
				// "Успешное форматирование даты. ");
			} catch (ParseException e) {

				Utils.ShowMsg(contextNewZayavka, "Ошибка форматирования даты. "
						+ e.getMessage());
				e.printStackTrace();
				return;
			}
			addItemToAdapter(val, spDate);

			// клиент
			ind = cursorHead.getColumnIndex(DB.fName());
			val = cursorHead.getString(ind);
			addItemToAdapter(val, spPartner);

			ind = cursorHead.getColumnIndex(DB.fTypePrice());
			fTypePrice = cursorHead.getInt(ind);

			ind = cursorHead.getColumnIndex(DB.fkPartner());
			fk_id = cursorHead.getInt(ind);
			ID_CLIENT = fk_id;

			// магазин
			ind = cursorHead.getColumnIndex(DB.fName2());
			val = cursorHead.getString(ind);
			addItemToAdapter(val, spMagazin);
			ind = cursorHead.getColumnIndex(DB.fkMag());
			fk_id = cursorHead.getInt(ind);
			ID_MAG = fk_id;

			// примечание
			ind = cursorHead.getColumnIndex(DB.fNote());
			val = cursorHead.getString(ind);

			etNote.setText(val);

			// устанавливаем статус документа 0 - не редактировался
			// это нужно сделать т.к. после etNote.setText(val) документ
			// считается измененным
			is_changed = 0;

			ind = cursorHead.getColumnIndex(DB.fOut());
			val = cursorHead.getString(ind);

			if (val.equalsIgnoreCase("X")) {
				editIsPossible = false;
			}
			double debts = getDebts(ID_CLIENT, ID_MAG);
			tvDebts.setText(Utils.formatNumber(debts, "#,##0.00"));

			setEnable(editIsPossible);
		}

		// docTableAdapter.setFilterQueryProvider(new FilterQueryProvider() {
		// public Cursor runQuery(CharSequence constraint) {
		// /*
		// * "SELECT a." + fID() + ", a." + fName() + ", a." + fCount() +
		// * ", a." + fIdEdIzm() + ", a." + fTypePrice() + " AS " +
		// * fPrice() + " FROM " + tblTov() + " a WHERE a." + fIsGroup() +
		// * " = " + where + order + "", null);
		// */
		//
		// String[] columns = new String[] { DB.fID(), DB.fName(),
		// DB.fCount(), DB.fIdEdIzm(),
		// DB.fTypePrice() + " AS " + DB.fPrice() };
		//
		// String where = "" + DB.fIsGroup() + " = ? AND + " + DB.fName()
		// + " LIKE ?";
		//
		// String[] where_arg = new String[] { " ",
		// "%" + constraint.toString().toUpperCase() + "%" };
		//
		// String order_by = "fName";
		// DB.setField(DB.tblTov(), columns, where, where_arg, null, null,
		// order_by);
		// if (!constraint.toString().equalsIgnoreCase("")) {
		// cursorDoc = DB.fetchCountriesByName(constraint.toString());
		// }
		//
		// // return counterDB.fetchCountriesByName(constraint.toString());
		//
		// return cursorDoc;
		// }
		// });

		showLog(TAG, "OnCreate");
		spPartner.setFocusableInTouchMode(false);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		update_widget();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		cursorOrder.close();
		DB.close();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
