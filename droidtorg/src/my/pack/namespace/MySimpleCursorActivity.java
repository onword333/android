//
// 
//
//
//package my.pack.namespace;
//
////import android.R;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
////import com.androidbook.triviaquiz6.R;
//
////import android.R;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.XmlResourceParser;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ListView;
//import android.widget.SimpleCursorAdapter;
//import android.widget.TableLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class MySimpleCursorActivity extends Activity {
//	public db mDB;
//	public Context contextMain;
//	public Cursor cur;
//	public SimpleCursorAdapter myAdapter;
//	// public ListView ivlistMain;
//	public GridView grMain;
//	public Button addRecord, delRecord;
//
//	public void do_click(Button bButton) {
//		bButton.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				switch (v.getId()) {
//				case R.id.button1:
//					Toast.makeText(contextMain, "Заполняем базу данными",
//							Toast.LENGTH_SHORT).show();
//					try {
//						getEventsFromAnXML();
//						Log.d("TABHOST", "Начало попытки");
//					} catch (XmlPullParserException e) {
//						Log.d("TABHOST", e.toString());
//					} catch (IOException e) {
//						Log.d("TABHOST", e.toString());
//					}
//
//					refreshCursor();
//					myAdapter.changeCursor(cur);
//
//					break;
//				case R.id.button2:
//					mDB.deleteAll();
//					refreshCursor();
//					myAdapter.changeCursor(cur);
//					break;
//				default:
//					Toast.makeText(contextMain,
//							"Для этой кнопки не предусмотрено событие",
//							Toast.LENGTH_SHORT).show();
//					break;
//				}
//			}
//		});
//	}
//
//	public void refreshCursor() {
//		stopManagingCursor(cur);
//		// cur = mDB.getAllData();
//		startManagingCursor(cur);
//	}
//
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//
//		addRecord = (Button) findViewById(R.id.button1);
//		delRecord = (Button) findViewById(R.id.button2);
//
//		do_click(addRecord);
//		do_click(delRecord);
//		contextMain = this;
//		mDB = new db(contextMain);
//
//		mDB.open();
//		// try {
//		// //getEventsFromAnXML();
//		// Log.d("TABHOST", "Начало попытки");
//		// } catch (XmlPullParserException e) {
//		// Log.d("TABHOST", e.toString());
//		// } catch (IOException e) {
//		// Log.d("TABHOST", e.toString());
//		// }
//
//		refreshCursor();
//		// Toast.makeText(contextMain,
//		// "Количество записей = " + cur.getCount() + "",
//		// Toast.LENGTH_SHORT).show();
//		// // формируем столбцы сопостовления
//		String[] from = new String[] { mDB.TOV_NAME, mDB.TOV_QUANTITY_ORDER,
//				mDB.TOV_UNIT, mDB.TOV_COST, mDB.TOV_QUANTITY };
//
//		int[] to = new int[] { R.id.tvMyitem_tovar, R.id.tvMyitem_order_count,
//				R.id.tvMyitem_unit, R.id.tvMyitem_coast, R.id.tvMyitem_quantity };
//
//		// создаем адаптер и настраиваем список
//		myAdapter = new SimpleCursorAdapter(contextMain, R.layout.myitem, cur,
//				from, to);
//		grMain = (GridView) findViewById(R.id.gridView1);
//
//		grMain.setAdapter(myAdapter);
//		grMain.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//
//				cur.moveToPosition(arg2);
//				int nameIndex = cur.getColumnIndex(mDB.TOV_NAME);
//				int coast = cur.getColumnIndex(mDB.TOV_COST);
//
//				String tovar_name = cur.getString(nameIndex);
//				Float tovar_coast = cur.getFloat(coast);
//				Intent add_order = new Intent(contextMain, AddOrder.class);
//				add_order.putExtra("ID", arg3);
//				add_order.putExtra(mDB.TOV_NAME, tovar_name);
//				add_order.putExtra(mDB.TOV_COST, tovar_coast);
//
//				Toast.makeText(contextMain, "" + tovar_coast + "",
//						Toast.LENGTH_SHORT).show();
//
//				mDB.update_record(Long.toString(arg3));
//				refreshCursor();
//				myAdapter.changeCursor(cur);
//				// Intent add_order = new Intent(contextMain, AddOrder.class);
//				startActivity(add_order);
//
//			}
//		});
//
//		grMain.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				Toast.makeText(contextMain, "Долгое нажатие",
//						Toast.LENGTH_SHORT).show();
//				return false;
//			}
//
//		});
//
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		mDB.close();
//		Toast.makeText(contextMain, "Закрываем базу", Toast.LENGTH_SHORT)
//				.show();
//	}
//
//	private void getEventsFromAnXML() throws XmlPullParserException,
//			IOException {
//		// StringBuffer stringBuffer = new StringBuffer();
//		// XmlResourceParser mockAllScores =
//		// getResources().getXml(R.xml.allscores);
//		// mockAllScores.next();
//
//		// int eventType = mockAllScores.getEventType();
//		XmlResourceParser tovars = getResources().getXml(R.xml.and);
//
//		int eventType = -1;
//		boolean find = false;
//		int count = 0;
//		while (eventType != XmlPullParser.END_DOCUMENT) {
//			if (eventType == XmlResourceParser.START_TAG) {
//				String tovar = tovars.getName();
//				if (tovar.equals("TOVAR")) {
//					find = true;
//					count = count + 1;
//					String name = tovars.getAttributeValue(null, "name");
//					String unit = tovars.getAttributeValue(null, "unit");
//					String cost = tovars.getAttributeValue(null, "cost");
//					String quantity = tovars
//							.getAttributeValue(null, "quantity");
//
//					float cost_f = Float.parseFloat(cost);
//					float quantity_f = Float.parseFloat(quantity);
//
//					mDB.addRecord(name, unit, cost_f, quantity_f);
//					Log.d("WHILE",
//							"name = " + name.toString() + " unit"
//									+ unit.toString() + "");
//					// createNewTableRow(scoreTable, username, val, rank);
//
//				}
//			}
//
//			eventType = tovars.next();
//		}
//		Toast.makeText(contextMain, "Всего добавлено строк " + count,
//				Toast.LENGTH_SHORT).show();
//	}
//
// }
