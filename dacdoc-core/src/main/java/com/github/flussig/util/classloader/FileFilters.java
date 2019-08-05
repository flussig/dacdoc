package com.github.flussig.util.classloader;

public class FileFilters {

	public static final JavaClassFileFilter JAVA_CLASS_FILE_FILTER = new JavaClassFileFilter();
	public static final MatchAllFileFilter ALL_FILES_FILTER = new MatchAllFileFilter();
	
	private FileFilters() {
	}
	
	
}
