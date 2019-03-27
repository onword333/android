package my.pack.dialog;

import my.pack.namespace.R;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class DialogFactory {
	public final static int DIALOG_ALERT = 1;
	public final static int DIALOG_PROGRESS = 5;
	public final static int DIALOG_INPUT = 3;
	public final static int DIALOG_PROGR_HORIZ = 4;
	public static String inputFromDialog;
	public static String MESSAGE = "MESSAGE";
	public static String TITLE = "info";
	public static Dialog dialog;
	public Context con;

	// public static Dialog dialog = null;

	public DialogFactory(Context cx) {
		con = cx;
		// TODO Auto-generated constructor stub
	}

	public static Dialog getDialogById(int id, final Context context) {
		//TITLE = context.getResources().getString(R.string.dialog_title);
		Dialog dialog = null;
		switch (id) {
		case DIALOG_ALERT:

			dialog = createAlertDialog(context);
			break;
		case DIALOG_PROGRESS:

			dialog = createProgressDialog(context);
			break;
		case DIALOG_INPUT:

			// dialog = createInputAlert(context);
			break;

		case DIALOG_PROGR_HORIZ:

			dialog = createProgDialogHoriz(context);
			break;
		}

		return dialog;
	}

	private static Dialog createAlertDialog(final Context context) {
		Dialog dialog;
		Builder builder = new Builder(context);

		// �������� ��������� ���� ��� �����
		final EditText editText = new EditText(context);

		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String input = editText.getText().toString();

				
				dialog.dismiss();
			}
		};

		/*
		 * dialog = builder.setTitle("Input Alert")
		 * .setMessage("Please enter something") .setPositiveButton("Ok",
		 * onClickListener).setView(editText) .create();
		 */
		/*
		 * dialog = builder.setTitle("Input Alert").setMessage(MESSAGE)
		 * .setPositiveButton("Ok", onClickListener).setView(editText)
		 * .create();
		 */
		dialog = builder.setTitle(TITLE).setMessage(MESSAGE)
				.setPositiveButton("Ok", onClickListener).create();
		return dialog;
	}

	private static Dialog createProgressDialog(final Context context) {
		//ProgressDialog dialog;
		dialog = new ProgressDialog(context);
		((ProgressDialog) dialog).setMessage("Please wait");
		
		/*
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss",
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				*/
		return dialog;
	}

	public static void closeDialog() {
		dialog.dismiss();
	}
	

	
	private static Dialog createProgDialogHoriz(final Context context) {
		ProgressDialog dialog;
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle(TITLE);
		dialog.setMessage(MESSAGE);
		/*dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss",
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});*/
		
		return dialog;
	}

	public static String getValue() {
		return inputFromDialog;
	}
}
