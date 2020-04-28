package net.ages.liturgical.workbench.transformer.git;

import org.ocmc.pols.git.JGitUtils;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

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
		String configPath = "/Transformer.config";
		PropertyUtils props = new PropertyUtils(configPath);
		boolean debug = props.getPropBoolean("debugGit");
		String alwbPath = GeneralUtils.getParentPath(props.getPropString("pathToParentGitDirectory"));
		String username = System.getenv("username");
		String pwd = System.getenv("pwd");
		String message = "";  // this message will apply to all commits in all repositories.
		JGitUtils.pushAllGitRepos(
				alwbPath
				, username
				, pwd
				, message
				, debug
				);
	}
}
