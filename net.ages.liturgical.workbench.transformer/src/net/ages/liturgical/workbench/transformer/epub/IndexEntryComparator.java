package net.ages.liturgical.workbench.transformer.epub;

import java.text.Normalizer;
import java.util.Comparator;

public class IndexEntryComparator  implements Comparator<IndexEntry>{
	public int compare(IndexEntry e1, IndexEntry e2) {
		String left = Normalizer.normalize(e1.getKey().toLowerCase(), Normalizer.Form.NFD);
		left = left.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		String right = Normalizer.normalize(e2.getKey().toLowerCase(), Normalizer.Form.NFD);
		right = right.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return left.compareTo(right);
    }
}
