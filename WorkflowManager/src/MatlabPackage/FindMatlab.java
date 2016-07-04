package MatlabPackage;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

public class FindMatlab {

	public static String findFile(String name,File file)
    {
        File[] list = file.listFiles();
        if(list!=null)
        for (File fil : list)
        {
            if (fil.isDirectory())
            {
                findFile(name,fil);
            }
            else if (name.equalsIgnoreCase(fil.getName()))
            {
                return fil.getParentFile().toPath().toAbsolutePath().toString();
            }
        }
        return "";
    }
	
	public static String getOnWindows(){
		return findFile("C:/",new File("matlab.exe"));
		/*
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:matlab.exe");

			Path filename = Paths.get("C:/");
			if (matcher.matches(filename)) {
			    return filename.toAbsolutePath().toString();
			}
			return "";
			*/
	}
	
	public static void main(String[] args){
		System.out.print("");
	}
	
}
