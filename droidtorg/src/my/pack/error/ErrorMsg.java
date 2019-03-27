package my.pack.error;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class ErrorMsg {
	// ����� ����������� ���������
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
	 * ��������� ������ �� ������ � ������
	 * 
	 * @return - ���������� true - ������ ������, false - ������ �� ������
	 */
	public boolean checkSD() {
		boolean isOk = false;
		boolean isSDMounted = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		String DIR_SD = "bluetooth";
		// ���� ������ ��������, ���� ��������� ����������� �� ������� � �������
		// ����� ���������� ������������
		if (isSDMounted == true) {
			File sdPath = Environment.getExternalStorageDirectory();
			sdPath = new File(sdPath.getAbsoluteFile() + "/" + DIR_SD);
			if (sdPath.exists()) {
				SD_PATH = sdPath.toString();
				isOk = true;

				return isOk;
			} else {
				errMsg("�� ������ �� ������ ������� " + DIR_SD);
				return isOk;
			}
		} else {
			errMsg("������ �� ������ � ������, ��������� �� ���������");
			return isOk;
		}

	}

}
