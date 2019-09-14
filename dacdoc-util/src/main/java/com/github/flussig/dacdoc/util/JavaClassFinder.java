package com.github.flussig.dacdoc.util;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Utility to walk the Java classpath, and to find all classes which are assignable (i.e. inherit) 
 * a specified class. If no matching class is specified, will return all classes in the classpath
 *
 * Code was largely taken from a solution for this question https://stackoverflow.com/questions/9991253/finding-all-classes-implementing-a-specific-interface/9991343
 * Solution was proposed by user Sam Goldberg.
 *
 * The code was significantly changed and simplified to allow for finding all loaded classes of given type in a given classpath
 */
public class JavaClassFinder  {
	private static final String CLASS_FILE_EXT = ".class";

	private ClassLoader classLoader;

	public JavaClassFinder(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Finds all classes which are Assignable from the specified class
	 * @return List of class objects
	 */
	@SuppressWarnings("unchecked")
	public <T> List<Class<? extends T>> findAllMatchingTypes(String classpath, Class<T> toFind) {
		ArrayList<Class<?>> foundClasses = new ArrayList<>();
		List<Class<? extends T>> returnedClasses = new ArrayList<>();
		walkClassPath(classpath, toFind, foundClasses);
		for (Class<?> clazz : foundClasses) {
			returnedClasses.add((Class<? extends T>) clazz);
		}
		return returnedClasses;
	}

	private <T> void walkClassPath(String classpath, Class<T> toFind, ArrayList<Class<?>> foundClasses) {
		try {
			File rootDir = new File(classpath);
			walk(rootDir, classpath, toFind, foundClasses);
		} catch (Exception e) {
			// if any sort of error occurs due to bad file path, or other issues, just catch and swallow
			// because we don't expect any Exceptions in normal course of usage
			e.printStackTrace();
		}
	}

	private <T> void walk(File currentDir,  String classpath, Class<T> toFind, ArrayList<Class<?>> foundClasses) {
		File[] files = currentDir.listFiles();

		if(files == null || files.length == 0) {
			return;
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				walk(file, classpath, toFind, foundClasses);
			} else {
				if (file.getName().endsWith(CLASS_FILE_EXT)) {
					Class<?> clazz = convertToClass(file, classpath);
					if (clazz != null && toFind.isAssignableFrom(clazz)) {
						foundClasses.add(clazz);
					}
				}
			}
		}
	}

	private Class convertToClass(File classFile, String classpath) {
		Class classInstance = null;
		if (classFile.getAbsolutePath().startsWith(classpath) && classFile.getAbsolutePath().endsWith(CLASS_FILE_EXT)) {
			classInstance = getClassFromName(classFile.getAbsolutePath(), classpath);
		}
		return classInstance;
	}

	private Class getClassFromName(String fileName, String classpath) {
		try {
			String className = removeClassPathBase(fileName, classpath);
			className = Files.removeExtension(className);
			return classLoader.loadClass(className);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String removeClassPathBase(String fileName, String classpath) {
		String classPart = fileName.substring(classpath.length() + 1);
		String className = classPart.replace(File.separatorChar, '.');
		return className;
	}
}

