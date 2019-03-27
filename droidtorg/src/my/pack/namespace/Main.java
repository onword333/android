package my.pack.namespace;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class Main extends Activity {
	// общие поля используются в качестве значений
	public final static int ZERO = 0;
	public final static int ONE = 1;

	// строковые константы
	// key OPEN_FROM - описывает откуда пришел пользователь
	public final static String OPEN_FROM = "OPEN_FROM";
	// значение key OPEN_FROM = MAIN_MENU (из гл. Actyvity)
	public final static String MAIN_MENU = "MAIN_MENU";
	// значение key OPEN_FROM = DOCUMENT (из документа)
	public final static String DOCUMENT = "DOCUMENT";
	// значение key OPEN_FROM = JOURNAL (из журнала)
	public final static String JOURNAL = "JOURNAL";
	// значение key OPEN_FROM = JOURNAL (из отчета)
	public final static String REPORT = "REPORT";

	// key описывает id записи
	public final static String _ID = "_id";

	// поле определяет факт редактирования, 1 - да, 0 - нет
	public final static String IS_EDIT = "IS_EDIT";

	// начальная дата
	public final static String START_DATE = "START_DATE";
	// конечная дата
	public final static String END_DATE = "END_DATE";

	public void showMassage(Context cx, String str) {
		Toast.makeText(cx, str, Toast.LENGTH_SHORT).show();
	}

	public void showLog(String TAG, String str) {
		Log.d(TAG, str);
	}

	public String loadText(String key, String def) {
		SharedPreferences sPref = getPreferences(MODE_PRIVATE);
		String savedText = sPref.getString(key, def);
		return savedText;
		//Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
	}

	public void savePref(String key, String val) {
		SharedPreferences sPref = getPreferences(MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putString(key, val);
		ed.commit();
		// Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
	}

}
