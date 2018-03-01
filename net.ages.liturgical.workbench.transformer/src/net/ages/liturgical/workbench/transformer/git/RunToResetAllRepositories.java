package net.ages.liturgical.workbench.transformer.git;

import org.ocmc.pols.git.JGitUtils;

/**
 * Run this when you want to blow away all your changes and reset the
 * project libraries and templates to be exactly the way they are in Github.
 * 
 * @author mac002
 *
 */
public class RunToResetAllRepositories {

	/**
	 * Do not be alarmed by red letters about: SLF4J: Class path contains multiple SLF4J bindings.
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Set the path to the folder under which all your repositories reside.
		 * Remember that Windows uses backslashes.
		 * You have to use a double back slash when you set the alwbPath,
		 * e.g. c:\\git
		 * Do not use spaces in the path names.
		 */
		String alwbPath = "/Users/mac002/Git/alwb-repositories/ages"; 
		JGitUtils.resetAllGitRepos(alwbPath);
	}
}
