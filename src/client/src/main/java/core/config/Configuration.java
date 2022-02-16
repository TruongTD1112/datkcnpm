package core.config;

import java.io.File;

public class Configuration {
	public final static String projectPath = System.getProperty("user.dir") + File.separatorChar +"src"+ File.separatorChar;
	public final static String inputPath =  System.getProperty("user.dir") + File.separatorChar +"src"+ File.separatorChar+"main"+ File.separatorChar+"java"+ File.separatorChar+"core" + File.separatorChar+"input"+ File.separatorChar;
	public final static String imagesPath = inputPath+"images/";
	public final static double INFINITIVE = 10000;
	
}
