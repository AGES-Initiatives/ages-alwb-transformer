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

import net.ages.liturgical.workbench.transformer.alwb.html.models.Atem;
import net.ages.liturgical.workbench.transformer.alwb.html.models.Cell;
import net.ages.liturgical.workbench.transformer.alwb.html.models.CellElement;
import net.ages.liturgical.workbench.transformer.alwb.html.models.ClassToAtem;
import net.ages.liturgical.workbench.transformer.alwb.html.models.Row;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

public class RunToTransformAgesHtmlToLatex {

	private String html;
	private Map<Integer, Row> rowMap = new TreeMap<Integer,Row>();
	private List<String> classNames = new ArrayList<String>();
	private List<Row> mismatch = new ArrayList<Row>();
	private static RunToTransformAgesHtmlToLatex extractor;
	
	public RunToTransformAgesHtmlToLatex(String html) {
		this.html = html;
		process();
	}
	
	public String toAresFileContents(String resourceName) {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}
	
	public String toAtemFileContents(
			String templateName
			, String domain
			, String title
			) {
		ClassToAtem cta = new ClassToAtem();
		StringBuffer contents = new StringBuffer();
		contents.append("Template " + templateName +  "\n\n");
		
		// set imports
		contents.append("Status Draft\n\n");
//		contents.append("\timport duplicates_gr_GR_cog.*\n");
		contents.append("\timport " + templateName + ".*\n\n");
		contents.append("\timport iTags.*\n");
		contents.append("\timport bTags.*\n");
		contents.append("\timport roles.*\n");
		
		// set header
		contents.append("\n\tHead\n");
		contents.append("\t\tPage_Header_Even\n");
		contents.append("\t\t\tcenter @text \"" + title + "\"\n");
		contents.append("\t\tEnd_Page_Header_Even\n");
		contents.append("\t\tPage_Header_Odd\n");
		contents.append("\t\t\tcenter @text \"" + title + "\"\n");
		contents.append("\t\tEnd_Page_Header_Odd\n");
		contents.append("\t\tPage_Footer_Even\n");
		contents.append("\t\tleft @text \"PRIEST'S SERVICE BOOK\"\n");
		contents.append("\t\tright @pageNbr\n");
		contents.append("\t\tEnd_Page_Footer_Even\n");
		contents.append("\t\tPage_Footer_Odd\n");
		contents.append("\t\tleft @pageNbr\n");
		contents.append("\t\tright @text \"PRIEST'S SERVICE BOOK\"\n");
		contents.append("\t\tEnd_Page_Footer_Odd\n");
		contents.append("\t\tSet_Page_Number 1 End_Set_Page_Number\n");
		contents.append("\tEnd_Head\n\n");
		for (Row row : rowMap.values()) {
			Cell left = row.getLeft();
			List<CellElement> list = left.getList();
			if (list == null || list.size() == 0) {
				System.out.println("Oh, no!!!!");
			} else {
				String firstClassName = list.get(0).getClassName();
				Atem firstAtem = cta.get(firstClassName);
				for (CellElement element : left.getList()) {
					Atem atem = cta.get(element.getClassName());
					if (atem == null) {
						System.out.println("can't find " + element.getTag() + " " + element.getClassName());
					} else {
						if (element.tagIsForOuter()) {
							contents.append(
							" sid " 
							+ element.getKey().replace("|", ".")
							);
						} else {
							contents.append(
									" " 
							+ atem.getPrefix() 
							+ " sid " 
							+ element.getKey().replace("|", ".")
							+ " "
							+ atem.getSuffix()
							);
						}
					}
				}
				contents.append(" ");
				if (firstAtem != null) {
					contents.append(firstAtem.getSuffix());
				}
				contents.append("\n");
			}
		}
		contents.append("End-Template");
		return contents.toString();
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
				if (key.contains("li.rubric.rank1")) {
					System.out.println("");
				}
				String className = parent.className();
				if (className.matches("leftCell") || className.matches("rightCell")) {
					// ignore
				} else {
					classNames.add(className.trim());
					String text = parent.ownText();
					Element previousSibling = keyElement.previousElementSibling();
					if (previousSibling == null) {
						elementList.add(new CellElement(row, tag, className, text, key, false));
					} else {
						Element previousSiblingParent = previousSibling.parent();
						if (previousSiblingParent == parent) {
							elementList.add(new CellElement(row, tag, className, text, key, true));
						} else {
							elementList.add(new CellElement(row, tag, className, text, key, false));
						}
					}
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

	
	public static void writeData(
			String out
			, String lang
			, List<Cell> cells
			) {
		int counter = 0;
		StringBuffer sb = new StringBuffer();
		for (Cell cell : cells) {
			List<CellElement> elements = cell.getList();
			for (CellElement e : elements) {
				sb.append(e.getText() + "\n");
			}
		}
		AlwbFileUtils.writeFile(out+"_"+ lang +".txt", sb.toString());
		
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
		AlwbFileUtils.writeFile(
				out+"bk.liturgy.chrysostom.lash.oak.atem"
				, extractor.toAtemFileContents(
						"bk.liturgy.chrysostom.lash.oak"
						,"gr_GR_cog"
						, "Divine Liturgy of St. John Chrysostomos"
						)
			);

	}
	
	public static void main(String[] args) {
		String base = "/Users/mac002/Git/ages-alwb-transformer/net.ages.liturgical.workbench.transformer/data";
//		String in = "/Users/mac002/Git/alwb-repositories/kenya/oak-alwb-templates-oak/net.ages.liturgical.workbench.templates.oak/src-gen/website/test/dcs/h/b/liturgy/chrysostom/lash/oak/en/index.html";
		String in = base + "/in/dl.gr-en.html";
		String out = base + "/out/liturgystjohnEng.";
		extractor = new RunToTransformAgesHtmlToLatex(
				AlwbFileUtils
				.getFileAsString(new File(in)));
		List<Cell> cells = extractor.getLeftCells();
		writeData(out,"gr", cells);
		cells = extractor.getRightCells();
		writeData(out,"eng",cells);
	}

}
