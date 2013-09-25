import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

/**
 * ClassName:ExcelMain Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-10-6
 */
public class ExcelMain {
	public static void main(String[] args) throws BiffException, IOException, SQLException, ClassNotFoundException {

		File typeFile = new File("C:\\Users\\Wendell\\Desktop\\type.txt");
		Workbook rwb = Workbook.getWorkbook(new File("C:\\Users\\Wendell\\Desktop\\ecboss_salesorder_shipping.xls"));
		Sheet rs = rwb.getSheet(0);
		int size = rs.getRows();
		Scanner cin = new Scanner(typeFile);
		List<String> typeList = new ArrayList<String>();
		while (cin.hasNext()) {
			String type = cin.nextLine().trim();
			if (StringUtils.isBlank(type))
				break;
			if (type.equals("float"))
				type = "int";
			typeList.add(type);
		}

		String[] type = typeList.toArray(new String[0]);

		System.out.println(type.length);
		File file = new File("C:\\Users\\Wendell\\Desktop\\sql.sql");

		FileWriter writer = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		Cell[] titleCells = rs.getRow(0);
		System.out.println(titleCells.length);
		for (int i = 1; i < size; i++) {
			boolean[] flag = new boolean[rs.getColumns()];
			Cell[] cells = rs.getRow(i);
			for (int index = 0; index < cells.length; index++) {
				Cell cell = cells[index];
				if (StringUtils.isNotBlank(cell.getContents())) {
					flag[index] = true;
				}
			}
			String sql = "INSERT INTO ecboss_salesorder_shipping (";
			boolean isOr = false;
			for (int index = 0; index < rs.getColumns(); index++) {
				if (flag[index]) {
					if (isOr)
						sql += ",";
					sql += titleCells[index].getContents();
					isOr = true;
				}
			}
			sql += ") VALUES (";
			isOr = false;
			for (int index = 0; index < rs.getColumns(); index++) {
				if (flag[index]) {
					if (isOr)
						sql += ",";
					if (type[index].equals("int"))
						sql += cells[index].getContents();
					else if (type[index].equals("varchar")) {
						sql += "'" + cells[index].getContents() + "'";
					} else if (type[index].equals("timestamp")) {
						sql += "NOW()";
					}
					isOr = true;
				}
			}
			sql += ");";
			bufferedWriter.write(sql);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		}
		writer.close();

	}
}
