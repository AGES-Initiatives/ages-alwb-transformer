package net.ages.liturgical.workbench.transformer.alwb.html;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.ages.liturgical.workbench.transformer.alwb.html.models.Cell;
import net.ages.liturgical.workbench.transformer.alwb.html.models.CellElement;
import net.ages.liturgical.workbench.transformer.alwb.html.models.Row;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

public class AlwbHtmlInfoExtractor {

	private String html;
	private Map<Integer, Row> rowMap = new TreeMap<Integer,Row>();
	private List<String> classNames = new ArrayList<String>();
	private List<Row> mismatch = new ArrayList<Row>();
	
	public AlwbHtmlInfoExtractor(String html) {
		this.html = html;
		process();
	}
	
	
	private Cell processCellElements(int row, boolean left, Elements elements) {
		Cell result = new Cell();
		List<CellElement> elementList = result.getList();
		Iterator<Element> it = elements.iterator();
		while (it.hasNext()) {
			Element e = it.next();
			Elements keyElements = e.getElementsByAttribute("data-key");
			for (Element keyElement : keyElements) {
				Element parent = keyElement.parent();
				String tag = parent.tagName();
				String key = keyElement.attr("data-key");
				String className = parent.className();
				if (className.matches("leftCell") || className.matches("rightCell")) {
					// ignore
				} else {
					classNames.add(className.trim());
					String text = parent.ownText();
					elementList.add(new CellElement(row, tag, className, text, key));
				}
			}
		}
		result.setList(elementList);
		return result;
	}
	
	public List<String> getClassNames() {
		return classNames;
	}
	
	public List<Cell> getLeftCells() {
		List<Cell> result = new ArrayList<Cell>();
		for (Row row : rowMap.values()) {
			result.add(row.getLeft());
		}
		return result;
	}
	
	public int rowCount() {
		return rowMap.size();
	}
	
	public List<Cell> getRightCells() {
		List<Cell> result = new ArrayList<Cell>();
		for (Row row : rowMap.values()) {
			result.add(row.getRight());
		}
		return result;
	}

	private void process() {
		String left = "leftCell";
		String right = "rightCell";
		Document doc = Jsoup.parse(html);
		Elements rows = doc.getElementsByTag("tr");
		ListIterator<Element> it = rows.listIterator();
		int rowCount = 0;
		while (it.hasNext()) {
			Element row = it.next();
			rowCount++;
			Cell leftCell = processCellElements(rowCount, true, row.getElementsByClass(left));
			Cell rightCell = processCellElements(rowCount, false, row.getElementsByClass(right));
			if (leftCell.getList().size() != rightCell.getList().size()) {
				mismatch.add(new Row(rowCount,leftCell,rightCell));
			}
			rowMap.put(rowCount, new Row(rowCount, leftCell, rightCell));
		}
	}

	public List<Row> getMismatches() {
		return mismatch;
	}
	public static void main(String[] args) {
		String base = "/Users/mac002/git/ages-alwb-transformer/net.ages.liturgical.workbench.transformer/data/";
		String in = base + "in/dl.gr-en.html";
		String out = base + "/out/liturgystjohnGrk.";
		AlwbHtmlInfoExtractor extractor = new AlwbHtmlInfoExtractor(
				AlwbFileUtils
				.getFileAsString(new File(in)));
		List<Cell> cells = extractor.getLeftCells();
		int counter = 0;
		StringBuffer sb = new StringBuffer();
		for (Cell cell : cells) {
			List<CellElement> elements = cell.getList();
			for (CellElement e : elements) {
				sb.append(e.getText() + "\n");
			}
		}
		AlwbFileUtils.writeFile(out+"txt", sb.toString());
		
		List<Row> mismatches = extractor.getMismatches();
		sb = new StringBuffer();
		for (Row r : mismatches) {
			Cell leftCell = r.getLeft();
			Cell rightCell = r.getRight();
			sb.append("===================\n");
			for (CellElement cell : leftCell.getList()) {
				sb.append(cell.getClassName() + " - " + cell.getKey() + ": " + cell.getText() +"\n");
			}
			sb.append("\n");
			for (CellElement cell : rightCell.getList()) {
				sb.append((cell.getClassName() + " - " + cell.getKey() + ": " + cell.getText() + "\n"));
			}

		}
		AlwbFileUtils.writeFile(out+"mismatched.txt", sb.toString());
	}

}
