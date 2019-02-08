package com.poderosa.fileextn.changer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class FileExtnChanger {
	
	static File readingDir;
	static String writtingDir;
	static String choice;

	@SuppressWarnings("unchecked")
	static void displayDirectoryContents() {
		System.out.println("Inside Convert Block");
		try {
            boolean recursive = true;
			Collection<File> files = FileUtils.listFiles(readingDir,null, recursive);
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				System.out.println(file.getCanonicalPath());
				String destDir = getDestinationDir(file);
				if(choice.equalsIgnoreCase("Convert")) {
					renameFileExtension(file,destDir);
				} else if(choice.equalsIgnoreCase("Reverse")) {
					reverseFileExtensions(file,destDir);
				} else {
					System.out.println("Invalid Choice");
					System.exit(0);
				}
			}
			readingDir.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static String getDestinationDir(File sourceFile) throws IOException {
		String srcFolder = readingDir.getCanonicalPath().substring(readingDir.getCanonicalPath().lastIndexOf("\\"));
		String canoPath = sourceFile.getCanonicalPath();
		String changedCanoPath = canoPath.replaceFirst(srcFolder.substring(1), writtingDir);
		String destDir = changedCanoPath.substring(0, changedCanoPath.lastIndexOf("\\"));
		new File(destDir).mkdirs();
		return destDir;
	}
	
	public static void renameFileExtension(File source,String destDir) throws IOException {
		File newfile;
		if(source.getName().endsWith("txt") || source.getName().endsWith("TXT")) {
			newfile = new File (destDir+"\\"+source.getName());
		} else {
			String newSource = source.getName().replace(".", "_");
			String strNewFileName = newSource + ".txt";
			newfile = new File(destDir+"\\"+strNewFileName);
		}
		boolean Rename = source.renameTo(newfile);
		if(!Rename)  {
			System.out.println("FileExtension hasn't been changed successfully.");
		} else {
			System.out.println("FileExtension has been changed successfully.");
		}
	}
	
	public static void reverseFileExtensions(File sourceFile,String destDir) throws IOException {
		String newFile = null;
		String newSource = null;
		if(sourceFile.getName().indexOf("_") > 0) {
			newFile = sourceFile.getName().substring(0,sourceFile.getName().indexOf("."));
			newSource = newFile.replace("_", ".");
		} else {
			newSource = sourceFile.getName();
		}
		System.out.println("New File :: "+newFile + " , new Source :: "+newSource+ " " +destDir+"\\"+newSource);
		File newfile = new File(destDir+"\\"+newSource);
		System.out.println(sourceFile.getCanonicalPath()+" "+newfile.getCanonicalPath());
		boolean Rename = sourceFile.renameTo(newfile);
		if(!Rename)  {
			System.out.println("FileExtension hasn't been changed successfully.");
		} else {
			System.out.println("FileExtension has been changed successfully.");
		}
	}

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Please enter the Choice (Convert / Reverse): ");
			String inputchoice = in.readLine();
			choice = inputchoice;
			System.out.print("Enter the Reading Directory (mention full paath) : ");
			String inputDir = in.readLine();
			readingDir = new File(inputDir);
			System.out.print("Enter the over-riding path : ");
			String rewrittingDir = in.readLine();
			writtingDir = rewrittingDir;
			displayDirectoryContents();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
