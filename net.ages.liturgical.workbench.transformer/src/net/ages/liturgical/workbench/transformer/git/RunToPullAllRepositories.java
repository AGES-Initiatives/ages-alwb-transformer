package net.ages.liturgical.workbench.transformer.git;

import org.ocmc.pols.git.JGitUtils;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

/**
 * Run this to pull all repositories from Github.
 * 
 * @author mac002
 *
 */
public class RunToPullAllRepositories {

	/**
	 * Do not be alarmed by red letters about: SLF4J: Class path contains multiple SLF4J bindings.
	 * @param args
	 */
	public static void main(String[] args) {
		String configPath = "/Transformer.config";
		PropertyUtils props = new PropertyUtils(configPath);
		String alwbPath = GeneralUtils.getParentPath(props.getPropString("pathToParentGitDirectory"));
		JGitUtils.pullAllGitRepos(alwbPath);
	}
}
