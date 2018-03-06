package net.ages.liturgical.workbench.transformer.text.to.alwb;

import java.io.File;
import java.util.List;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;

public class GreekKeys {
	
	private static int counter;
	
	public static String keysFromLine(String line) {
		StringBuffer result = new StringBuffer();
		counter++;
		String key = "p" + pad(counter);
		try {
			String[] parts = line.split("\\(from ");
			if (parts.length == 2) {
				result.append("\n" + key + ": 1 key: " + line);
			} else if (parts.length == 3) {
				result.append("\n" + key + ": 2 keys: "  + line);
			} else if (parts.length == 4) {
				result.append("\n" + key + ": 3 keys: "  + line);
			} else {
				result.append("\n" + key + ": ? keys: "  + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	public static String pad(int i) {
		return String.format("%04d", i);
	}


	public static void main(String[] args) {
		String in = "/Users/mac002/git/ages-alwb-transformer/net.ages.liturgical.workbench.transformer/data/in/greekwithkeys.txt";
		String out = "/Users/mac002/git/ages-alwb-transformer/net.ages.liturgical.workbench.transformer/data/out/liturgystjohn.txt";
		counter = 0;
		List<String> lines = AlwbFileUtils.linesFromFile(new File(in));
		StringBuffer result = new StringBuffer();
		for (String line : lines) {
			if (line.trim().length() > 0) {
				result.append(keysFromLine(line));
			}
		}
		AlwbFileUtils.writeFile(out, result.toString());
	}

}
