package my.pack.namespace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;

import my.pack.dialog.DialogFactory;
import my.pack.error.ErrorMsg;
import my.pack.inet.Inet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdminActivity extends Main {
	private final static String startQuery = "<query>";
	private final static String endQuery = "</query>";
	public Button btFromServer, btFromFile;
	public Context contextAdmin;
	private db mDB;
	private static final String TAG = "ADMINACTIVITY";
	protected final String ENCODE = "windows-1251"; // ���������

	// ����� �� ������� ��������� � LogCat. 0 - ���, 1 - ��
	private int toLog = 0;
	public int DATA_IS_SUCCESS = 0;
	protected File sdPath;

	protected SharedPreferences setting;
	protected SharedPreferences.Editor shareEditor;
	protected final String settDateOst = "last_update";
	protected final String settingName = "PathUpdate";
	protected final String keyName = "RadioButtion";
	protected int rbValue;
	protected int progress;

	protected int ID_DIALOG; // id �������
	protected Dialog typeDialog = null; // �������� ��� ����� ������
	protected ProgressDialog dialogProg = null;// ������ �� �������� ������
	protected AlertDialog dialogAlert = null;// ������ �� ����� ������

	protected Handler progHand; // Handle ��� ��������� ������ � ���������
	protected Handler loadHand; // Handle ��� ��������� ������ ��� �������

	// ����� ���������� ��������� (��� ProgressDialog)
	protected String whatSpravLoad;

	// ���� �� ������ ��������, false - ���, true - ��
	protected boolean isDownload = false;

	// ��������� ����������
	// map_parametr - �������� � ���� key=value, �� ��
	protected HashMap<String, String> map_parametr;
	// host - ip ������ ������ ��� ����� ����������
	private String host;
	// resources - /subhost/pa.txt
	private String resources;
	// port - ���� �� ���������
	private String port = "80";
	private Inet inet;

	// send_id - id ��������
	// torg_id - id ��������� �������������
	protected int send_id, torg_id;

	// ����������� � ������� ��������� ���-�� �����
	protected final int pack = 200;
	// ����� ������ ������� �������� ������ � �������
	// paspr2 - ������� �������, ��� ����� �����������
	protected final String loadSpr = "paspr2";

	// ����� ������ ��� �� �������������� ������ ���������� StringBuilder
	protected StringBuilder strBuild = new StringBuilder();

	//
	protected final String bundleKey = "bundleKey";
	protected final String bundleLoad = "load";
	protected final String bundleConnection = "connection";
	protected final String bundleResult = "result";
	protected final String bundleShowDialog = "show_dialog";

	// tvStatusConnection - ������ ����������
	// tvStatusLoad - ������ ���������� ��������
	// tvStatusResult - ������ ����������
	private TextView tvStatusConnection;// tvStatusLoad, tvStatusResult;
	private String[] listSprav;

	// private String strStatusConnection, strStatusLoad, strStatusResult;

	// public AdminActivity(Context ctx) {
	// TODO Auto-generated constructor stub
	// contextAdmin = ctx;
	// }

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
				Log.d("HASHMAP", "" + map_parametr.get(val) + " ");
			} while (cur.moveToNext());
			host = map_parametr.get("host");
			resources = map_parametr.get("resources");
			port = map_parametr.get("port");
			send_id = Integer.parseInt(map_parametr.get("sendID"));
			torg_id = Integer.parseInt(map_parametr.get("torgID"));
		}

		cur.close();
	}

	/**
	 * <b>writeToFile(String file_name, String text)</b><br>
	 * ���������� ������ ����������� ���������� text � ���� file_name
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
		osw.flush();
		osw.close();
		fos.close();
	}

	private void showLog(String msg) {
		if (toLog == 1) {
			Log.d(TAG, msg);
		}
	}

	private void showMsg(String text) {
		Toast.makeText(contextAdmin, text, Toast.LENGTH_LONG).show();
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

	protected void loadFromServer() {
		final ErrorMsg err = new ErrorMsg(contextAdmin);
		final Resources res = getResources();
		// res = getResources();
		if (rbValue == 1) {
			if (err.checkSD()) {

			} else {
				isDownload = false;
				btFromFile.setEnabled(true);
				Utils.ShowLog("CHECK_SD", isDownload + "");
				return;
			}
		}

		Utils.ShowLog("CHECK_SD", isDownload + "");
		tvStatusConnection.setText("");
		final String[] listFile = res.getStringArray(R.array.arr_update_file);

		isDownload = true;
		Thread t = new Thread(new Runnable() {
			public void run() {
				String host, reply;
				//String loadOperation_1 = "����������....... ";
				//String loadOperation_2 = "��������....... ";
				//String loadOperation_3 = "�������� ��������� �������.";
				
				
				String loadOperation_1 = contextAdmin.getResources().getString(R.string.str_connection) + " ...";
				String loadOperation_2 = contextAdmin.getResources().getString(R.string.str_load) + " ... ";
				String loadOperation_3 = contextAdmin.getResources().getString(R.string.str_result) + ".";

				String strResult = "";
				String name = "";
				Bundle b = new Bundle();
				Message msg = loadHand.obtainMessage();

				// try {
				// host = inet.getHost2();
				// strResult = loadOperation_1 + "OK\r\n";
				// b.putString(bundleKey, strResult);
				// // b.putString(bundleConnection, "OK");
				// msg.setData(b);
				// loadHand.sendMessage(msg);
				// } catch (MalformedURLException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// strResult = loadOperation_1 + "������ ������ �������\r\n";
				// b.putString(bundleKey, strResult);
				// b.putString(bundleShowDialog,
				// "�� ������ ������.\r\n" + e1.getMessage());
				// msg.setData(b);
				// loadHand.sendMessage(msg);
				// isDownload = false;
				// return;
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// strResult = loadOperation_1 + "������ ������/������.\r\n";
				// b.putString(bundleKey, strResult);
				// b.putString(bundleShowDialog, "������ ������/������.\r\n"
				// + e1.getMessage());
				// msg.setData(b);
				// loadHand.sendMessage(msg);
				// isDownload = false;
				// return;
				// } catch (IllegalArgumentException e1) {
				// strResult = loadOperation_1
				// + "���� ��� ���������� ���������.\r\n";
				// b.putString(bundleKey, strResult);
				// b.putString(
				// bundleShowDialog,
				// "���� ��� ���������� ���������.\r\n"
				// + e1.getMessage());
				// msg.setData(b);
				// loadHand.sendMessage(msg);
				// isDownload = false;
				// return;
				// } finally {
				// inet.closeConnect();
				// }

				showLog("loadFromServer Thread start " + isDownload + "");

				// ������ ��� �������� �������� ���� LK (�.�. � �������
				// ������� ������� �� ������), �� ���������� -
				// ������������� �� ����� �� �������
				for (int i = 0; i < listFile.length; i++) {
					int countAllRows = 0;
					name = listFile[i];

					if (i == 2) {
						// break;
					}
					strBuild.delete(0, strBuild.length());

					// Log.d("FILE", "" + name + "");
					// inet.readCountRows = pack;
					boolean end = false;
					// while (end == false) {
					inet.changeQuery(startQuery + loadSpr + "|" + torg_id + "|"
							+ name + endQuery + "\r\n");

					msg = loadHand.obtainMessage();

					try {

						inet.openSocket();
						inet.writeToSocket(255, inet.getOutpuStream());
						inet.shutDownOutput();

						inet.readFromSocket(255, inet.getInputStream());

						String data = inet.getByteOutput().toString(
								Inet.ENCODING);

						// Log.d(TAG, data);
						// end = true;
						if ((data.equalsIgnoreCase(inet.NO_ROWS))
								|| (data.equalsIgnoreCase(""))) {
							end = true;
							// inet.closeConnect();
						} else {
							strBuild.append(data);
						}

					} catch (ConnectException e1) {
						b.putString(bundleKey, strResult + loadOperation_2
								+ " ошибка соединения!");
						b.putString(bundleShowDialog, "ошибка соединения.\r\n"
								+ e1.getMessage());
						msg.setData(b);
						loadHand.sendMessage(msg);
						isDownload = false;
						e1.printStackTrace();
						return;

					} catch (UnknownHostException e1) {
						b.putString(bundleKey, strResult + loadOperation_2
								+ " неизвестный хост!");
						b.putString(
								bundleShowDialog,
								" неизвестный хост.\r\n"
										+ e1.getMessage());
						msg.setData(b);
						loadHand.sendMessage(msg);
						isDownload = false;
						e1.printStackTrace();
						return;

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						b.putString(bundleKey, strResult + loadOperation_2
								+ " ошибка записи/чтения!");
						b.putString(bundleShowDialog,
								" ошибка записи/чтения.\r\n" + e1.getMessage());
						msg.setData(b);
						loadHand.sendMessage(msg);
						isDownload = false;
						e1.printStackTrace();

						return;

					} finally {
						inet.closeConnect();
					}

					// strBuild.append(inet.readFromSocket().toString());
					// Log.d("TAG_2", "" +
					// inet.readFromSocket("92.112.8.99") +
					// "");

					countAllRows += pack;

					// b.clear();

					b.putString(bundleKey, strResult + loadOperation_2 + ""
							+ name + " " + countAllRows + "");
					msg.setData(b);
					loadHand.sendMessage(msg);

					// }
					msg = loadHand.obtainMessage();

					if (name.equalsIgnoreCase(res.getString(R.string.str_lk))) {
						String arr[] = strBuild.toString().split("\\|", 17);
						// Log.d("��������", strBuild.toString());
						if (arr[0].equalsIgnoreCase("OK")) {
							String formatStr = strBuild.toString();
							strBuild.delete(0, strBuild.length());
							strBuild.append(formatStrDate(formatStr));

						} else {
							// showMassage(contextMainMenu,
							// "����� �� ������� �������������!");

							b.putString(bundleKey,
									"����� �� ������� �������������!");
							b.putString(bundleShowDialog,
									"����� �� ������� �������������! ");
							msg.setData(b);
							loadHand.sendMessage(msg);
							// showMsg("����� �� ������� �������������!");
							isDownload = false;
							return;
						}
					}

					try {
						if (rbValue == 1) {
							writeToFile(err.SD_PATH + "/" + name + ".txt",
									strBuild.toString());
						} else {
							writeToFile("" + name + ".txt", strBuild.toString());
						}
						// Log.d("TAG", "" + strBuild.toString() + "");

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				isDownload = false;
				b.putString(bundleKey, loadOperation_3);
				b.putString(bundleShowDialog, loadOperation_3);
				msg.setData(b);
				loadHand.sendMessage(msg);

				Log.d("isDownload", "Thread end " + isDownload + "");
				// b.putBoolean("isDownload", isDownload);
				// msg.setData(b);
				// loadHand.sendMessage(msg);

			}
		});
		t.start();

	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		String dialogTitle = "";
		ID_DIALOG = id;
		switch (id) {
		case DialogFactory.DIALOG_ALERT:
			dialogTitle = contextAdmin.getResources().getString(R.string.dialog_title);
			((AlertDialog) dialog).setTitle(dialogTitle);
			//((AlertDialog) dialog).setMessage("1111111");
			dialogAlert = (AlertDialog) dialog;
			break;

		case DialogFactory.DIALOG_PROGR_HORIZ:
			dialogTitle = contextAdmin.getResources().getString(R.string.load_sprav);
			((ProgressDialog) dialog).setTitle(dialogTitle);
			dialogProg = (ProgressDialog) dialog;
			break;
		default:
			break;
		}

	}

	protected Dialog onCreateDialog(int id) {
		return DialogFactory.getDialogById(id, this);
	}

	public String decodeRow(String str) throws UnsupportedEncodingException {
		String strDecode = "";
		int n = 2;

		byte[] b = str.getBytes("windows-1251");
		byte[] byteEncoding = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			int codeSymbol = b[i];
			byteEncoding[i] = (byte) (codeSymbol - n);
			if (n == 2) {
				n = 3;
			} else {
				n = 4;
			}
		}
		strDecode = new String(byteEncoding, "windows-1251");
		return strDecode;
	}

	/**
	 * @param str - ������ ������� ���� ��������������
	 * @return - ���������� ���������������� ������ � ���������
	 * @throws UnsupportedEncodingException
	 */
	public String encodeRow(String str) throws UnsupportedEncodingException {
		String strDecode = "";
		int shift = 2;
		byte[] b = str.getBytes("windows-1251");
		for (int i = 0; i < b.length; i++) {
			b[i] += shift;
			if (shift == 2) {
				shift = 3;
			} else {
				shift = 4;
			}
		}
		strDecode = new String(b, "windows-1251");
		return strDecode;
	}

	/**
	 * ��������� ������ �� ������ � ������
	 * 
	 * @return - ���������� true - ������ ������, false - ������ �� ������
	 */
	protected boolean checkSD() {
		boolean resCheck = false;
		boolean isSDMounted = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		String DIR_SD = "bluetooth";
		// ���� ������ ��������, ���� ��������� ����������� �� ������� � �������
		// ����� ���������� ������������
		if (isSDMounted == true) {
			sdPath = Environment.getExternalStorageDirectory();
			sdPath = new File(sdPath.getAbsoluteFile() + "/" + DIR_SD);
			if (sdPath.exists()) {
				resCheck = true;

				return resCheck;
			} else {
				showMassage(contextAdmin, "�� ������ �� ������ ������� "
						+ DIR_SD);
				return resCheck;
			}
		} else {
			showMassage(contextAdmin,
					"������ �� ������ � ������, ��������� �� ���������");
			return resCheck;
		}

	}

	protected void readFile(String fileName) throws IOException {

		FileInputStream inputStrem;

		if (rbValue == 1) {
			String filePath = sdPath.toString();
			inputStrem = new FileInputStream(filePath + "/" + fileName + ".txt");
		} else {
			inputStrem = openFileInput("" + fileName + ".txt");
		}

		int size = inputStrem.available();

		if (size <= 0) {
			return;
		}

		// Log.d("LOAD", filePath + "/" + fileName + ".txt");
		InputStreamReader inputReader = new InputStreamReader(inputStrem,
				"windows-1251");
		BufferedReader bfReader = new BufferedReader(inputReader, size);

		String row, strPref = "";

		progress = -2;
		// progDialog.setProgress(progress);
		// progDialog.setMax(size);

		dialogProg.setProgress(progress);
		dialogProg.setMax(size);

		Resources res = getResources();
		int iii = 0;
		while ((row = bfReader.readLine()) != null) {

			if (fileName.equals(res.getString(R.string.str_lk))) {
				strPref = row;
			}
			//Utils.ShowLog(TAG, row);
			row = encodeRow(row);
			progress = row.length() + 2;
			// progDialog.incrementProgressBy(progress);
			dialogProg.incrementProgressBy(progress);
			String strArray[] = row.split("\\|", 17);

			if (fileName.equals(res.getString(R.string.str_ya))) { //
				// ������������
				iii = iii + 1;
				// Log.d("NUM_STRING", "" + iii + "");
				whatSpravLoad = listSprav[0];
				mDB.insertRow(strArray);
			} else if (fileName.equals(res.getString(R.string.str_zc))) { //
				// �����������
				whatSpravLoad = listSprav[1];
				mDB.insertClient(strArray);
			} else if (fileName.equals(res.getString(R.string.str_zd))) { //
				// ��������
				whatSpravLoad = listSprav[2];
				mDB.insertMag(strArray);
			} else if (fileName.equals(res.getString(R.string.str_yd))) { //
				// ��. ���������.
				whatSpravLoad = listSprav[3];
				mDB.insertEdm(strArray);
			} else if (fileName.equals(res.getString(R.string.str_ye))) { //
				// ��������� ��. ���.
				whatSpravLoad = listSprav[4];
				mDB.insertEdmKr(strArray);
			} else if (fileName.equals(res.getString(R.string.str_yb))) { //
				// �������� �����
				whatSpravLoad = listSprav[7];
				mDB.insertSetka(strArray);
			} else if (fileName.equals(res.getString(R.string.str_xf))) { //
				// ���������
				whatSpravLoad = listSprav[9];
				mDB.insertDebitorka(strArray);
			} else if (fileName.equals(res.getString(R.string.str_ze))) { //
				// ����� ������������
				whatSpravLoad = listSprav[6];
				mDB.insertPrice(strArray);
			} else if (fileName.equals(res.getString(R.string.str_yf))) { //
				// ��������� ������
				whatSpravLoad = listSprav[5];
				mDB.insertTovarKr(strArray);
			} else if (fileName.equals(res.getString(R.string.str_lk))) {
				Log.d("LK", strPref);
				shareEditor.putString(settDateOst, strPref);
				shareEditor.commit();
			} else {
				// showMassage(contextAdmin,
				// "��� ������ �������� ����� ������� ���");
				continue;
			}
			progHand.sendMessage(progHand.obtainMessage());
		}

		bfReader.close();
		inputReader.close();
		inputStrem.close();
	}

	/**
	 * ��������� ����� � ��������� ����
	 */
	protected void readFromSD() {
		if (rbValue == 1) {
			if (checkSD()) {

			} else {
				dialogProg.dismiss();
				return;
			}
		}
		isDownload = true;
		tvStatusConnection.setText("");

		// progDialog = initProgDialog();
		// progDialog.show();

		new Thread(new Runnable() {

			public void run() {
				String[] res = getResources().getStringArray(
						R.array.arr_update_file);

				// ����� �� ������� �� ������ ���. ������� ������ � �����������
				// ������.,
				mDB.setField(DbField.tblDebitorka, null, null, null, null,
						null, null);
				mDB.doDelete();

				for (int i = 0; i < res.length; i++) {
					try {
						readFile(res[i]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				dialogProg.dismiss();
				isDownload = false;
				progHand.sendMessage(progHand.obtainMessage());
				// showDialog(DialogFactory.DIALOG_ALERT);

			}
		}).start();
	}

	private void do_click(Button bButton) {
		bButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				ID_DIALOG = 0;
				switch (v.getId()) {
				case R.id.bt_from_server:
					ID_DIALOG = DialogFactory.DIALOG_ALERT;

					btFromFile.setEnabled(false);
					// DialogFactory.MESSAGE =
					// "����� ����� ���������� ��� ������������";
					if (isDownload) {

					} else {
						loadFromServer();
					}

					// showDialog(ID_DIALOG);

					// progHand.sendMessage(progHand.obtainMessage());

					break;

				case R.id.bt_from_file:
					ID_DIALOG = DialogFactory.DIALOG_PROGR_HORIZ;
					showDialog(ID_DIALOG);

					if (isDownload) {
						showDialog(ID_DIALOG);
					} else {
						readFromSD();
					}
					// showDialog(DialogFactory.DIALOG_ALERT);
					break;

				default:
					Toast.makeText(contextAdmin, "������� �� �������������",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}

	private void init_widget() {
		contextAdmin = this;
		btFromServer = (Button) findViewById(R.id.bt_from_server);
		btFromFile = (Button) findViewById(R.id.bt_from_file);

		tvStatusConnection = (TextView) findViewById(R.id.tv_status_connection);
		// tvStatusLoad = (TextView) findViewById(R.id.tv_status_load);
		// tvStatusResult = (TextView) findViewById(R.id.tv_status_result);

		// strStatusConnection = tvStatusConnection.getText().toString();
		// strStatusLoad = tvStatusLoad.getText().toString();
		// strStatusResult = tvStatusResult.getText().toString();

		setting = getSharedPreferences(settingName, MODE_PRIVATE);
		shareEditor = setting.edit();
		rbValue = setting.getInt(keyName, 2);

	}

	/**
	 * ������������� ���������� �������
	 * 
	 * @param arg - false ������ ����������, true - ��������
	 */
	protected void doSetEnable(boolean arg) {
		btFromServer.setEnabled(arg);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		
		setContentView(R.layout.admin);
		init_widget();
		listSprav = getResources().getStringArray(R.array.arr_list_sprav);
		
		map_parametr = new HashMap<String, String>();
		mDB = new db(contextAdmin);
		mDB.open();
		getConstant(mDB);

		do_click(btFromServer);
		do_click(btFromFile);

		// inet = new Inet("http://" + host + resources, Integer.parseInt(port),
		// contextAdmin);
		inet = new Inet(host, Integer.parseInt(port), contextAdmin);

		final String dialogTitle = contextAdmin.getResources().getString(R.string.load_sprav);
		final String dialogMessage = contextAdmin.getResources().getString(R.string.str_result);
		
		progHand = new Handler() {
			public void handleMessage(android.os.Message msg) {

				switch (ID_DIALOG) {
				case DialogFactory.DIALOG_PROGR_HORIZ:
					if (isDownload) {
						doSetEnable(false);
					} else {
						doSetEnable(true);
						DialogFactory.MESSAGE = dialogMessage;
						showDialog(DialogFactory.DIALOG_ALERT);
					}
					dialogProg.setTitle(dialogTitle);
					dialogProg.setMessage("" + whatSpravLoad + "");

					break;

				case DialogFactory.DIALOG_ALERT:

					break;
				default:
					break;
				}
			};

		};

		loadHand = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Bundle b = msg.getData();
				String strMsg = b.getString(bundleKey);

				if (strMsg.equalsIgnoreCase(bundleConnection)) {
					// strMsg = b.getString(bundleConnection);
					// tvStatusConnection.setText(strStatusConnection
					// + " ........... " + strMsg);
				} else if (strMsg.equalsIgnoreCase(bundleLoad)) {
					// strMsg = b.getString(bundleLoad);
					// tvStatusLoad.setText(strStatusLoad + " ........... "
					// + strMsg);
				} else if (strMsg.equalsIgnoreCase(bundleResult)) {
					// strMsg = b.getString(bundleResult);
					// tvStatusResult.setText(strMsg);
					// ID_DIALOG = DialogFactory.DIALOG_ALERT;
					// showDialog(ID_DIALOG);
					// dialogAlert.setMessage(strMsg);

				}

				tvStatusConnection.setText(strMsg);
				showLog("handle msg " + msg.what + "");
				if (b.getString(bundleShowDialog) != null) {
					ID_DIALOG = DialogFactory.DIALOG_ALERT;
					
					DialogFactory.MESSAGE = b.getString(bundleShowDialog);
					//dialogAlert.setMessage(b.getString(bundleShowDialog));
					showDialog(ID_DIALOG);
					
					//dialogAlert.setMessage("777777");

				}

				if (isDownload) {

				} else {
					btFromFile.setEnabled(true);
				}
				// dialogAlert.setTitle("����������");
				// dialogAlert.setTitle("777777");

			};

		};

		Log.d(TAG, "OnCreate");

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		showLog("onStart(): OnStart");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showLog("OnResume(): OnResume");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		showLog("OnStop(): OnStop");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// mDB.close();
		mDB.close();
		super.onDestroy();
		showLog("OnDestroy(): OnDestroy");

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isDownload) {
				return false;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

}
