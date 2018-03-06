package net.ages.liturgical.workbench.transformer.git;

import org.ocmc.pols.git.JGitUtils;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

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
		String configPath = "/Transformer.config";
		PropertyUtils props = new PropertyUtils(configPath);
		boolean debug = props.getPropBoolean("debugGit");
		String alwbPath = GeneralUtils.getParentPath(props.getPropString("pathToParentGitDirectory"));
		JGitUtils.resetAllGitRepos(alwbPath, debug);
	}
}
