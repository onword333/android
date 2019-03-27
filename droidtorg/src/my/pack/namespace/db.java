package my.pack.namespace;

//import my.pack.namespace.R.id;
//import android.R.integer;

import java.text.ParseException;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class db {

	// ������� �������� � LogCat ����������
	protected final int IS_LOG = 1;
	private final String TAG = "DB";
	// ������� � ������ ������, ������ ���������� ������� �������� ���
	// ������������ ����
	// ������������ �������
	public String[] fTovar;
	public String[] fCounteragent;
	public String[] fMag;
	public String[] fEdIzm;
	public String[] fEdIzmKr;
	public String[] fTovKr;
	public String[] fTovSetka;
	public String[] fPartnerPrice;
	public String[] fDebetorka;
	public String[] fConstant;
	public String[] fDocHead;
	public String[] fDocTable;

	private final static String DB_NAME = "simpleadapter.db";
	private final static int DB_VERSION = 8;
	private int DB_CURRENT_VERSION;
	private int typePrice = 2;
	protected int ID_DOC = 0;

	public final static String TBL_CONSTANT = "tblConstant";

	// tblDebitorka - ���������
	public final static String TBL_DEBITORKA = "tblDebitorka";
	public final static String DEB_ID = "_id";
	public final static String DEB_NDOC = "fNDoc";
	public final static String DEB_DDOC = "fDDoc";
	public final static String DEB_VDOC = "fVDoc";
	public final static String DEB_KEYPART = "fKeyPartner";
	public final static String DEB_KEYMAG = "fKeyMag";
	public final static String DEB_PRIHOD = "fPrihod";
	public final static String DEB_RASHOD = "fRashod";

	// tblPartnerPrice - ����� ������������
	public final static String TBL_PPICE = "tblPartnerPrice";
	public final static String PPICE_ID = "_id";
	public final static String PPICE_KEYPARTNER = "fKeyPartner";
	public final static String PPICE_KEYTOV = "fKeyTovar";
	public final static String PPICE_KEYTOVSETKA = "fKeyTovarSetka";
	public final static String PPICE_BASE = "fBase";
	public final static String PPICE_PRICE = "fPrice";
	public final static String PPICE_PARCENT = "fParcent";
	public final static String PPICE_HOW = "fHow";

	// tblTovarSetka - ����� �������, ���. �������� ������
	public final static String TBL_SETKA = "tblTovarSetka";
	public final static String SETKA_ID = "_id";
	public final static String SETKA_KEY = "fKey";
	public final static String SETKA_NAME = "fName";
	public final static String SETKA_PRICE = "fPrice";
	public final static String SETKA_KEYTOV = "fKeyTovar";

	// tblEdIzmKr - ���������� ��������� ��. ��������� (�������� ���. ��.
	// ��������� tblEdIzm)
	// ���� ����������� ��� ������ ���������
	public final static String TBL_EDMKR = "tblEdIzmKr";
	public final static String EDMKR_ID = "_id";
	public final static String EDMKR_KEY = "fKey";
	public final static String EDMKR_NAME = "fName";
	public final static String EDMKR_KR = "fKr";
	public final static String EDMKR_KEYEDIZM = "fKeyEdIzm";

	// tblEdIzm - ���������� ������� ���������
	// ���� ����������� ��� ������ ���������
	public final static String TBL_EDM = "tblEdIzm";
	public final static String EDM_ID = "_id";
	public final static String EDM_NAME = "fName";

	// tblClient - ���������� ��������
	// ���� ����������� ��� ������ ���������
	public final static String TBL_CLIENT = "tblCounteragent";
	public final static String CL_ID = "_id";
	public final static String CL_KEY = "fKey";
	public final static String CL_NAME = "fName";
	public final static String CL_RESTOUR = "fRestOur";
	public final static String CL_RESTTHEM = "fRestThem";
	public final static String CL_TYPEPRICE = "fTypePrice";
	public final static String CL_PARCENTPRICE = "fPercentPrice";

	// tblMag - ���������� ��������� (�������� ���. �����������)
	// ���� ����������� ��� ������ ���������
	public final static String TBL_MAG = "tblMag";
	public final static String MAG_ID = "_id";
	public final static String MAG_KEY = "fKey";
	public final static String MAG_RESTOUR = "fRestOur";
	public final static String MAG_RESTTHEM = "fRestThem";
	public final static String MAG_KEYPARTNER = "fKeyPartner";
	public final static String MAG_NAME = "fName";

	// tblDocTable - ��������� ����� ���������(������)
	// ���� ����������� ��� ������ ���������
	public final static String TBL_DT = "tblDocTable";

	public final static String DT_KEYZ = "fKeyZ"; // Y

	public final static String DT_KEYTOV = "fKeyTovar"; // Y

	public final static String DT_COUNT = "fCount"; // Y

	public final static String DT_PRICE = "fPrice"; // Y

	public final static String DT_SUM = "fSum";
	public final static String DT_EDIZM = "fKeyEdIzm";
	public final static String DT_EDIZMKR = "fKeyEdIzmKr";
	public final static String DT_COUNTKR = "fCountKr";

	// tblDocHead - ����� ���������(������)
	public final static String TBL_DH = "tblDocHead";
	public final static String DH_KEYZ = "_id";
	public final static String DH_KEYCLIENT = "fKeyPartner";
	public final static String DH_DATE = "fDate";
	public final static String DH_SUM = "fSum";
	public final static String DH_OUT = "fOut";
	public final static String DH_NOTE = "fNote";
	public final static String DH_KEYMAG = "fKeyMag";

	// tblTovar - ���������� ������������
	// ���� ����������� ��� ������ ���������
	public final static String TBL_TOVAR = "tblTovar";
	public final static String TOV_ID = "_id"; // id ������
	public final static String TOV_NAME = "fName"; // ������. ������
	public final static String TOV_KEYEDIZM = "fKeyEdIzm"; // ��. ���������
	public final static String TOV_PRICE = "fPriceO"; // ����
	public final static String TOV_QUANTITY = "fCount"; // ����������
	public final static String TOV_GROUP = "fIsGroup";
	public final static String TOV_LEVEL = "fLevel";
	public final static String TOV_CODEPARENT = "fCodeParent";
	public final static String TOV_ISRED = "fIsRed";
	public final static String TOV_PRICEKOPT = "fPriceKOpt";
	public final static String TOV_PRICEOPT = "fPriceOpt";
	public final static String TOV_PRICEROZN = "fPriceRozn";
	public final static String TOV_REST = "fRest";
	public final static String TOV_PRICEO = "fPriceO";
	public final static String TOV_PRICEMOPT = "fPriceMOpt";
	public final static String TOV_CODEROOT = "fCodeRoot";
	public final static String TOV_PRICEBN = "fPriceBN";
	public final static String TOV_PRICEOPT2 = "fPriceOpt2";
	public final static String TOV_PRICEVIP = "fPriceVip";

	// tblTovKr - ���������� ������������
	// ���� ����������� ��� ������ ���������
	public final static String TBL_TOVARKR = "tblTovKr";
	public final static String TOVKR_ID = "_id"; // id ������
	public final static String TOVKR_KEYTOV = "_id"; // ���. ������
	public final static String TOVKR_KEYKR = "fKeyKr"; // ��� ��. �����.
														// ���������

	public final static String _ID = "_id";

	private Context mCtx;
	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;

	public ContentValues cv;

	// ��������� ������ � ����� ������
	protected String tbl_name, where, group_by, having, order_by;
	protected String[] columns, where_arg;

	public DBHelper test2() {

		return mDBHelper;
	}

	/**
	 * @return - ���������� ������� ������ ����
	 */
	public int getVer() {
		return DB_CURRENT_VERSION;
	}

	/**
	 * @param tbl_name - ��� �������
	 * @param columns - ������ �����
	 * @param where - ���� �������
	 * @param where_arg - ������ �������� ��� where
	 * @param group_by - ����������� ��...
	 * @param having - ������������� ������� ��� ���������� �������
	 * @param order_by - ���������� �� ���_�������
	 * @return - ���������� ������
	 */
	public void setField(String tbl_name, String[] columns, String where,
			String[] where_arg, String group_by, String having, String order_by) {
		this.tbl_name = tbl_name;
		this.columns = columns;
		this.where = where;
		this.where_arg = where_arg;
		this.group_by = group_by;
		this.having = having;
		this.order_by = order_by;
	}

	public Cursor fetchCountriesByName(String inputText) throws SQLException {
		Log.w("fetchCountriesByName", inputText);

		// doSelect();
		Cursor mCursor = doSelect();
		/* mCursor = mDB.query(true, tblName, field, "fName like '%" +
		 * inputText.toUpperCase() + "%'", null, null, null, null, null); */
		/* if (inputText == null || inputText.length() == 0) { mCursor =
		 * mDB.query(TBL_CLIENT, new String[] { "_id", "fName", "fRestThem",
		 * "fRestOur", "fTypePrice", "fPercentPrice" }, null, null, null, null,
		 * null); } else { mCursor = mDB.query(true, TBL_CLIENT, new String[] {
		 * "_id", "fName", "fRestThem", "fRestOur", "fTypePrice",
		 * "fPercentPrice" }, "fName like '%" + inputText.toUpperCase() + "%'",
		 * null, null, null, null, null); } */
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public db(Context c) {

		mCtx = c;
		Resources res = mCtx.getResources();
		fTovar = res.getStringArray(R.array.arr_field_tovar);
		// Arrays.sort(fTovar);

		fCounteragent = res.getStringArray(R.array.arr_fields_counteragent);
		// Arrays.sort(fCounteragent);

		fMag = res.getStringArray(R.array.arr_fields_mag);
		// Arrays.sort(fMag);

		fEdIzm = res.getStringArray(R.array.arr_fields_edizm);
		// Arrays.sort(fEdIzm);

		fEdIzmKr = res.getStringArray(R.array.arr_fields_edizmkr);
		// Arrays.sort(fEdIzmKr);

		fTovKr = res.getStringArray(R.array.arr_fields_tovkr);
		// Arrays.sort(fTovKr);

		fTovSetka = res.getStringArray(R.array.arr_fields_tovsetka);
		// Arrays.sort(fTovSetka);

		fPartnerPrice = res.getStringArray(R.array.arr_fields_part_price);
		// Arrays.sort(fPartnerPrice);

		fDebetorka = res.getStringArray(R.array.arr_field_debetorka);
		// Arrays.sort(fMag);

		fConstant = res.getStringArray(R.array.arr_fields_constant);
		fDocHead = res.getStringArray(R.array.arr_fields_dochead);
		fDocTable = res.getStringArray(R.array.arr_fields_doctable);

		cv = new ContentValues();
	}

	protected String getFDocHead(int arg0) {
		return fDocHead[arg0];
	}

	protected String getValCv(String key) {
		return cv.getAsString(key);
	}

	protected String tblTovar() {
		return TBL_TOVAR;
	}

	protected String tblClient() {
		return TBL_CLIENT;
	}

	public String tblDH() {
		return TBL_DH;
	}

	public String tblMag() {
		return TBL_MAG;
	}

	public String tblTov() {
		return TBL_TOVAR;
	}

	public String tblDT() {
		return TBL_DT;
	}

	public String definePrice(int typePrice) {

		String price;
		switch (typePrice) {
		case 0:
			price = TOV_PRICEOPT;
			break;
		case 1:
			price = TOV_PRICEKOPT;
			break;
		case 2:
			price = TOV_PRICEOPT;
			//price =TOV_PRICEOPT2;
			break;
		case 3:
			price = TOV_PRICEMOPT;
			break;
		case 4:
			price = TOV_PRICEROZN;
			break;
		case 5:
			price = TOV_PRICEBN;
			break;
		case 6:
			price = TOV_PRICEOPT2;
			break;
		case 7:
			price = TOV_PRICEVIP;
			break;
		// case 8:
		// price = TOV_PRICEOPT2;
		// break;
		// case 9:
		// price = TOV_PRICEVIP;
		// break;
		// case 10:
		// price = this.fPrice();
		// break;
		default:
			price = TOV_PRICEOPT;
			break;

		}
		return price;
	}

	/**
	 * ������������� ��� ����
	 * 
	 * @param arg - �� 0 �� 7 (��. ����� definePrice())
	 */
	protected void setTypePrice(int arg) {
		typePrice = arg;
	}

	protected String fTypePrice() {
		return CL_TYPEPRICE;
	}

	public String fLevel() {
		return TOV_LEVEL;
	}

	public String fCodeRoot() {
		return TOV_CODEROOT;
	}

	public String fOut() {
		return DH_OUT;
	}

	public String fkMag() {
		return DH_KEYMAG;
	}

	public String fNote() {
		return DH_NOTE;
	}

	public String fDate() {
		return DH_DATE;
	}

	public String fkPartner() {
		return MAG_KEYPARTNER;
	}

	public String fThem() {
		return CL_RESTTHEM;
	}

	public String fOur() {
		return CL_RESTOUR;
	}

	public String fIsGroup() {
		return TOV_GROUP;
	}

	public String fName() {
		return TOV_NAME;
	}

	protected String fName2() {
		return TOV_NAME + "2";
	}

	public String fKrat() {
		return DT_COUNTKR;
	}

	public String fIdEdIzmKr() {
		return DT_EDIZMKR;
	}

	public String fIdEdIzm() {
		return DT_EDIZM;
	}

	public String fSum() {
		return DT_SUM;
	}

	public String fPrice() {
		return DT_PRICE;
	}

	public String fCount() {
		return DT_COUNT;
	}

	public String fIdTov() {
		return DT_KEYTOV;
	}

	public String fID() {
		return _ID;
	}

	public String fIdDoc() {
		return DT_KEYZ;
	}

	public boolean _isOpen() {

		return mDB.isOpen();
	}

	/* public boolean _isClose() { return true; } */

	public void open() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
		DB_CURRENT_VERSION = mDB.getVersion();

	}

	public void close() {
		if (mDBHelper != null) {
			mDB.close();
		}
	}

	public void deleteAll() {
		mDB.delete(TBL_TOVAR, null, null);
	}

	// ------------------------------------------------------------

	public Cursor customQuery(String str) {
		return mDB.rawQuery(str, null);
	}

	// ============================================================

	public void deleteDocJoin(int id) {
		Cursor Cur;
		String table = "" + tblDH() + " as h LEFT JOIN " + tblDT()
				+ " as t ON h." + fID() + " = t." + DT_KEYZ + "";
		String where = "h." + fID() + " = " + id + "";
		mDB.delete(table, where, null);

	}

	public void del_fVal() {

	}

	// ============================================================

	public int updateRow(String tbl, String where, String[] fVal) {
		int rowCount = mDB.update(tbl, cv, where, fVal);
		return rowCount;
	}

	// ============================================================

	// ��������� ������ �� ID � ���������� ���. ����������� �����
	public int updateForID(String tbl) {
		int rowCount = mDB.update(tbl, cv, "_id = ?", new String[] { ""
				+ ID_DOC + "" });
		return rowCount;
	}

	// ============================================================

	public void deleteDoc() {
		Cursor Cur;
		String q = "DELETE FROM " + tblDT() + " WHERE " + fIdDoc() + " = "
				+ ID_DOC + "";
		Cur = customQuery(q);
		int dtCount = Cur.getCount();

		q = "DELETE FROM " + tblDH() + " WHERE " + fID() + " = " + ID_DOC + "";
		Cur = customQuery(q);
		int dhCount = Cur.getCount();

	}

	// ------------------------------------------------------------

	/**
	 * @return (������ � ���. tblDocHead) ���������� true - ���� ������ �
	 *         ��������� ID_DOC ����������, ����� false
	 */
	protected boolean isExistsDH() {
		boolean isExists = false;
		String q = "SELECT a." + fID() + " AS " + fID() + " FROM " + tblDH()
				+ " a WHERE a." + fID() + " = " + ID_DOC + "";
		Cursor docCur = customQuery(q);
		if (docCur.getCount() > 0) {
			isExists = true;
		}
		return isExists;
	}

	// ------------------------------------------------------------

	/**
	 * @return ���������� ������ ������
	 */
	protected Cursor getJournal() {
		String q = "SELECT a." + fID() + " AS " + fID() + ", a." + fkPartner()
				+ " AS " + fkPartner() + ", a." + fDate() + " AS " + fDate()
				+ ", b." + fName() + " AS " + fName() + ", a." + fSum()
				+ " AS " + fSum() + ", a. " + getFDocHead(4) + " AS "
				+ getFDocHead(4) + " FROM " + tblDH() + " a ";

		String j = "LEFT JOIN " + tblClient() + " b ON a." + fkPartner()
				+ " = b." + fID() + " ORDER BY a." + fID() + " DESC";

		// return q + j;
		return mDB.rawQuery(q + j, null);
	}

	/**
	 * @return ���������� ����� ���������� ���������
	 */
	protected int idDoc() {
		int id = 0;
		String q = "SELECT a." + fID() + " AS " + fID() + " FROM " + tblDH()
				+ " a ORDER BY a." + fID() + " ASC";
		Cursor docCur = customQuery(q);
		if (docCur.getCount() > 0) {
			docCur.moveToLast();
			int index = docCur.getColumnIndex(fID());
			id = docCur.getInt(index);
		}
		return id;
	}

	protected Cursor getDocHead() {
		String q, j, j2, where;
		q = "SELECT a." + fkPartner() + " AS " + fkPartner() + ", a." + fkMag()
				+ " AS " + fkMag() + ", a." + fDate() + " AS " + fDate()
				+ ", b." + fName() + " AS " + fName() + ", b." + fTypePrice()
				+ " AS " + fTypePrice() + ", c." + fName() + " AS " + fName2()
				+ ", a." + fNote() + " AS " + fNote() + ", a." + fOut()
				+ " AS " + fOut() + " FROM " + tblDH() + " a";

		j = " LEFT JOIN " + tblClient() + " b ON a." + fkPartner() + " = b."
				+ fID() + "";
		j2 = " LEFT JOIN " + tblMag() + " c ON a." + fkMag() + " = c." + fID()
				+ "";
		where = " WHERE a." + fID() + " = " + ID_DOC + "";
		// Log.d("DB", q + j + j2 + where);
		return mDB.rawQuery(q + j + j2 + where, null);
	}

	/**
	 * ���������� ����� ������ �� ������ ���������
	 */
	public double getDtSum() {
		String q = "SELECT SUM(a." + fSum() + ") AS " + fSum() + " FROM "
				+ tblDT() + " a WHERE a." + fIdDoc() + " = " + ID_DOC + "";
		Cursor docCur = customQuery(q);
		docCur.moveToFirst();
		int index = docCur.getColumnIndex(fSum());
		double fSum = docCur.getFloat(index);
		return fSum;
	}

	public Cursor getOrder() {
		// String fieldSQL = "SELECT b." + fID() + " AS " + fID() + ", a."
		String fieldSQL = "SELECT a." + fID() + " AS " + fID() + ", a."
				+ fIdDoc() + " AS " + fIdDoc() + ", a." + fIdTov() + " AS "
				+ fIdTov() + ", a." + fIdEdIzmKr() + " AS " + fIdEdIzmKr()
				+ ", b." + fName() + " AS " + fName() + ", a." + fCount()
				+ " AS " + fCount() + ", a." + fKrat() + " AS " + fKrat()
				+ ", a." + fPrice() + " AS " + fPrice() + ", a." + fSum()
				+ " AS " + fSum() + " FROM " + tblDT() + " a";

		String joinSQL = " LEFT JOIN " + tblTov() + " b ON a." + fIdTov()
				+ " = b." + fID() + " WHERE a." + fIdDoc() + " = " + ID_DOC
				+ "";
		// + " ORDER BY " + fName() + "";
		if (this.IS_LOG == 1) {
			// Log.d(TAG + "_getOrder", fieldSQL + joinSQL);
		}

		return mDB.rawQuery(fieldSQL + joinSQL, null);
	}

	protected int doUpdate() {
		return mDB.update(tbl_name, cv, where, where_arg);
	}

	protected void doInsert() {
		mDB.insert(tbl_name, null, cv);
	}

	/**
	 * ������� ������ �� �������
	 * 
	 * @return - ���. ��������� �������
	 */
	protected int doDelete() {
		return mDB.delete(tbl_name, where, where_arg);
	}

	public Cursor doSelect() {
		return mDB.query(tbl_name, columns, where, where_arg, group_by, having,
				order_by);
	}
/*
	public Cursor getTov(String where, String order) {

		if (!order.equalsIgnoreCase("")) {
			order = " ORDER BY " + order + "";
		}
		Log.d("DB", "SELECT a." + fID() + ", a." + fName() + ", a." + fCount()
				+ ", a." + fIdEdIzm() + ", a." + definePrice() + " AS "
				+ fPrice() + " FROM " + tblTov() + " a WHERE a." + fIsGroup()
				+ " = " + where + "" + order + ""); */
		/* return mDB.query(tblTov(), null, "" + fIsGroup() + " = ?", new
		 * String[] { where }, null, null, null); */
/*
		return mDB.rawQuery("SELECT a." + fID() + ", a." + fName() + ", a."
				+ fCount() + ", a." + fIdEdIzm() + ", a." + definePrice()
				+ " AS " + fPrice() + " FROM " + tblTov() + " a WHERE a."
				+ fIsGroup() + " = " + where + order + "", null);

	}*/

	public Cursor getAllData(String tblname) {

		return mDB.rawQuery("select * from " + tblname, null);
		// return mDB.query(DB_TABLE, null, null, null, null, null, null);

	}

	protected void clearCV() {
		cv.clear();
	}

	protected void addCV(String key, String val) {
		cv.put(key, val);
	}

	protected void addCV(String key, int val) {
		cv.put(key, val);
	}

	protected void addCV(String key, double val) {
		cv.put(key, val);
	}

	protected int updateRow(String tbl) {

		return mDB.update(tbl, cv, "" + fID() + " = " + cv.get(fID()) + "",
				null);
	}

	protected void insertRow(String tbl) {
		mDB.insert(tbl, null, cv);
	}

	protected void insertDocHead(ContentValues conval) {
		// TODO Auto-generated method stub
		mDB.insert(TBL_DH, null, conval);
	}

	public void insertConst(String val) {
		int rowCount = mDB.update("tblConstant", cv, fConstant[1] + "=?",
				new String[] { val });

		if (rowCount == 0) {
			mDB.insert("tblConstant", null, cv);
		}
	}

	public void insertTovarKr(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fTovKr.length; i++) {
			cv.put(fTovKr[i], strArray[i]);
		}
		int rowCount = mDB.update(TBL_TOVARKR, cv, fTovKr[0] + "=? AND "
				+ fTovKr[1] + "=?", new String[] { strArray[0], strArray[1] });
		if (rowCount == 0) {
			mDB.insert(TBL_TOVARKR, null, cv);
		}
	}

	/**
	 * ��������� ������� tblPartnerPrice ������� �� strArray, ���� ����������
	 * ���� ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ���� ���������: 0, 1, 2, 3, 6 -
	 *            �����; 4, 5 - ����� � ����. ������;
	 */
	public void insertPrice(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fPartnerPrice.length; i++) {
			cv.put(fPartnerPrice[i], strArray[i]);
		}
		int rowCount = mDB.update(TBL_PPICE, cv, fPartnerPrice[0] + "=? AND "
				+ fPartnerPrice[1] + " =?", new String[] { strArray[0],
				strArray[1] });

		if (rowCount == 0) {
			mDB.insert(TBL_PPICE, null, cv);
		}
	}

	/**
	 * ��������� ������� tblDebitorka ������� �� strArray, ���� ���������� ����
	 * ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ���� ���������: 0, 3, 4 - �����; 1
	 *            - ����; 2 - ������; 5, 6 - ����� � ����. ������;
	 */
	public void insertDebitorka(String[] strArray) {
		cv.clear();
		String val = "";
		for (int i = 0; i < fDebetorka.length; i++) {
			val = strArray[i];
			// �������� ���� ���� ��������� � 1 �������, ������� ������������
			// � ��� yyyy-MM-dd HH:mm;ss
			if (i == 1) {
				try {
					val = Utils.convertDate(val, "M/d/yyyy",
							"yyyy-MM-dd HH:mm:ss");
					//Utils.ShowLog(TAG, val + " " + strArray[i]);
				} catch (ParseException e) {
					Utils.ShowLog(TAG,
							"�� ������� �������������� ����." + e.getMessage());
					e.printStackTrace();
				}
			}

			cv.put(fDebetorka[i], val);
		}
		int rowCount = mDB.update(TBL_DEBITORKA, cv, fDebetorka[0]
				+ " = ? AND " + fDebetorka[2] + " = ?", new String[] {
				strArray[0], strArray[2] });
		if (rowCount == 0) {
			mDB.insert(TBL_DEBITORKA, null, cv);

		}
	}

	/**
	 * ��������� ������� tblTovarSetka ������� �� strArray, ���� ���������� ����
	 * ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ������� ���������: 0 - �����; 1 -
	 *            ������; 2 - ����� � ����. ������; 3 - �����;
	 */
	public void insertSetka(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fTovSetka.length; i++) {
			cv.put(fTovSetka[i], strArray[i]);
		}
		int rowCount = mDB.update(TBL_SETKA, cv, fTovSetka[0] + "=?",
				new String[] { strArray[0] });
		if (rowCount == 0) {
			mDB.insert(TBL_SETKA, null, cv);
		}
	}

	/**
	 * ��������� ������� tblEdIzmKr ������� �� strArray, ���� ���������� ����
	 * ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ������� ���������: 0 - �����; 1 -
	 *            ������; 2, 3 - �����;
	 */
	public void insertEdmKr(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fEdIzmKr.length; i++) {
			cv.put(fEdIzmKr[i], strArray[i]);
		}
		int rowCount = mDB.update(TBL_EDMKR, cv, fEdIzmKr[0] + "=?",
				new String[] { strArray[0] });
		if (rowCount == 0) {
			mDB.insert(TBL_EDMKR, null, cv);
		}
	}

	/**
	 * ��������� ������� tblEdIzm ������� �� strArray, ���� ���������� ����
	 * ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ���� ���������: 0 - �����; 1 -
	 *            ������;
	 */
	public void insertEdm(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fEdIzm.length; i++) {
			cv.put(fEdIzm[i], strArray[i]);
		}
		int rowCount = mDB.update(TBL_EDM, cv, fEdIzm[0] + "=?",
				new String[] { strArray[0] });

		if (rowCount == 0) {
			mDB.insert(TBL_EDM, null, cv);
		}
	}

	/**
	 * ��������� ������� tblMag ������� �� strArray, ���� ���������� ���� �����
	 * ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ���� ���������: 0 - �����; 1 -
	 *            ������; 2 - �����, 3, 4 - ����� � ����. ������
	 */
	public void insertMag(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fMag.length; i++) {
			cv.put(fMag[i], strArray[i]);
		}
		int rowCount = mDB.update(TBL_MAG, cv, fMag[0] + "=?",
				new String[] { strArray[0] });

		if (rowCount == 0) {
			mDB.insert(TBL_MAG, null, cv);
		}
	}

	/**
	 * ��������� ������� tblCounteragent ������� �� strArray, ���� ����������
	 * ���� ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� ����� ���������: 0 - �����; 1 -
	 *            ������; 2, 3, 4, 5 - ����� � ����. ������
	 */
	public void insertClient(String[] strArray) {
		cv.clear();
		for (int i = 0; i < fCounteragent.length; i++) {
			// String val = strArray[i];
			// if (i == 1) {
			// val = val.toUpperCase();
			// }
			//Utils.ShowLog("CLIENT", "" + fCounteragent[i] + " fCounteragent: " + fCounteragent.length + " strArray: " + strArray.length);
			
			if (i == 1) {
				cv.put(fCounteragent[i], strArray[i].toUpperCase());
			} else {
				cv.put(fCounteragent[i], strArray[i]);
			}

		}
		int rowCount = mDB.update(TBL_CLIENT, cv, fCounteragent[0] + "=?",
				new String[] { strArray[0] });
		if (rowCount == 0) {
			mDB.insert(TBL_CLIENT, null, cv);
		}
	}

	/**
	 * ��������� ������� tblTovar ������� �� strArray, ���� ���������� ����
	 * ����� ����� �������� ����� ������.
	 * 
	 * @param strArray - ��������� ������ �� 17-�� ���������: 0, 6, 7, 12, 13 -
	 *            �����; 2, 8, 9 - ������; 1, 3, 4, 5, 10, 11, 14, 15, 15 -
	 *            ����� � ����. ������
	 */
	public void insertRow(String[] strArray) {
		cv.clear();
		// Log.d("INSERT_ROM", " ----------------- ------------------ ");
		for (int i = 0; i < fTovar.length; i++) {
			String val = strArray[i];
			if (i == 4) {

				val = val.toUpperCase();

			}
			if (i == 0) {
				// Log.d("INSERT_ROM", "" + fTovar[i] + " " + val + "");
				if (Integer.parseInt(val) == 1) {
					val = "G";
				} else {
					val = " ";
				}
			}
			if (i == 5) {
				if (Integer.parseInt(val) == 1) {
					val = "X";
				} else {
					val = " ";
				}
			}
			cv.put(fTovar[i], val);
		}
		int rowCount = mDB.update(TBL_TOVAR, cv, fTovar[3] + "=?",
				new String[] { strArray[3] });
		if (rowCount == 0) {
			mDB.insert(TBL_TOVAR, null, cv);
		}
	}

	public void addRecord(String name, String unit, Float cost, Float quantity) {
		// ContentValues cv = new ContentValues();
		// cv.clear();
		cv.put(TOV_NAME, name);
		cv.put(TOV_KEYEDIZM, unit);
		cv.put(TOV_PRICE, cost);
		cv.put(TOV_QUANTITY, quantity);

		mDB.insert(TBL_TOVAR, null, cv);
	}

	public void addRecordPartner(String table_name, ContentValues cv) {
		// ContentValues cv = new ContentValues();
		// cv.clear();
		mDB.insert(table_name, null, cv);
	}

	public void deleteAll(long id) {

		mDB.delete(TBL_TOVAR, TOV_ID + " = " + id, null);

	}

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		protected void insertConst(SQLiteDatabase db, String val_1,
				String val_2, String val_3) {
			cv = new ContentValues();
			cv.clear();
			// cv.put(fConstant[0], val_1);
			cv.put(fConstant[1], val_1);
			cv.put(fConstant[2], val_2);
			cv.put(fConstant[3], val_3);
			int rowCount = db.update("tblConstant", cv, fConstant[1] + "=?",
					new String[] { val_1 });

			if (rowCount == 0) {
				db.insert("tblConstant", null, cv);
			}

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub

			String[] str_sql = mCtx.getResources().getStringArray(
					R.array.tables);

			for (String ddl_txt : str_sql) {
				db.execSQL(ddl_txt);
			}

			insertConst(db, "torgID", "ID представителя", "0");
			insertConst(db, "port", "Порт хоста", "3000");
			insertConst(db, "host", "Хост", "q1.edel.donbass.info");
			insertConst(db, "resources", "Ресурс хоста", "/subhost/pa.txt");
			insertConst(db, "pass", "Пароль администратора", "9876");
			insertConst(db, "sendID", "ID отправки", "0");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion != newVersion) {
				Toast.makeText(mCtx, "База обновлена с " + oldVersion + " --> " + newVersion, Toast.LENGTH_LONG).show();
				db.execSQL("DROP TABLE IF EXISTS " + TBL_DT);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_DH);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_CLIENT);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_EDM);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_EDMKR);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_MAG);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_SETKA);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_TOVAR);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_TOVARKR);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_DEBITORKA);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_PPICE);
				db.execSQL("DROP TABLE IF EXISTS " + TBL_CONSTANT);
				onCreate(db);
			}

		}

	}
}
