package net.ages.liturgical.workbench.transformer.epub;

import java.util.Comparator;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

/**
 * Compares after making lowercase and removing diacritics.
 * @author mac002
 *
 */
public class IndexEntryComparator  implements Comparator<IndexEntry>{
	public int compare(IndexEntry e1, IndexEntry e2) {
		String left = GeneralUtils.normalize(e1.getKey());
		String right = GeneralUtils.normalize(e2.getKey());
        return left.compareTo(right);
    }
}
