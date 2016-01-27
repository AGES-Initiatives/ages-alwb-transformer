package net.ages.liturgical.workbench.transformer.text.to.alwb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ages.alwb.utils.core.error.handling.ErrorUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

/**
 * Run this to create ares and atem files from text files.
 * Settings for this program are in Transformer.config.
 * Because this program analyzes all the files in the directory
 * pointed to by text.to.alwb.path.in, you should have all your text
 * files converted to ares and atem files before you start changing
 * the format tags in the atem files.  But, it is OK to add additional
 * files later.
 * @author mac002
 *
 */
public class RunToConvertTextToAlwb {
	final static Logger logger = LoggerFactory.getLogger(RunToConvertTextToAlwb.class);

	public static void main(String[] args) {	
		try {
			String ePubConfig = "/Transformer.config";
			PropertyUtils props = new PropertyUtils(ePubConfig);
			String pathIn = props.getPropString("text.to.alwb.path.in");
			String pathOut = props.getPropString("text.to.alwb.path.out");
			String actors = props.getPropString("text.to.alwb.actors");
			String actorDelimiter = props.getPropString("text.to.alwb.actor.delimiter");
			String domain = props.getPropString("text.to.alwb.domain");
			String atemTagOpen = props.getPropString("text.to.alwb.default.atem.tag.open");
			String atemTagClose = props.getPropString("text.to.alwb.default.atem.tag.close");
			
			TextToAlwb tta = new TextToAlwb(
					actors
					, actorDelimiter
					, pathIn
					, pathOut
					, domain
					, atemTagOpen
					, atemTagClose
					);
			tta.process();
			System.out.println("Done...Look in " + pathOut);
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
			System.out.println("Did you forget to assign values in the transformer.config file for TextToAlwb?");
		}
	}
		
}
