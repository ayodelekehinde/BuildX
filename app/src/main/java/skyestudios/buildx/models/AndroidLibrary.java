package skyestudios.buildx.models;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import skyestudios.buildx.helpers.FileUtil;

public class AndroidLibrary {
    private File manifestFile;
    private File assetsDir;
    private File resDir;
    private File jarDir;

    public AndroidLibrary(File manifestFile, File resDir, File jarDir){
        this.manifestFile = manifestFile;
        this.resDir = resDir;
        this.jarDir = jarDir;
    }


    public File getManifestFile() {
        return manifestFile;
    }

    public File getAssetsDir() {
        return assetsDir;
    }

    public File getResDir() {
        return resDir;
    }

    public File getJarDir() {
        return jarDir;
    }

    public ArrayList<File> getJavaLibraries() {
        File[] files = getJarDir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File jarLib) {
                return (jarLib.isFile() && jarLib.getName().endsWith(".jar"));
            }
        });
        return new ArrayList<>(Arrays.asList(files));
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
}
