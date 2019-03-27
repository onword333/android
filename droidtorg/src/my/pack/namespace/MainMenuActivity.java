package my.pack.namespace;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import my.pack.dialog.DialogFactory;
import my.pack.error.ErrorMsg;
import my.pack.inet.Inet;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenuActivity extends Main {
	private final static String TAG = "MAIN_MENU";

	private final static String startQuery = "<query>"; // ��������� ��� �������
	private final static String endQuery = "</query>";// �������� ��� �������
	private final static String startData = "<data>"; // ��������� ��� ������
	private final static String endData = "</data>";// �������� ��� ������

	// ���� �� ������ �������� (�� ��������� - ���):
	// true - ��; false - ���
	private boolean isDownload = false;

	/** �������� ���: 0 -���, 1 - �� */
	private int toLog = 1;

	protected final int pack = 100;
	public ListView lvMainMenu_main;
	public Context contextMainMenu;
	protected final String checkOst = "paspr1";
	protected final String loadSpr = "paspr2";
	protected SharedPreferences setting;
	protected SharedPreferences.Editor shareEditor;
	protected String settingName = "PathUpdate";
	public String settDateOst = "last_update";
	protected String keyName = "RadioButtion";
	protected int rbValue;
	protected int dateYear, dateMonth, dateDay;
	protected Calendar cal = Calendar.getInstance();
	protected db DB;
	protected StringBuilder strBuild;
	protected final String ENCODE = "windows-1251";
	protected String TorgID, SendID;
	protected Inet inet;
	protected ProgressDialog progDialog;
	
	// TextView
	protected TextView tvCountDoc;
	protected TextView tvDateRest;
	protected TextView tvDbVersion;

	// ��������� ���������
	protected String host, resources, port;
	protected int send_id, torg_id;
	protected HashMap<String, String> map_parametr;
	private ListView lvConstant;
	private LayoutInflater layInf;
	private View lay, layInfo;
	private EditText etValue;
	private Button butOK;
	private Button butCencel;
	private Builder dialogEditText;
	private String pass;

	// ����� ��������� ��������� ���������� ��� ������������
	// �������� � �������
	private String infoForUser;

	private Handler hCheckRests;

	protected SimpleDateFormat fromDateStr = new SimpleDateFormat("yyyy-MM-dd");
	protected SimpleDateFormat toDateStr = new SimpleDateFormat("dd.MM.yy");

	/**
	 * �������� - ������ ������ � ������� ��������� ����������
	 * "�� ����� ���� ��������� �������"
	 * @param args - ��������� ������ �� ������� ��������� � ����: [0] - ��
	 *            �������,[1] - yyyy.M.d.hh.mm, [2] - yyyy.M.d, [3]- yyyy.M.d
	 * @return - �������: [1] ���������� � [2] �� [3]
	 */
	public String parseDataDownloading(String[] args) {
		if (args.length == 0) {
			return "";
		}
		String str = "";

		try {
			String dateDownload = Utils.convertDate(args[1], "yyyy.M.d.hh.mm",
					"dd.MM.yyyy hh:mm:ss");
			String dateDebitorkaFrom = Utils.convertDate(args[2], "yyyy.M.d",
					"dd.MM.yyyy");
			String dateDebitorkaTo = Utils.convertDate(args[3], "yyyy.M.d",
					"dd.MM.yyyy");
			
			str = contextMainMenu.getResources().getString(R.string.restDate);
			
			str += dateDownload + "\n";
			str += contextMainMenu.getResources().getString(R.string.reports_submenu_debts_buyers);
			str += dateDebitorkaFrom + " - "
					+ dateDebitorkaTo;
		} catch (ParseException e) {
			str =contextMainMenu.getResources().getString(R.string.error1) + "\r\n" + e.toString();
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * @param arg - ������ � ���� "yyyy-MM-dd"
	 * @return - ��������� ������ "dd.MM.yy"
	 * @throws ParseException
	 */
	protected String dateFormat(String arg) throws ParseException {
		return toDateStr.format(fromDateStr.parse(arg));
	}

	private void detectCoder(String str) {
		String value = str;
		Map map = Charset.availableCharsets();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			// Get charset name
			String charsetName = (String) it.next();
			// Get charset
			Charset charset = Charset.forName(charsetName);
			// ��� ���� ��������� ���������
			// �������� ���:
			try {
				byte[] b = value.getBytes(charsetName);
				value = new String(b, charsetName);

				System.out.println("charsetName=" + charsetName + "; value ="
						+ value);

			} catch (Exception e) {
				System.out.println("Is not " + charsetName + "; message:"
						+ e.getMessage());
			}
		}
	}

	private void showLog(String msg) {
		if (toLog == 1) {
			Log.d(TAG, msg);
		}
	}

	protected void doClick(Button bt) {
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);

		switch (id) {
		case DialogFactory.DIALOG_ALERT:
			dialog.setTitle(contextMainMenu.getResources().getString(R.string.dialog_title));
			//dialog.setTitle("3333");
			((AlertDialog) dialog).setMessage(infoForUser);
			break;
		case DialogFactory.DIALOG_PROGRESS:
			((ProgressDialog) dialog).setMessage(infoForUser);
		default:
			break;
		}

	}

	// ����������� ������ ����.��.��, ��������� ����� ��.��.����
	// str - ������ � ������������ |
	protected String formatStrDate(String str) {
		String arr[] = str.split("\\|", 17);
		String arrDate[] = {};
		String formatDate = "";
		if (arr[0].equalsIgnoreCase("OK")) {
			arrDate = arr[1].split("\\.", 17);
			formatDate = arrDate[2] + "." + arrDate[1] + "." + arrDate[0];
		}
		return formatDate;
	}

	// ������� �� ���� ��������� �������� ������
	// � ��������� ��� HashMap map_parametr;
	// DB - ������ �� ���� ������
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
			port = map_parametr.get("port");
			send_id = Integer.parseInt(map_parametr.get("sendID"));
			torg_id = Integer.parseInt(map_parametr.get("torgID"));
			pass = map_parametr.get("pass");
		}
		cur.close();
	}

	protected Dialog onCreateDialog(int id) {
		return DialogFactory.getDialogById(id, contextMainMenu);
	}

	public String setDateFormat(String tempFormat) {
		dateYear = cal.get(Calendar.YEAR);
		dateMonth = cal.get(Calendar.MONTH);
		dateDay = cal.get(Calendar.DAY_OF_MONTH);

		Time dateFormat = new Time();
		dateFormat.set(dateDay, dateMonth, dateYear);
		long dt = dateFormat.toMillis(true);
		String strFormat = (String) DateFormat.format(tempFormat, dt);
		return strFormat;
	}

	/**
	 * <b>readFromFile(String file_name, StringBuilder sb)</b><br>
	 * ��������� ���������� ����� file_name � sb *
	 * 
	 * @param file_name - ������ ���� � ����� + ��� ����� ��� ������ ��� ����� *
	 * @param sb - ���������� ���� StringBuilder*
	 * @throws IOException
	 */
	protected void readFromFile(String file_name, StringBuilder sb)
			throws IOException {
		FileInputStream inputStrem;

		if (file_name.indexOf("/") == -1) {
			inputStrem = openFileInput(file_name);
		} else {
			inputStrem = new FileInputStream(file_name);
		}
		int size = inputStrem.available();

		InputStreamReader inputReader = new InputStreamReader(inputStrem,
				ENCODE);

		BufferedReader bfReader = new BufferedReader(inputReader, size);
		String row;
		while ((row = bfReader.readLine()) != null) {
			sb.append(row + "\r\n");
		}

		bfReader.close();
		inputReader.close();
		inputStrem.close();

	}

	/**
	 * <b>writeToFile(String file_name, String text)</b><br>
	 * ���������� ������ ����������� text � ���� file_name
	 * 
	 * @param file_name - ������ ���� � ����� + ��� ����� ��� ������ ��� ����� *
	 * @param text - ����� ������� ���� �������� *
	 * @throws IOException
	 */
	protected void writeToFile(String file_name, String text)
			throws IOException {
		FileOutputStream fos;

		if (file_name.indexOf("/") == -1) {
			fos = openFileOutput(file_name, MODE_PRIVATE);
		} else {
			fos = new FileOutputStream(file_name);
		}

		OutputStreamWriter osw = new OutputStreamWriter(fos, ENCODE);

		osw.write(text);
		osw.close();
		fos.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Cursor cur, cur_2;
		Intent intent;
		String host, reply;

		final ErrorMsg err = new ErrorMsg(contextMainMenu);
		final Resources res;
		switch (item.getItemId()) {
		case R.id.itLoadSpav:
			intent = new Intent(contextMainMenu, AdminActivity.class);
			startActivity(intent);
			break;

		case R.id.itemCheckOst:
			inet.changeQuery(startQuery + checkOst + "|" + torg_id + "|" + "LK"
					+ endQuery + "\r\n");
			/* try { inet.getHost2();
			 * 
			 * } catch (MalformedURLException e2) { // TODO Auto-generated
			 * catch block infoForUser = "�� ������� ��������� �������!" +
			 * e2.toString(); showDialog(DialogFactory.DIALOG_ALERT);
			 * e2.printStackTrace(); return false;
			 * 
			 * } catch (ConnectException e2) { infoForUser =
			 * "������ �����/������!" + e2.toString();
			 * showDialog(DialogFactory.DIALOG_ALERT); e2.printStackTrace();
			 * return false;
			 * 
			 * } catch (UnknownHostException e2) { infoForUser =
			 * "���� � ��������� ������ �� ����������!" + e2.toString();
			 * showDialog(DialogFactory.DIALOG_ALERT); e2.printStackTrace();
			 * return false;
			 * 
			 * } catch (IOException e2) { // TODO Auto-generated catch block
			 * infoForUser = "������ �����/������!\r\n" + e2.getMessage();
			 * showDialog(DialogFactory.DIALOG_ALERT); e2.printStackTrace(); //
			 * return false; return false;
			 * 
			 * } finally { inet.closeConnect(); } */
			reply = "";
			try {
				inet.openSocket();
				inet.writeToSocket(255, inet.getOutpuStream());
				inet.shutDownOutput();
				inet.readFromSocket(255, inet.getInputStream());
				reply = inet.getByteOutput().toString(inet.ENCODING);
				infoForUser = parseDataDownloading(reply.split("\\|"));
			} catch (IOException e3) {
				infoForUser = "������ ��� ��������/��������� ������.\r\n"
						+ e3.toString();
				e3.printStackTrace();
				showDialog(DialogFactory.DIALOG_ALERT);
			} finally {
				inet.closeConnect();
			}

			showDialog(DialogFactory.DIALOG_ALERT);

			break;
		case R.id.itSendZ:
			if (rbValue == 1) {
				if (err.checkSD()) {
					// ����������
				} else {
					return false;
				}
			}

			if (torg_id == 0) {
				showMassage(contextMainMenu,
						"ID ��������� �������������: �� ��������� !");
				break;
			}

			// ��������� ��������� ������ (torg_id, send_id)
			getConstant(DB);

			String dateStr = Utils.getCurentDate("yyyy.M.d");
			String head_zayavka = startQuery;
			head_zayavka += "paformat|" + torg_id + "|" + dateStr + "|"
					+ send_id;
			head_zayavka += endQuery + "\r\n";

			String file_name = "Zayavka.txt";
			strBuild.delete(0, strBuild.length());

			String str = "SELECT * FROM tblDocHead WHERE " + DB.fDocHead[4]
					+ " = ''";
			cur = DB.customQuery(str);

			if (cur.moveToFirst()) {
				int idIndex = cur.getColumnIndex(DB.fDocHead[0]);
				int keyPartnerIndex = cur.getColumnIndex(DB.fDocHead[1]);
				int DateIndex = cur.getColumnIndex(DB.fDocHead[2]);
				int SumIndex = cur.getColumnIndex(DB.fDocHead[3]);
				int OutIndex = cur.getColumnIndex(DB.fDocHead[4]);
				int NoteIndex = cur.getColumnIndex(DB.fDocHead[5]);
				int keyMagIndex = cur.getColumnIndex(DB.fDocHead[6]);

				strBuild.append(head_zayavka);
				strBuild.append(startData);

				do {
					String formatDate = "";
					try {
						formatDate = dateFormat(cur.getString(DateIndex));
					} catch (ParseException e) {
						e.printStackTrace();
						break;
					}

					strBuild.append("" + formatDate + "|" + cur.getInt(idIndex)
							+ "|" + cur.getInt(keyPartnerIndex) + "|"
							+ cur.getInt(keyMagIndex) + "|"
							+ cur.getFloat(SumIndex) + "|"
							+ cur.getString(NoteIndex) + "|");
					str = "SELECT * FROM tblDocTable WHERE " + DB.fDocTable[1]
							+ " = " + cur.getInt(idIndex) + "";
					cur_2 = DB.customQuery(str);

					if (cur_2.moveToFirst()) {
						do {
							int fk_zayavka_ind = cur_2
									.getColumnIndex(DB.fDocTable[1]);
							int fk_tov_ind = cur_2
									.getColumnIndex(DB.fDocTable[2]);
							int count_ind = cur_2
									.getColumnIndex(DB.fDocTable[3]);
							int price_ind = cur_2
									.getColumnIndex(DB.fDocTable[4]);
							int sum_ind = cur_2.getColumnIndex(DB.fDocTable[5]);

							strBuild.append("" + cur_2.getInt(fk_zayavka_ind)
									+ "^" + cur_2.getInt(fk_tov_ind) + "^"
									+ cur_2.getFloat(count_ind) + "^"
									+ cur_2.getFloat(price_ind) + "^"
									+ cur_2.getFloat(sum_ind) + "^");

						} while (cur_2.moveToNext());
					}

					int len_str = strBuild.length();

					// ������� � ����� ������ ���� ������ "^"
					strBuild.delete(len_str - 1, len_str);

					strBuild.append("\r\n");
					cur_2.close();

				} while (cur.moveToNext());

			}
			strBuild.append(endData);
			cur.close();

			String zayavkaFormat = strBuild.toString();
			// showLog(TAG + "------ ", zayavkaFormat);

			String OK = "OK";
			String fOK = null;

			try {
				// ������������ � windows-1251
				zayavkaFormat = new String(strBuild.toString().getBytes(
						"windows-1251"), "windows-1251");
				fOK = new String(OK.getBytes("windows-1251"), "windows-1251");

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				infoForUser = "������ ������������� ������, �������� ������ ����������\r\n"
						+ e1.toString();
				showDialog(DialogFactory.DIALOG_ALERT);
				break;
			}
			zayavkaFormat = zayavkaFormat.trim();
			// DB.close();
			Log.d("SELECT", Environment.getExternalStorageDirectory()
					.getAbsolutePath().toString());
			;
			if (cur.getCount() > 0) {

				try {
					if (rbValue == 1) {
						writeToFile(err.SD_PATH + "/" + file_name + "",
								zayavkaFormat);
					} else {
						writeToFile(file_name, zayavkaFormat);
					}

					// showMassage(contextMainMenu, "������ ������������.");
				} catch (IOException e) {
					infoForUser = "��������� ������� ���������� ������ ��� ��������!"
							+ err.SD_PATH + "/" + file_name;

					// showMassage(contextMainMenu, infoForUser);
					e.printStackTrace();
					showDialog(DialogFactory.DIALOG_ALERT);
					break;
				}
				// showDialog(DialogFactory.DIALOG_PROGRESS);
				// t = new Thread(new Runnable() {
				// public void run() {
				/* try { // host = inet.getHost2(); inet.getHost2(); } catch
				 * (MalformedURLException e2) { infoForUser =
				 * "�� ������� ����� ����!\r\n" + e2.toString();
				 * e2.printStackTrace(); showDialog(DialogFactory.DIALOG_ALERT);
				 * return false;
				 * 
				 * } catch (IllegalArgumentException e2) { infoForUser =
				 * "������!!\r\n" + e2.toString(); e2.printStackTrace();
				 * showDialog(DialogFactory.DIALOG_ALERT); return false; } catch
				 * (IOException e2) { infoForUser = "������ �����/������!\r\n" +
				 * e2.toString(); e2.printStackTrace();
				 * showDialog(DialogFactory.DIALOG_ALERT); return false; }
				 * finally { inet.closeConnect(); } */
				// ��������� ������ �������� ������������ ������
				// �.�. ����� �������� �� ������� �����: ������ �� ��
				// ������,
				// ��� ������������ ������ ��� ���.
				// String query_zayavka = "paquery|" + send_id + "|" +
				// dateStr
				// + "|" + torg_id + "";

				String str_massage = "";
				inet.changeQuery(strBuild.toString());

				String answer;

				try {
					inet.openSocket();
					inet.writeToSocket(255, inet.getOutpuStream());
					inet.shutDownOutput();
					inet.readFromSocket(255, inet.getInputStream());
					answer = inet.getByteOutput().toString(inet.ENCODING);
				} catch (ConnectException e1) {
					infoForUser = "��������� ���� ������, �� ������ �� ��������!\r\n"
							+ e1.toString();
					e1.printStackTrace();
					showDialog(DialogFactory.DIALOG_ALERT);
					return false;
				} catch (UnknownHostException e1) {
					infoForUser = "���� � ��������� ������ �� ����������!\r\n"
							+ e1.toString();
					e1.printStackTrace();
					showDialog(DialogFactory.DIALOG_ALERT);
					return false;

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					infoForUser = "������ �����/������!\r\n" + e1.toString();
					e1.printStackTrace();
					showDialog(DialogFactory.DIALOG_ALERT);
					return false;
				} finally {
					inet.closeConnect();
				}

				String answerFormat = answer.trim();

				int row_count = 0;
				if (answerFormat.equalsIgnoreCase(fOK)) {
					DB.clearCV();
					DB.addCV(DB.fDocHead[4], "X");
					row_count = DB.updateRow(DB.tblDH(), DB.getFDocHead(4)
							+ " = ?", new String[] { "" });
					str_massage = "������ ���������� " + row_count + " ��.";

				} else {
					str_massage = "������ ����������, �� ������ �� �� ����������!"
							+ answer + "";
				}

				showLog("updateRow:" + strBuild.toString() + "");

				DB.clearCV();
				DB.addCV(DB.fConstant[3], send_id + 1);
				DB.insertConst("sendID");

				infoForUser = str_massage;
				showLog("���������� ��� ������������:" + str_massage);

				showDialog(DialogFactory.DIALOG_ALERT);
				showLog("itSendZ_�����������:" + answerFormat + ":"
						+ answerFormat.length() + "");
				// hCheckRests.sendMessage(hCheckRests.obtainMessage());

				// }

				// });
				// t.start();
				refreshValue();
				// DB.close();

			} else {
				showMassage(contextMainMenu, "��� ������ ��� ��������!");
			}

			Log.d("��������� ������", "SEND");
			break;

		case R.id.itSubMenuSels:
			intent = new Intent(contextMainMenu, ReportActivity.class);
			startActivity(intent);
			break;
		case R.id.itemConstant:
			intent = new Intent(contextMainMenu, PasswordActivity.class);
			startActivity(intent);
			// showDialog(DIALOG_PASS);
			// intent = new Intent(contextMainMenu, ConstantActivity.class);
			// startActivity(intent);
			break;

		case R.id.itSetting:
			intent = new Intent(contextMainMenu, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.itReportsSubMenuDebitorka:
			intent = new Intent(contextMainMenu, ReportDebtsBuyers.class);
			startActivity(intent);
			break;
		case R.id.itProcessing:
			intent = new Intent(contextMainMenu, Processing.class);
			startActivity(intent);
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.mymenu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	public void doItemClick(ListView lv) {
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView selectedItem = (TextView) arg1;
				String strItem = selectedItem.getText().toString();

				Class act;
				String val = "";
				int val2 = 0, _id = 0;
				Resources res = getResources();
				Intent selectedActivity = new Intent();

				if (strItem.equalsIgnoreCase(res.getString(R.string.str_tovar))) {
					act = NomenclatureActivity.class;
					// showMassage(contextMainMenu, strItem);
				} else if (strItem.equalsIgnoreCase(res
						.getString(R.string.str_partner))) {
					act = CounteragentActivity.class;
					// showMassage(contextMainMenu, strItem);
				} else if (strItem.equalsIgnoreCase(res
						.getString(R.string.str_zayavka))) {
					act = DocJournal.class;
					// showMassage(contextMainMenu, strItem);
				} else if (strItem.equalsIgnoreCase(res
						.getString(R.string.str_new_zayavka))) {
					act = NewZayavkaActivity.class;
					val = res.getString(R.string.str_new_zayavka);
					_id = DB.idDoc();
					selectedActivity.putExtra(DB.fID() + DB.tblDH(), _id + 1);
					// showMassage(contextMainMenu, strItem);

				} else if (strItem.equalsIgnoreCase(res
						.getString(R.string.str_administration))) {
					act = AdminActivity.class;

					// } else if (strItem.equalsIgnoreCase(res
					// .getString(R.string.main_submenu_sels))) {
					// act = ReportActivity.class;

				} else {
					act = MainMenuActivity.class;
				}

				// Intent s = new Intent();
				// s.setClass(packageContext, cls)
				// Intent selectedActivity = new Intent(contextMainMenu, act);
				selectedActivity.setClass(contextMainMenu, act);

				selectedActivity.putExtra("action", val);
				selectedActivity.putExtra("openFrom", val2);
				// selectedActivity.putExtra(DB.fID() + DB.tblDH(), _id + 1);
				startActivity(selectedActivity);
			}
		});

	}

	/* ������������� �������� ����� */
	public void init_widget() {
		lvMainMenu_main = (ListView) findViewById(R.id.lvMainMenu_main);
		tvCountDoc = (TextView) findViewById(R.id.tvCountDoc);
		tvDateRest = (TextView) findViewById(R.id.tvDateRest);
		tvDbVersion = (TextView) findViewById(R.id.tvDbVersion);

		lvConstant = (ListView) findViewById(R.id.lvConstant);
		layInf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		lay = layInf.inflate(R.layout.dialog_change_value,
				(ViewGroup) findViewById(R.id.LinAlertDialog));
		layInfo = layInf.inflate(R.layout.dialog_info,
				(ViewGroup) findViewById(R.id.lv_dialog_info));

		etValue = (EditText) lay.findViewById(R.id.etValue);
		butOK = (Button) lay.findViewById(R.id.btOK);
		butCencel = (Button) lay.findViewById(R.id.btCancel);

		// pbStatus = (ProgressBar) findViewById(R.id.progressBar1);

		doClick(butOK);
		doClick(butCencel);
		// setting = getSharedPreferences(settingName, MODE_PRIVATE);
		// // shareEditor = setting.edit();
		// rbValue = setting.getInt(keyName, 2);
		// Log.d("MAINMENU", rbValue + " +  DFASDFASF");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		contextMainMenu = this;

		setting = getSharedPreferences(settingName, MODE_PRIVATE);
		shareEditor = setting.edit();
		strBuild = new StringBuilder();
		map_parametr = new HashMap<String, String>();
		init_widget();

		DB = new db(contextMainMenu);
		DB.open();
		getConstant(DB);
		refreshValue();
		// inet = new Inet("http://" + host + resources, Integer.parseInt(port),
		// contextMainMenu);
		inet = new Inet(host, Integer.parseInt(port), contextMainMenu);

		ArrayAdapter adap = ArrayAdapter.createFromResource(contextMainMenu,
				R.array.arr_main_menu, R.layout.main_menu_item);

		lvMainMenu_main.setAdapter(adap);

		doItemClick(lvMainMenu_main);

		hCheckRests = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				DialogFactory.closeDialog();
				showDialog(DialogFactory.DIALOG_ALERT);
			}
		};

	}

	protected void refreshValue() {
		String countDoc = "";
		int count = DB.customQuery(
				"SELECT * FROM tblDocHead WHERE " + DB.fDocHead[4] + " = ''")
				.getCount();
		
		tvCountDoc.setText(" " + count + "");
		tvDateRest.setText(" " + setting.getString(settDateOst, "01.01.1980"));
		tvDbVersion.setText(" " + DB.getVer() + "");
		
	}

	// @Ovedrride
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("MAINMENU", "ONRESUME");
	}

	@Override
	protected void onStart() {

		// TODO Auto-generated method stub
		super.onStart();

		rbValue = setting.getInt(keyName, 2);

		// DB.open();
		getConstant(DB);
		refreshValue();
		// inet = new Inet("http://" + host + resources, Integer.parseInt(port),
		// contextMainMenu);
		Log.d("MAINMENU", "ONSTART");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// DB.close();
		Log.d("MAINMENU", "ONSTOP");
		// mDB.close();
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		DB.close();
		Log.d("MAINMENU", "ONDESTROY");
		super.onDestroy();
	}

}
