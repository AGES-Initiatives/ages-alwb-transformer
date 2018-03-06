package net.ages.liturgical.workbench.transformer;

import java.io.File;
import java.util.List;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;

public class ListFiles {

	public static void listFiles(String prefix, String suffix, String path, String extension) {
		List<File> files = AlwbFileUtils.getFilesInDirectory(path, extension);
		for (File f : files) {
			System.out.println(prefix + f.getName() + suffix);
		}
	}
	public static void main(String[] args) {
	}

}
