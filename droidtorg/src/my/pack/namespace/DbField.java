package my.pack.namespace;

public class DbField {
	public final static String tblConstant = "tblConstant";
	public final static String tblDebitorka = "tblDebitorka";
	public final static String tblPartner = "tblCounteragent";
	public final static String tblTovar = "tblTovar";
	public final static String tblMag = "tblMag";
	public final static String tblPartnerPrice = "tblPartnerPrice";
	public final static String tblDocHead = "tblDocHead";
	public final static String tblDocTable = "tblDocTable";
	public final static String tblTovKr = "tblTovKr";
	public final static String tblTovarSetka = "tblTovarSetka";
	public final static String tblEdIzm = "tblEdIzm";
	public final static String tblEdIzmKr = "tblEdIzmKr";

	
	
	
	// общие поля
	public final static String _id = "_id";
	public final static String fName = "fName";
	public final static String fkTovar = "fk_tovar";
	public final static String fkPartner = "fk_partner";
	public final static String fkMagazin = "fk_magazin";
	public final static String fCount = "fCount";
	public final static String fPrice = "fPrice";

	// tblCounteragent
	public final static String fTypePrice = "fTypePrice";
	public final static String fRestThem = "fRestThem";

	// tblTovar
	public final static String fCodeParent = "fCodeParent";

	// tblConstant
	public final static String fProgID = "fProgID";
	public final static String fValue = "fValue";

	// tblDebitorka
	public final static String fDDoc = "fDDoc";
	public final static String fVDoc = "fVDoc";
	public final static String fPrihod = "fPrihod";
	public final static String fRashod = "fRashod";

	// tblPartnerPrice
	public final static String fParcent = "fParcent";
	public final static String fHow = "fHow";
	public final static String fBase = "fBase";

	// tblDocHead
	public final static String fKeyPartner = "fKeyPartner";
	public final static String fDate = "fDate";
	public final static String fOut = "fOut";

	// tblDocTable
	public final static String fKeyZ = "fKeyZ";

	public final static String startQuery = "<query>"; // начальный тег запроса
	public final static String endQuery = "</query>";// конечный тег запроса
	public final static String startData = "<data>"; // начальный тег данных
	public final static String endData = "</data>";// конечный тег данных
	public final static String query4 = "getrest"; // остаток по id ТМЦ

}
