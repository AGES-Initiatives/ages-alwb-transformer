package net.ages.liturgical.workbench.transformer.utils;

public class RunToCopyFiles {

	public static void main(String[] args) {
		String fileExtension = "ares";
		String fromDir = "/Users/mac002/git/ages-alwb-library-en-uk-lash/net.ages.liturgical.workbench.library_en_UK_lash";
		String toDir = "/Users/mac002/Documents/workspaces/mars/kenya/megalo/libraries";
		AlwbFileUtils.copyFiles(fromDir, toDir, fileExtension);
	}
}
