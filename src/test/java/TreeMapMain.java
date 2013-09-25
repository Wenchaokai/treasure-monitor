import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * ClassName:TreeMapMain Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-14
 */
public class TreeMapMain {
	public static void main(String[] args) {
		TreeMap<Double, String> tt = new TreeMap<Double, String>(Collections.reverseOrder());
		tt.put(Double.parseDouble("100"), "100");
		tt.put(Double.parseDouble("101"), "101");
		tt.put(Double.parseDouble("99"), "99");
		tt.put(Double.parseDouble("102"), "102");
		for (Entry<Double, String> entry : tt.entrySet()) {
			System.out.println(entry.getValue());
		}
	}
}
