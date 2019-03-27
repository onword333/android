package my.pack.namespace;

import java.io.IOException;
import java.util.HashMap;

import my.pack.adapter.TovarAdapter;
import my.pack.dialog.DialogFactory;
import my.pack.inet.Inet;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class NomenclatureActivity extends Main {
	private static final String TAG = "NomenclatureActivity";

	public Context ctxNomenclature;
	public ListView lvTovar_list;

	private String infoForUser = "";
	private String sysSep = System.getProperty("Separator");

	// private DocTableAdapter adap;
	private TovarAdapter adap;
	// курсор для списка тмц
	public Cursor cur;
	// курсор для сущ. групп тмц
	public Cursor curGroup;
	public db DB;
	protected String tbl_name, where, order_by, group_by, having;
	protected String[] columns, where_arg;
	protected Intent intent;
	protected Spinner grSpinner;

	// определяет - откуда пользователь открыл текущую Actyvity
	// MAIN_MENU - из гл. меню, DOCUMENT - из документа
	protected String openFrom = "";

	// id документа (tblDocHead), по умолчанию 0
	protected int idDoc = 0;

	protected View before;

	protected long lastarg;

	protected View current;

	protected HashMap<String, String> map_parametr = new HashMap<String, String>();

	private String host;
	private String resources;
	private int port;
	private int torg_id;
	private long selectedID = 0;
	private double selectedCount = 0.000;

	// id диалога
	protected final static int ID_DIALOG_ALERT = DialogFactory.DIALOG_ALERT;
	protected final static int ID_DIALOG_GROUP = 2;

	protected int lastrow = -1;

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.it_update:
			String rest = getCurrentRest(selectedID);
			if (rest != null) {
				try {
					selectedCount = Double.parseDouble(rest);
					DB.clearCV();
					DB.addCV(DbField.fCount, selectedCount);

					DB.setField(DbField.tblTovar, null, DbField._id + " = ?",
							new String[] { String.valueOf(selectedID) }, null,
							null, null);

					DB.doUpdate();

					stopManagingCursor(adap.getCursor());

					cur.requery();
					startManagingCursor(adap.getCursor());

					infoForUser = "Текущий остаток: "
							+ Utils.formatNumber(selectedCount, "#,##0.000");

				} catch (NumberFormatException ex) {
					infoForUser = "Ошибка конвертирования строки \r\n"
							+ ex.toString();
					// showDialog(ID_DIALOG_ALERT);
					// Utils.ShowMsg(ctxNomenclature, ex.toString());

				}

			}
			showDialog(ID_DIALOG_ALERT);
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
		getMenuInflater().inflate(R.menu.context_tovar, menu);
	}

	// достает из базы параметры отправки заявок
	// и заполняет ими HashMap map_parametr;
	// DB - ссылка на базу данных
	protected void getConstant(db DB) {
		int index, index_2;
		String val, val_2;
		Cursor cur = DB.getAllData("tblConstant");
		if (cur.moveToFirst()) {
			map_parametr.clear();
			do {
				index = cur.getColumnIndex(DB.fConstant[1]);
				index_2 = cur.getColumnIndex(DB.fConstant[3]);
				val = cur.getString(index);
				val_2 = cur.getString(index_2);
				map_parametr.put(val, val_2);
			} while (cur.moveToNext());
			host = map_parametr.get("host");
			resources = map_parametr.get("resources");
			port = Integer.parseInt(map_parametr.get("port"));
			torg_id = Integer.parseInt(map_parametr.get("torgID"));

		}
		cur.close();
	}

	protected String getCurrentRest(long id) {
		infoForUser = "";
		// Inet inet = new Inet("http://" + host + resources, port,
		// ctxNomenclature);
		Inet inet = new Inet(host, port, ctxNomenclature);
		inet.changeQuery(DbField.startQuery + DbField.query4 + "|" + torg_id
				+ "|" + id + DbField.endQuery);
		String answer = null;

		// try {
		// inet.getHost2();
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return answer;
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return answer;
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return answer;
		// } finally {
		// inet.closeConnect();
		// }

		try {
			inet.openSocket();
			inet.writeToSocket(255, inet.getOutpuStream());
			inet.shutDownOutput();
			inet.readFromSocket(255, inet.getInputStream());
			answer = inet.getByteOutput().toString(inet.ENCODING);
			// Utils.ShowLog(TAG + "----------- ", answer);
		} catch (IOException e) {

			e.printStackTrace();
			infoForUser = "Ошибка во время отправки/получения данных\r\n"
					+ e.toString();
		} finally {
			inet.closeConnect();
		}
		return answer;
	}

	/**
	 * Описание - долгое нажатие
	 * @param lv - ListView
	 */
	protected void do_setOnItemLognClick(ListView lv) {
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				cur.moveToPosition(arg2);
				selectedID = arg3;
				// getCurrentRest(arg3);

				// Utils.ShowMsg(ctxNomenclature, "arg2: " + arg2 + " arg3: "
				// + arg3);
				return false;
			}

		});

	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case ID_DIALOG_ALERT:
			((AlertDialog) dialog).setMessage(infoForUser);
			break;
		case ID_DIALOG_GROUP:

		default:
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		Builder builder = null;

		switch (id) {
		case ID_DIALOG_GROUP:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Выберите группу");
			// builder.setIcon(R.drawable.ic_launcher);

			builder.setCursor(curGroup, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) { // TODO
					curGroup.moveToPosition(which);

					int fIdIndex = curGroup.getColumnIndex(DB.fID());
					int fId = curGroup.getInt(fIdIndex);

					int fNameIndex = curGroup.getColumnIndex(DB.fName());
					String fName = curGroup.getString(fNameIndex);

					DB.setField(tbl_name, columns, DB.fIsGroup() + " = ? AND "
							+ "fCodeRoot = ?", new String[] { " ",
							"" + fId + "" }, null, null, order_by);

					cur = DB.doSelect();
					adap.changeCursor(cur);
					dialog.dismiss();

					// если пользователь пришел из отчета, то возвращаем
					// выбранное значение _id, fName
					if (openFrom.equalsIgnoreCase(REPORT)) {
						Intent intent = new Intent();
						intent.putExtra(DB.fID() + DB.tblTov(), fId);
						intent.putExtra(DB.fName(), fName);
						intent.putExtra(DB.fIsGroup(), 1);
						// intent.setClass(ctxNomenclature,
						// ReportActivity.class);
						setResult(RESULT_OK, intent);
						finish();
					}

					showLog(TAG, which + " " + fId);
				}
			}, "fName");
			return builder.create();

		case ID_DIALOG_ALERT:
			return DialogFactory.getDialogById(id, ctxNomenclature);
		default:
			break;
		}
		// return builder.create();
		return super.onCreateDialog(id);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itSearch:
			// Context context = getApplicationContext();
			InputMethodManager imm = (InputMethodManager) getSystemService(ctxNomenclature.INPUT_METHOD_SERVICE);
			imm.showSoftInput(lvTovar_list, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.it_group_search:

			showDialog(ID_DIALOG_GROUP);
			/* builder.setItems(items, new DialogInterface.OnClickListener() {
			 * public void onClick(DialogInterface dialog, int item) {
			 * Toast.makeText(getApplicationContext(), items[item],
			 * Toast.LENGTH_SHORT).show(); } }); */
			/* AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 * builder.setTitle("Выбор группы");
			 * 
			 * builder.setIcon(R.drawable.ic_launcher);
			 * 
			 * builder.setCursor(curGroup, new DialogInterface.OnClickListener()
			 * {
			 * 
			 * public void onClick(DialogInterface dialog, int which) { // TODO
			 * cur.moveToPosition(which); String text = cur.getString(0);
			 * showLog(TAG, which + " " + text); curGroup.close(); }
			 * 
			 * }, "fName"); AlertDialog alert = builder.create();
			 * 
			 * alert.show(); */
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

	protected void do_ItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				lastrow = arg2;
				//lvTovar_list.requestFocusFromTouch();
				//lvTovar_list.setSelection(lastrow);
				
				
				((TovarAdapter) lvTovar_list.getAdapter()).setSelectItem(arg2);
				
				// Samsung Note 2 без этой строки не работает подсветка строк
				((TovarAdapter) lvTovar_list.getAdapter()).notifyDataSetChanged(); 
				
				
				//arg1.setSelected(true);
				//arg0.setEnabled(true);
				
				 //arg1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				cur.moveToPosition(arg2);
				if (openFrom == null) {
					return;
				}

				int ID_TOV = (int) arg3;

				Utils.ShowLog(TAG, "id tovar arg3: " + ID_TOV);

				int fNameIndex = cur.getColumnIndex(DB.fName());
				String fName = cur.getString(fNameIndex);
				double fCount = 1.000d;

				// int kratnostIndex = cur.getColumnIndex(DB.fKrat());
				// float kratnost = cur.getFloat(kratnostIndex);

				int priceIndex = cur.getColumnIndex(DB.fPrice());
				float price = cur.getFloat(priceIndex);
				intent = new Intent();
				// intent = new Intent(ctxNomenclature, AddOrder.class);
				intent.putExtra(IS_EDIT, ZERO);
				intent.putExtra(DB.fID() + DB.tblDH(), idDoc);
				intent.putExtra(DB.fID() + DB.tblTovar(), ID_TOV);
				intent.putExtra(DB.fName(), fName);
				intent.putExtra(DB.fCount(), fCount);
				// intent.putExtra(DB.fKrat(), kratnost);
				intent.putExtra(DB.fPrice(), price);

				// если пользователь пришел из документа
				if (openFrom.equals(DOCUMENT)) {
					intent.setClass(ctxNomenclature, AddOrder.class);
					startActivity(intent);
				}

				// если пользователь пришел из отчета
				if (openFrom.equals(REPORT)) {
					intent.putExtra(DB.fIsGroup(), 0);
					// intent.setClass(ctxNomenclature, ReportActivity.class);
					setResult(RESULT_OK, intent);
					finish();
				}

			}

		});
	}

	public void refreshCursor() {
		// stopManagingCursor(cur);
		// cur = DB.getTov("' '", "fName");
		cur = DB.doSelect();

		// startManagingCursor(cur);
	}

	private void init_widget() {
		ctxNomenclature = this;
		lvTovar_list = (ListView) findViewById(R.id.lvTovar_list);
		intent = getIntent();
		openFrom = intent.getStringExtra(OPEN_FROM);
		if (openFrom == null) {
			openFrom = "";
		}

		idDoc = intent.getIntExtra(_ID, 0);
		// showLog(TAG, "init_widget " + "openFrom = " + openFrom + "; idDoc = "
		// + idDoc);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nomenclature);
		init_widget();
		do_ItemClick(lvTovar_list);
		do_setOnItemLognClick(lvTovar_list);
		registerForContextMenu(lvTovar_list);
		DB = new db(ctxNomenclature);
		DB.open();
		getConstant(DB);

		// DB.setTypePrice(intent.getIntExtra(DB.fTypePrice(), ZERO));

		DB.setField(DB.tblTovar(),
				new String[] { DB.fID(), DB.fName(), DB.fCodeRoot() },
				DB.fIsGroup() + " = ? AND " + DB.fLevel() + " = ?",
				new String[] { "G", "1" }, null, null, DB.fName());
		curGroup = DB.doSelect();

		tbl_name = DB.tblTovar();
		columns = new String[] {
				DB.fID(),
				DB.fName(),
				DB.fCount(),
				DB.fIdEdIzm(),
				DB.definePrice(intent.getIntExtra(DB.fTypePrice(), ZERO))
						+ " AS " + DB.fPrice() };

		where = "" + DB.fIsGroup() + " = ? AND + " + DB.fName() + " LIKE ?";

		where_arg = new String[2];
		where_arg[0] = " ";

		where_arg[1] = "%%";

		order_by = DB.fName();

		DB.setField(tbl_name, columns, where, where_arg, group_by, having,
				order_by);

		refreshCursor();

		String[] from = new String[] { DB.fName(), DB.fPrice(), DB.fCount() };
		int[] to = new int[] { R.id.tv1Myitem, R.id.tv4Myitem, R.id.tv5Myitem };

		// adap = new DocTableAdapter(ctxNomenclature, R.layout.myitem, cur,
		// from,
		// to);

		adap = new TovarAdapter(ctxNomenclature, R.layout.myitem, cur, from, to);
		

		lvTovar_list.setAdapter((ListAdapter) adap);

		adap.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				where_arg[0] = " ";
				where_arg[1] = "%" + constraint.toString().toUpperCase() + "%";
				DB.setField(tbl_name, columns, where, where_arg, null, null,
						order_by);
				if (!constraint.toString().equalsIgnoreCase("")) {
					cur = DB.fetchCountriesByName(constraint.toString());
				}

				// return counterDB.fetchCountriesByName(constraint.toString());

				return cur;
			}
		});

		/* adap.setFilterQueryProvider(new FilterQueryProvider() { public Cursor
		 * runQuery(CharSequence constraint) { cur = DB.fetchCountriesByName(
		 * constraint.toString(), 2); // return
		 * counterDB.fetchCountriesByName(constraint.toString());
		 * Log.d("setFilterQueryProvider", "" + constraint + ""); return cur; }
		 * }); */
		// lvTovar_list.setFocusable(true);
		// lvTovar_list.setSelector(R.drawable.test_list_item_selector);

		//lvTovar_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// lvTovar_list.setSelector(getResources().getDrawable(
		// R.drawable.test_list_item_selector));

		// lvTovar_list.requestFocusFromTouch();
		// lvTovar_list.setFocusable(true);
		// lvTovar_list.setSelection(1);

		// lvTovar_list.setOverScrollMode(View.OVER_SCROLL_NEVER);

		// lvTovar_list.setSelection(1);

		// lvTovar_list.requestFocus();

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
		curGroup.close();
		cur.close();
		DB.close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 
		
		int lastRow_ = ((TovarAdapter) lvTovar_list.getAdapter())
				.getSelectItem();
		TovarAdapter tovAd = (TovarAdapter) lvTovar_list.getAdapter();
		tovAd.setSelectItem(lastRow_);
		
	}

}
