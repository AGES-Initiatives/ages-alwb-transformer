package net.ages.liturgical.workbench.transformer.utils;

public class RegExTest {

	public static void main(String[] args) {
		String filename = "bk.baptism.atem";
		System.out.println(filename.matches("bk.*.atem"));
	}

}
