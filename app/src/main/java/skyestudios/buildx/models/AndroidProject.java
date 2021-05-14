package skyestudios.buildx.models;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class AndroidProject {
    private ArrayList<File> javaFiles;
    private File rootDir; //app
    private File srcDir;  //app src
    private File buildDir; //app build
    private File resDir;
    private File javaDir;
    private File assetsDir;
    private File manifestFile;
    private File libsDir;
    private File dirDexedClasses;
    private File dirClasses;
    private File dirDexedLibs;
    private File dexFile;
    private File resourceArsc;
    private File app;
    private File jarDir;
    private String packageName;
    private String appName;
    private ArrayList<AndroidLibrary> libraries = new ArrayList<>();

    public AndroidProject(String rootDir,String packageName, String appName, ArrayList<AndroidLibrary> libraries){
        this.rootDir = new File(rootDir);
        app = new File(this.rootDir.getAbsolutePath().concat("/app"));
        this.packageName = packageName;
        this.appName = appName;
        this.libraries = libraries;
        init();

    }


    private void init(){
         buildDir = initDir(app,"build");
        if (libsDir==null|| !libsDir.exists()) libsDir = initDir(app,"libs");
         dirDexedLibs = new File(buildDir,"dexedLibs");
         dirDexedLibs.mkdir();
        dirClasses = new File(buildDir,"classes");
        dirClasses.mkdir();
        dirDexedClasses =  new File(buildDir,"dexedClasses");
        dirDexedClasses.mkdir();
         resourceArsc = new File(dirDexedClasses,"resources.arsc");
        dexFile = new File(dirDexedClasses,"classes.dex");



    }
    private static File initDir(File parent, String childName){
        File dir = new File(parent, childName);
        if(!dir.exists()) dir.mkdir();
        return dir;
    }

    public String getPackageName() {
        return packageName;
    }

    public ArrayList<File> getJavaFiles() {
        ArrayList<File> javaFiles = new ArrayList<>();
        javaFiles.add(new File(srcDir, "java"));
        return javaFiles;
    }
    public File getResourceArsc() {
        return resourceArsc;
    }
    public File getGenDirectory(){
        return new File(rootDir.getAbsolutePath().concat("/gen"));
    }

    public File getRootDir() {
        return rootDir;
    }

    public File getSrcDir() {
        return new File(app.getAbsolutePath().concat("/src"));
    }

    public File getBuildDir() {
        return buildDir;
    }

    public File getResDir() {
        return new File(getSrcDir().getAbsolutePath().concat("/main/res"));
    }

    public File getJavaDir() {
        return new File(getSrcDir().getAbsolutePath().concat("/main/java"));
    }

    public File getAssetsDir() {
        return new File(getSrcDir().getAbsolutePath().concat("/main/assets"));
    }

    public File getManifestFile() {
        return new File(getSrcDir().getAbsolutePath().concat("/main/AndroidManifest.xml"));
    }

    public File getLibsDir() {
        return libsDir;
    }

    public File getDirDexedClasses() {
        return dirDexedClasses;
    }

    public File getDirClasses() {
        return dirClasses;
    }

    public File getDirDexedLibs() {
        return dirDexedLibs;
    }

    public File getDexFile() {
        return dexFile;
    }

    public ArrayList<AndroidLibrary> getLibraries() {
        return libraries;
    }
    public File getUnSignedApk(){
        return new File(getRootDir().getAbsolutePath().concat("/unsigned.apk"));
    }
    public File getSignedApk(){
        return new File(getRootDir().getAbsolutePath().concat("/signed.apk"));
    }

    public void setLibraries(ArrayList<AndroidLibrary> libraries) {
        this.libraries = libraries;
    }
    public ArrayList<File> getJavaLibraries() {
        ArrayList<File> libList = new ArrayList<>();
        for (AndroidLibrary lib: getLibraries()){
            File[] files = lib.getJarDir().listFiles(new FileFilter() {
                @Override
                public boolean accept(File jarLib) {
                    return (jarLib.isFile() && jarLib.getName().endsWith(".jar"));
                }
            });
            libList.addAll(Arrays.asList(files));
        }

        return libList;
    }

    public String getClasspath() {
        ArrayList<File> javaLibraries = getJavaLibraries();
        StringBuilder classpath = new StringBuilder(".");
        for (File javaLibrary : javaLibraries) {
            if (classpath.length() != 0) {
                classpath.append(File.pathSeparator);
            }
            classpath.append(javaLibrary.getAbsolutePath());
        }
        return classpath.toString();
    }
    public ArrayList<File> getResLibraries() {
        ArrayList<File> libList = new ArrayList<>();
        for (AndroidLibrary lib: getLibraries()){
            File[] files = lib.getJarDir().listFiles(new FileFilter() {
                @Override
                public boolean accept(File jarLib) {
                    return (jarLib.isDirectory() && jarLib.getName().equals("res"));
                }
            });
            libList.addAll(Arrays.asList(files));
        }

        return libList;
    }
    public String getSourcePath() {
        StringBuilder srcPath = new StringBuilder();
        if (srcPath.length() != 0) {
            srcPath.append(File.pathSeparator);
        }
        srcPath.append(getJavaDir().getAbsolutePath());

        srcPath.append(File.pathSeparator).append(getGenDirectory().getAbsolutePath());
        return srcPath.toString();
    }

    public String[] getAllSourceFiles(File file) {
        ArrayList<String> javaFiles = new ArrayList<>();
        String[] sourcePaths = rootDir.getAbsolutePath().split(File.pathSeparator);
        for (String sourcePath : sourcePaths) {
            getAllSourceFiles(javaFiles, new File(sourcePath));
        }

        String[] sources = new String[javaFiles.size()];
        return javaFiles.toArray(sources);
    }

    public void getAllSourceFiles(ArrayList<String> toAdd, File parent) {
        if (!parent.exists()) {
            return;
        }
        for (File child : parent.listFiles()) {
            if (child.isDirectory()) {
                getAllSourceFiles(toAdd, child);
            } else if (child.exists() && child.isFile()) {
                if (child.getName().endsWith(".java")) {
                    toAdd.add(child.getAbsolutePath());
                }
            }
        }
    }
    public void clean(){
        if (dirClasses != null && dirDexedClasses != null){
            dirClasses.delete();
            dirDexedClasses.delete();
        }

    }

}
