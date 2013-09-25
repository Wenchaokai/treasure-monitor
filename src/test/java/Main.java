import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * ClassName:Main Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-4
 */
public class Main {
	public static void main(String[] args) throws BiffException, IOException {
		Workbook rwb = Workbook.getWorkbook(new File("D:/工作/数据库/data-sample/data-sample/asn.xls"));
		Sheet rs = rwb.getSheet(0);
		System.out.println(rs.getColumns());
	}
}
