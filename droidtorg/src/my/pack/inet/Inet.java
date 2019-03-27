package my.pack.inet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;

import my.pack.error.ErrorMsg;
import my.pack.namespace.Utils;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Inet {
	private final static String TAG = "Inet";
	// protected SocketAddress serverAddress;
	// protected SocketAddress endPoint;
	private String endPoint = null;
	protected Socket socket = null;
	private BufferedInputStream bis = null;
	private BufferedOutputStream bos = null;
	protected final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private ByteArrayInputStream bais;

	protected final int timeOut = 60 * 1000; // 90 секунд

	public final String NO_ROWS = "EF";

	protected String strException = "";

	public final static String ENCODING = "windows-1251";
	protected static int PORT;
	protected Context ctx;
	// protected String urlAdres;
	protected String host;
	protected String checkOst = "paspr1";
	public int readCountRows = 0;
	protected final ErrorMsg err;
	protected final StringBuilder strBuild = new StringBuilder();
	protected final String sep = System.getProperty("line.separator");

	// protected String sep = "\r\n";

	public void msg(String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
	}

	public ByteArrayOutputStream getByteOutput() {
		return baos;
	}

	public void openSocket() throws IOException {
		socket = new Socket(endPoint, PORT);
		socket.setSoTimeout(timeOut);
		
		
		
		Utils.ShowLog("IP ADDREss", endPoint + "");

		// socket.connect(endPoint);

	}

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public OutputStream getOutpuStream() throws IOException {
		return socket.getOutputStream();
	}

	public void readFromSocket(int size, InputStream is) throws IOException {
		bis = new BufferedInputStream(is);
		
		baos.reset();
		byte[] data = new byte[size];
		int read;
		while ((read = bis.read(data, 0, data.length)) != -1) {
			baos.write(data, 0, read);
		}
	}

	public void writeToSocket(int size, OutputStream out) throws IOException {		
		bos = new BufferedOutputStream(out);
		bais = new ByteArrayInputStream(checkOst.getBytes(ENCODING));
	
		byte[] data = new byte[size];
		int read;
		while ((read = bais.read(data, 0, data.length)) != -1) {
			out.write(data, 0, read);
		}
		out.flush();
		bais.close();
	}

	protected void closeOutputStream() {
		if (bos != null) {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				bos = null;
			}
		}
	}

	protected void closeInputStream() {
		if (bis != null) {
			try {
				bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bis = null;
			}
		}
	}

	protected void closeSocket() {
		if ((socket != null) && (socket.isConnected())) {
			if (!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					socket = null;
				}
			}
		}
	}

	protected void shutDownInput() {
		/* try { if (socket != null) { if (socket.isConnected()) { if
		 * (!socket.isInputShutdown()) { // Log.d(TAG,"shutDownInput(): " + //
		 * socket.isConnected()); true // Log.d(TAG, "isInputshutDown(): " + //
		 * socket.isInputShutdown()); false
		 * 
		 * socket.shutdownInput(); // срабатывает исключение Log.d(TAG,
		 * "ЗАКРЫТИЕ"); } } } } catch (IOException e) { e.printStackTrace(); } */
	}

	public void shutDownOutput() {
		try {
			if (socket != null) {
				if (!socket.isOutputShutdown()) {
					socket.shutdownOutput();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Закрывает объявленные поля класса <b>Inet</b> <br>
	 * {@link InputStream} is, <br>
	 * {@link InputStreamReader} isr, <br>
	 * {@link OutputStream} out, <br>
	 * {@link BufferedReader} bfr, <br>
	 * {@link Socket} socket
	 */
	public void closeConnect() {
		// shutDownOutput();
		// shutDownInput();
		closeOutputStream();
		closeInputStream();
		closeSocket();
	}

	public void changeQuery(String str) {
		this.checkOst = str;
	}

	public String setDateFormat(String[] args) {
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
			str = "Остатки: " + dateDownload + "\n";
			str += "Дебиторка c " + dateDebitorkaFrom + " по "
					+ dateDebitorkaTo;
		} catch (ParseException e) {
			str = "Ошибка конвертации даты.\r\n" + e.toString();
			e.printStackTrace();
		}
		return str;
	}

	public String formatReplay(String str) {
		String strArray[] = str.split("\\|", 17);
		String strFormat = setDateFormat(strArray);
		return strFormat;
	}

	// "вырезает" из строки str часть, находящуюся между строками start и end
	// если строки end нет, то берётся строка после start
	// если кусок не найден, возвращается null
	// для поиска берётся строка до "\n\n" или "\r\n\r\n", если таковые
	// присутствуют
	public String extract(String str, String start, String end) {
		int s = str.indexOf("\n\n", 0), e;
		Log.d(TAG, str.indexOf("\r\n", 0) + "");
		if (s < 0)
			s = str.indexOf("\r\n\r\n", 0);
		if (s > 0)
			str = str.substring(0, s);
		s = str.indexOf(start, 0) + start.length();
		if (s < start.length())
			return null;
		e = str.indexOf(end, s);
		if (e < 0)
			e = str.length();
		host = (str.substring(s, e)).trim();
		return (str.substring(s, e)).trim();
	}

	public String extraxt(String str, int start, int end) {
		str = str.substring(start, end).trim();
		return str;
	}

	/* public String getHost2() throws MalformedURLException, IOException,
	 * IllegalArgumentException { String str = ""; URL url = null; url = new
	 * URL(urlAdress);
	 * 
	 * HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
	 * 
	 * urlCon.setRequestProperty("Accept-Charset", "WINDOWS-1251");
	 * urlCon.setRequestProperty("Content-Type",
	 * "text/plain; charset=windows-1251");
	 * 
	 * // считываем данные readFromSocket(255, urlCon.getInputStream()); str =
	 * baos.toString(ENCODING); urlCon.disconnect();
	 * 
	 * 
	 * host = extract(str, "PAHOST: ", "\n"); serverAddress = new
	 * InetSocketAddress(host, PORT); urlCon = null; url = null;
	 * 
	 * return str; } */
	public Inet(String urlStr, int port, Context c) {
		ctx = c;
		endPoint = urlStr.trim();
		// urlAdress = urlStr;
		err = new ErrorMsg(ctx);
		// socket = new Socket();
		PORT = port;
		// serverAddress = new InetSocketAddress(urlAdress, PORT);
	}

}
