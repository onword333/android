//package my.pack.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.text.Utilities;

public class MyServer {
	public static int PORT;

	// ����� ��� ������ � DB MS Access 2003
	public static String DB_MSACCESS_CLASS = null;
	// ������ ���������� � DB MS Access 2003
	public static String DB_MSACCESS_CON = null;

	// ����� ��� ������ � DB MS SQL 2000
	public static String DB_MSSQL_CLASS = null;
	// ������ ���������� � DB MS SQL 2000
	public static String DB_MSSQL_CON = null;

	// ������� ������������
	public static String KPK_UPDATE = null;

	public final static void initProperties() throws IOException {
		Hashtable<String, String> hashTable = new Hashtable<String, String>();
		Properties prop = new Properties();

		prop.load(new FileInputStream(getApplicationStartUp()
				+ "\\config.properties"));
		Enumeration enumeration = prop.keys();

		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String value = prop.getProperty(key);
			// System.out.println(key + " = " + value);
			hashTable.put(key, value);
		}
		PORT = Integer.parseInt(hashTable.get("PORT"));
		DB_MSACCESS_CLASS = hashTable.get("DB_MSACCESS_CLASS");
		DB_MSACCESS_CON = hashTable.get("DB_MSACCESS_CON");
		DB_MSSQL_CLASS = hashTable.get("DB_MSSQL_CLASS");
		DB_MSSQL_CON = hashTable.get("DB_MSSQL_CON");
		KPK_UPDATE = hashTable.get("KPK_UPDATE");
		// System.out.println("get val: " + KPK_UPDATE);
		// System.out.println(getApplicationStartUp());

	}

	public static void initServerSocket() throws IOException {
		ServerSocket sc = new ServerSocket(PORT);

		System.out.println("Wait for connect client...");
		boolean isConnect = false;
		while (isConnect == false) {
			// System.out.println("Wait for connect client...");
			Socket incoming = sc.accept();
			System.out.println("Connect from IP: " + incoming.getInetAddress()
					+ " time: " + Utils.getCurentDate("dd:MM:yyyy HH:mm:ss"));
			Thread t = new TheadeEcho(incoming);
			t.start();
		}
		sc.close();
	}

	public static String getApplicationStartUp()
			throws UnsupportedEncodingException {
		String strPath = MyServer.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		try {
			URL url = new URL(strPath);
			strPath = url.getPath();
		} catch (MalformedURLException malformedUrlException) {

		}
		File f = new File(strPath);
		//String strResult = URLDecoder.decode(f.getParent(), "UTF-8");
		String strResult = URLDecoder.decode(f.getPath(), "UTF-8");
		return strResult;
	}

	public static void main(String[] args) {
		// System.setProperty("file.encoding","Cp866");
		try {
			initProperties();
			initServerSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class TheadeEcho extends Thread {
	private final static String startQuery = "<query>"; // ��������� ��� �������
	private final static String endQuery = "</query>";// �������� ��� �������
	private final static String startData = "<data>"; // ��������� ��� ������
	private final static String endData = "</data>";// �������� ��� ������
	private final static String query1 = "paspr1"; // ���� ��������
	private final static String query2 = "paspr2"; // ��� �����������
	private final static String query3 = "paformat"; // ������� ������
	private final static String query4 = "getrest"; // ������� �� id ���
	private final static String ENCODE = "windows-1251";

	private final static int size = 255;
	private final static byte[] data = new byte[size];

	private Socket incoming;
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	protected final ByteArrayOutputStream baot = new ByteArrayOutputStream();
	String path = "\\\\ED1_0n\\kpk\\";

	public TheadeEcho(Socket s) {
		incoming = s;
	}

	private double getCurrentRest(String idTovar) {
		double res = 0.000;
		int id = 0;
		try {
			id = Integer.parseInt(idTovar);
		} catch (NumberFormatException ex) {
			id = 0;
			ex.printStackTrace();
		}
		DB _db = new DB(MyServer.DB_MSSQL_CLASS, MyServer.DB_MSSQL_CON);
		try {
			_db.open();
			res = _db.getCurrentRest(id);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			_db.close();
		}
		return res;
	}

	/**
	 * �������� - ��������� ������� �� �������� �� �������� � �� �������: SC402;
	 * �������: CODE
	 * @param _idTorg: id ��������� �������������
	 * @return ���������� 1 - ������� �� ��������, 0 - �� �������.
	 */
	private int checkTorg(String _idTorg) {
		// �� ��������� �������� ������� �� ��������
		int isMark = 1;
		DB _db = new DB(MyServer.DB_MSSQL_CLASS, MyServer.DB_MSSQL_CON);
		try {
			_db.open();
			String[] where = new String[] { "CODE = " + _idTorg };
			ResultSet rs = _db.doSelect("SC402", null, where);
			if (rs.next()) {
				isMark = rs.getInt("ISMARK");
			}

			// System.out.println(isMark);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			_db.close();
		}
		return isMark;
	}

	private int doInsert(String[] str, String dataFromClient) {
		// ������ ������� ������. ������ �� ������� ������ ����� ���:
		// paformat|15|2012.12.3|7899
		// ��������|id ���������|���� ��������|����� ��������

		int torgID = Integer.parseInt(str[1]);
		String dateSend = Utils.convertDate(str[2], "yyyy.MM.d", "MM-dd-yyyy");
		String currentDate = Utils.getCurentDate("MM-dd-yyyy HH:mm:ss");
		int sendID = Integer.parseInt(str[3]);

		// ��������� � ������ id ��������� �������� ����� ������
		dataFromClient = torgID + "\r\n" + dataFromClient;

		int res = insertDoc(currentDate, dataFromClient, torgID, sendID,
				dateSend);
		return res;
	}

	private int insertDoc(String currentDate, String str, int torgID,
			int sendID, String dateSend) {
		int res = 0;
		DB _db = new DB(MyServer.DB_MSACCESS_CLASS, MyServer.DB_MSACCESS_CON);
		String[] field = new String[] { "fDate", "fText", "fCheck", "fTorgID",
				"fSendID", "fSendDate", "fSendVer" };
		String[] val = new String[] { "#" + currentDate + "#",
				"'" + str.replaceAll("'", "''") + "'", "0", "" + torgID + "",
				"" + sendID + "", "#" + dateSend + "#", "1" };
		try {
			_db.open();
			res = _db.inserRow("tblConnect", field, val);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			_db.close();
		}
		return res;
	}

	/**
	 * �������� - ��������� � ���������� ����
	 * @param nameFile - ���� � ����� ��� ����������
	 * @param bos - ��������� ����� ���� BufferedOutputStream
	 */
	private void sendFile(String nameFile, BufferedOutputStream bos) {
		FileProcess fp = new FileProcess();
		try {
			fp.openFile(nameFile + ".txt");

			/* int size = 255; byte[] data = new byte[size]; */
			int read = 0;
			int totalRead = 0;

			while ((read = fp.readByte(data, data.length)) != -1) {
				bos.write(data, 0, read);
				totalRead += read;
			}
			bos.flush();
			System.out.println("	Sending bytes = " + totalRead + " file: "
					+ nameFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fp.closeFile();
		}
	}

	private void closeConnect(Socket incoming) {
		try {
			// ��������� �������� �����
			if (bis != null) {
				bis.close();
				bis = null;
			}

			// ��������� ��������� �����
			if (bos != null) {
				bos.close();
				bos = null;
			}

			// ��������� �����
			if (incoming != null) {
				if (incoming.isConnected()) {
					incoming.close();
					incoming = null;
				}
			}
		} catch (IOException e) {
			System.out.println("Scoket no close!!!");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		String job, name;

		try {
			// ����� ������� � �������� �����
			bis = new BufferedInputStream(incoming.getInputStream());
			bos = new BufferedOutputStream(incoming.getOutputStream());

			/* int size = 255; byte[] data = new byte[size]; */

			int read;

			// ��������� ��� ������ ������� ������� ������
			baot.reset();
			while ((read = bis.read(data, 0, data.length)) != -1) {
				baot.write(data, 0, read);
			}
			String str = baot.toString(ENCODE);
			baot.close();
			// �������� ������ �������
			String strQuery = Utils.extract(str, startQuery, endQuery);
			// �������� ������ � ������� (��������. ������)
			String dataFromClient = Utils.extract(str, startData, endData);
			// ���� ������ strQuery �� ���������� ������ ������ ������ ��
			// ��������
			if (strQuery == null) {
				System.out.println("No query");
				return;
			}

			// ��������� ������ �� ���������� �������:
			// [0],[1] - ������������ �������, ������ ���� ���������.
			// [0] - ��� ����� �������, [1] - id ���������
			// ��������� ������� ������� �� �������� � [0]
			// ���� [0] = query1, �� [2] - ��� ����� ��� ����������
			// ���� [0] = query2, �� [2] - ��� ����� ��� ����������
			// ���� [0] = query3, �� [2] - ���� �������� �������� � ����
			// ���� [0] = query4, �� [2] - id ���
			// yyyy.MM.d; [3] - id ��������

			String[] arrStr = strQuery.split("\\|");
			job = arrStr[0];

			// ���� �������� �� ������� �� ��������, �� ������, ��� ������
			// ����� � ������� ��������
			if (checkTorg(arrStr[1]) == 0) {
				/*try {
					System.out.println("����� ����");
					TheadeEcho.sleep(30 * 1000);
					System.out.println("����� ���������");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				if (job.equalsIgnoreCase(query1)) {
					// ������ �������� ���� ��������
					sendFile(MyServer.KPK_UPDATE + arrStr[2], bos);
				} else if (job.equalsIgnoreCase(query2)) {
					// ������ �������� ����

					sendFile(MyServer.KPK_UPDATE + arrStr[2], bos);

				} else if (job.equalsIgnoreCase(query3)) {
					// ������ ������� ��������
					int res = 0;

					// ���� ������ ����������, �� ������ ������� ����� ���
					if (dataFromClient != null) {
						res = doInsert(arrStr, dataFromClient);
					}

					String msg = "";
					if (res > 0) {
						msg = "	The document is accepted from: id = " + arrStr[1];
						bos.write("OK".getBytes(ENCODE));
						bos.flush();
					} else {
						msg = "	The document NOT accepted from: id = " + arrStr[1];
						bos.write("NO".getBytes(ENCODE));
						bos.flush();
					}
					System.out.println(msg + " "
							+ incoming.getRemoteSocketAddress() + " time: "
							+ Utils.getCurentDate("dd.MM.yyyy HH:mm:ss"));

				} else if (job.equalsIgnoreCase(query4)) {
					// ������ �������� ������� ��� (�� id)
					String rest = getCurrentRest(arrStr[2]) + "";
					bos.write(rest.getBytes(ENCODE));
					bos.flush();
					System.out.println("	Get current rest for id = "
							+ arrStr[1] + " " + arrStr[2]);
				} else {

				}
			} else {
				System.out.println("	Access denied for torg with code: id = "
						+ arrStr[2] + " " + arrStr[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// fp.closeFile();
			closeConnect(incoming);
		}
	}

	public String extract(String str, String start, String end) {
		int s = str.indexOf("\n\n", 0), e;
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
		return (str.substring(s, e)).trim();
	}
}

class DB {
	private Connection con = null;
	private static String DB_CLASS = null;
	private static String DB_STR_CONNECTION = null;

	public DB(String dbClass, String dbStrCon) {
		DB_CLASS = dbClass;
		DB_STR_CONNECTION = dbStrCon;
	}

	/**
	 * ��������� - �������� ������� ��� �� id
	 * @param id - ���������� ��� ��� � ��
	 * @return - ���������� ������� �������(�� ���� �������)
	 * @throws SQLException
	 */
	public double getCurrentRest(int id) {
		double res = 0.000;
		CallableStatement proc_stmt = null;
		try {
			proc_stmt = con.prepareCall("{ call PA_GETREST(?, ?) }");
			proc_stmt.registerOutParameter(2, java.sql.Types.DOUBLE);
			proc_stmt.setInt(1, id);
			proc_stmt.execute();
			res = proc_stmt.getDouble(2);
			// System.out.println("_____-----" + res + "");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (proc_stmt != null) {
				try {
					if (!proc_stmt.isClosed()) {
						proc_stmt.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	protected ResultSet doSelect(String tbl, String[] columns, String[] where)
			throws SQLException {
		String strSql = "SELECT";
		String strCol = "";
		String strWhere = "";
		String sep = ", "; // ����������� ����� � ��������
		if (columns == null) {
			strSql = "SELECT * FROM " + tbl;
		} else {
			for (int i = 0; i < columns.length; i++) {
				if ((columns.length - 1) == i) {
					sep = "";
				}
				strCol += columns[i] + sep;
			}
		}

		if (where != null) {
			strWhere = " WHERE ";
			for (int i = 0; i < where.length; i++) {
				strWhere += where[i];
			}
		}
		strSql = strSql + strCol + strWhere;

		Statement stat = con.createStatement();

		// System.out.println("result: " + rs.getString(1) + " "
		// + rs.getString("DESCR") + " " + rs.getInt("ISMARK"));
		// System.out.println(strSql);
		return stat.executeQuery(strSql);
	}

	protected int inserRow(String tbl, String[] field, String[] val)
			throws SQLException {

		String strSql = "INSERT INTO " + tbl;
		String strSqlField = "";
		String strSqlVal = "";
		String sep = ", "; // ����������� ����� � ��������
		for (int i = 0; i < field.length; i++) {
			if ((field.length - 1) == i) {
				sep = "";
			}
			strSqlField += field[i] + sep;
			strSqlVal += val[i] + sep;
			// System.out.println(val[i] + "");
		}
		strSql = strSql + " (" + strSqlField + ") VALUES (" + strSqlVal + ")";

		// System.out.println(strSql);

		Statement stat = con.createStatement();
		int res = stat.executeUpdate(strSql);
		con.commit();
		return res;
	}

	/**
	 * <b>��������</b> - ��������� ���������� � �����
	 * @throws SQLException
	 */
	protected void close() {
		if (con != null) {
			try {
				if (!con.isClosed()) {
					con.close();
					con = null;
					// System.out.println("���� �������");
				}
			} catch (SQLException e) {
				System.out.println("Database not close");
				e.printStackTrace();
			}
		}
	}

	/**
	 * <b>��������</b> - ��������� ���������� � �����
	 * 
	 * @throws SQLException
	 */
	protected void open() throws SQLException, ClassNotFoundException {
		// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Class.forName(DB_CLASS);
		con = DriverManager.getConnection(DB_STR_CONNECTION);
		con.setAutoCommit(false);
	}
}

class FileProcess {
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private ByteArrayOutputStream baot = null;
	private final static int SIZE_INPUT_STREAM = 1024 * 100;

	public void closeFile() {
		try {
			if (fis != null) {
				fis.close();
				fis = null;
			}

			if (bis != null) {
				bis.close();
				bis = null;
			}
		} catch (Exception e) {
			System.out.println("File not close!!!");
			e.printStackTrace();
		}
	}

	public int readByte(byte[] data, int offSet) throws IOException {
		return bis.read(data, 0, offSet);
	}

	public void openFile(String name) throws FileNotFoundException {
		fis = new FileInputStream(name);
		bis = new BufferedInputStream(fis);
	}

	/**
	 * <b>��������</b> - ���������� ������ ����� � ������, ���� ��������� ���
	 * ������� ����� ���������� -1
	 * @return - ������ ����� � ������
	 */
	public long getFileSize() {
		try {
			return fis.getChannel().size();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}

class Utils {
	private final static SimpleDateFormat dateBefore = new SimpleDateFormat();
	private final static SimpleDateFormat dateAfter = new SimpleDateFormat();

	/**
	 * �������� - �������� �� str ������ ����� start � end
	 * @param str - ������ ��� ������
	 * @param start - ��������� ������
	 * @param end - �������� ������
	 * @return - ���������� ������ ����� start � end, ���� ����� ������ ��
	 *         ���������� - null
	 */
	protected static String extract(String str, String start, String end) {
		String strBetweenStartEnd = null;
		int startIndex = str.indexOf(start);
		int endIndex = str.indexOf(end);

		int startLengthStr = start.length();
		int endLengthStr = end.length();

		// ����, ������ start � ������ end �������,
		// �� ��������� ������ ����� ����
		if ((startIndex >= 0) & (endIndex >= 0)) {

			// �������� ������ ������ ���� ������ ���������� �������, �����
			// ������ ����� start � end �� ����������
			if ((startLengthStr + startIndex) < endIndex) {
				strBetweenStartEnd = str.substring(startLengthStr + startIndex,
						endIndex);
			}
		}
		return strBetweenStartEnd;
	}

	/**
	 * �������� - ���������� ������� ���� �� ������� pattern
	 * @param pattern - ������
	 * @return - ������� ����
	 */
	protected static String getCurentDate(String pattern) {
		dateBefore.applyPattern(pattern);
		String date = dateBefore.format(Calendar.getInstance().getTime());
		return date;
	}

	public static String convertDate(String strDate, String patterBefore,
			String patternAfter) {
		dateBefore.applyPattern(patterBefore);
		dateAfter.applyPattern(patternAfter);
		String strDateFormat = null;
		try {
			strDateFormat = dateAfter.format(dateBefore.parse(strDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strDateFormat;
	}
}
