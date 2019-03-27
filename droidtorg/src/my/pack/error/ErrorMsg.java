package my.pack.error;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class ErrorMsg {
	// время отображения сообщения
	protected final static int TIME_MSG = Toast.LENGTH_LONG;
	public String SD_PATH = "";
	protected Context ctx;

	public ErrorMsg(Context c) {
		ctx = c;
	}

	public void errMsg(String text) {
		Toast.makeText(ctx, text, TIME_MSG).show();
	}

	/**
	 * проверяет готова ли флэшка к работе
	 * 
	 * @return - возвращает true - флэшка готова, false - флэшка не готова
	 */
	public boolean checkSD() {
		boolean isOk = false;
		boolean isSDMounted = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		String DIR_SD = "bluetooth";
		// если флешка доступна, надо проверить существуеет ли каталог в котором
		// лежит обновление справочников
		if (isSDMounted == true) {
			File sdPath = Environment.getExternalStorageDirectory();
			sdPath = new File(sdPath.getAbsoluteFile() + "/" + DIR_SD);
			if (sdPath.exists()) {
				SD_PATH = sdPath.toString();
				isOk = true;

				return isOk;
			} else {
				errMsg("На флешке не найден каталог " + DIR_SD);
				return isOk;
			}
		} else {
			errMsg("Флешка не готова к работе, проверьте ее состояние");
			return isOk;
		}

	}

}
