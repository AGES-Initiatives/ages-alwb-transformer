package net.ages.liturgical.workbench.transformer.git;

import org.ocmc.pols.git.JGitUtils;

/**
 * Run this to push all local repositories to Github.
 * 
 * @author mac002
 *
 */
public class RunToPushAllRepositories {

	/**
	 * Do not be alarmed by red letters about: SLF4J: Class path contains multiple SLF4J bindings.
	 * @param args
	 */
	public static void main(String[] args) {
		// set the path to the folder under which all your repositories resides
       // Windows uses backslashes, e.g. C:\myGitDirectory.
		// Do not use a path with spaces in the names.
		String alwbPath = "/Users/mac002/Git/mcolburn/synch-test"; 
		String username = System.getenv("username");
		String pwd = System.getenv("pwd");
		String message = "";  // this message will apply to all commits in all repositories.
		JGitUtils.pushAllGitRepos(
				alwbPath
				, username
				, pwd
				, message);
	}
}
