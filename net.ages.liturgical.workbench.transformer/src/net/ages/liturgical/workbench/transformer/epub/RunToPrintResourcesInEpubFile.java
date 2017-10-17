package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class RunToPrintResourcesInEpubFile {

	public static void main(String[] args) {
	try {
		File f = new File("/Users/mac002/Git/ages-alwb-templates/net.ages.liturgical.workbench.templates/src-gen/website/test/dcs/e/m/s/2016/03/12/se.2016.03.12.epub");
		EpubReader reader = new EpubReader();
		Book book = reader.readEpub(new FileInputStream(f.getPath()));
		Collection<Resource> col = book.getResources().getAll();
		for (Resource r : col) {
			System.out.println(r.getId() + " " +  r.getHref());
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	}

}
