package net.ages.liturgical.workbench.transformer.text.to.latex;

import java.io.File;
import java.util.List;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;

public class LatexTransformer {

	public static void main(String[] args) {
		String in = "/Users/mac002/Git/mcolburn/kenya/transformer/in/fixActor";
		String out = "/Users/mac002/Git/mcolburn/kenya/transformer/out/fixActor";
		File pathOut = new File(out);
		pathOut.mkdirs();
		List<File> files = AlwbFileUtils.getFilesInDirectory(in, "tex");
		for (File f : files) {
			System.out.println(f.getName());
			List<String> lines = AlwbFileUtils.linesFromFile(f);
			StringBuffer sb = new StringBuffer();
			for (String line : lines) {
				sb.append(line);
				if (line.contains("Actor")) {
					// ignore
				} else {
					sb.append("\n");
				}
			}
			if (f.getName().contains("childbirth")) {
				System.out.println("hi");
			}
			AlwbFileUtils.writeFile(out + "/" + f.getName(), sb.toString());
		}
	}

}
