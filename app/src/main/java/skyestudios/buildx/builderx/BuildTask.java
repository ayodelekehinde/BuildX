package skyestudios.buildx.builderx;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringJoiner;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import kellinwood.security.zipsigner.ZipSigner;
import skyestudios.buildx.R;
import skyestudios.buildx.activities.HomeActivity;
import skyestudios.buildx.activities.MainActivity;
import skyestudios.buildx.helpers.FileUtil;
import skyestudios.buildx.helpers.IOUtils;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.helpers.Wood;
import skyestudios.buildx.keystorecreator.CustomKeySigner;
import skyestudios.buildx.models.AndroidLibrary;
import skyestudios.buildx.models.AndroidProject;

import com.android.sdklib.build.ApkBuilder;

import com.android.sdklib.build.ApkCreationException;
import com.android.sdklib.build.DuplicateFileException;
import com.android.sdklib.build.SealedApkException;
import com.duy.dex.Dex;
import com.duy.dx.command.dexer.DxContext;
import com.duy.dx.merge.CollisionPolicy;
import com.duy.dx.merge.DexMerger;
import com.github.underscore.lodash.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


public class BuildTask {

    BuildFiles buildFiles;
    Context context;
    boolean verbose = true;
    PrintStream out = System.out;
    PrintStream err = System.err;
    AndroidProject project;
    BuildTaskProgress taskProgress;
    private File newDexLib;
    private ArrayList<File> dexedFiles;

    public BuildTask(Context context, AndroidProject project) {
        buildFiles = new BuildFiles(context);
        this.project = project;
        this.context = context;

        unpack();
    }
    public void setProgressListener(BuildTaskProgress taskProgress){
        this.taskProgress = taskProgress;
        try {
           runAapt();
        } catch (Exception e) {

        }
    }

    private void runAapt() throws Exception {
        String minSdkVersion = "";
        String targetSdkVersion = "";
        String verCode = "";
        String verName = "";
        File file = new File(project.getRootDir().getAbsolutePath().concat("/app/build.gradle"));
        String gradle = FileUtil.readFile(file.getAbsolutePath());
        String [] lines = gradle.split("\n");
        for (String s: lines){
            String line = s.split(" ")[0].trim();
            if (line.split(" ")[0].equalsIgnoreCase("minsdkversion")){
                minSdkVersion  = s.split(" ")[1];
            }else if (line.split(" ")[0].equalsIgnoreCase("targetsdkversion")){
                targetSdkVersion = s.split(" ")[1];
            }else if (line.split(" ")[0].equalsIgnoreCase("versioncode")){
                verCode = s.split(" ")[1];
            }else if (line.split(" ")[0].equalsIgnoreCase("versionName")){
                verName = s.split(" ")[1].replace("\"","");
            }
        }
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        StringBuilder builder = new StringBuilder();
        if (projectBx.contains("firebase")){
            File tempManifest = project.getManifestFile();
            String manifestStr = FileUtil.readFile(tempManifest.getAbsolutePath());
            manifestStr = manifestStr.replace("</application>","").replace("</manifest>","");
            builder.append(manifestStr);
            String firebase = "firebase.xml";
            String content = IOUtils.toString(context.getAssets().open(firebase));
            builder.append(content);
            Utils.saveFile(tempManifest,builder.toString());
        }
        File androidJar = new File(buildFiles.androidJar.getAbsolutePath());
        taskProgress.onProgress("Aapt is running");
        //Run Aapt for Libs
        //TODO: Add asset dir to aapt
        ArrayList<String> libCommand = new ArrayList<>();
        libCommand.add(buildFiles.aapt.getAbsolutePath());
        libCommand.add(" package ");
        libCommand.add(" -I " + androidJar.getAbsolutePath());
        libCommand.add(" --extra-packages " + libPackageNames());
        libCommand.add(" --auto-add-overlay ");
        libCommand.add(" --non-constant-id ");
        libCommand.add(" --min-sdk-version " + minSdkVersion);
        libCommand.add(" --target-sdk-version " + targetSdkVersion);
        libCommand.add(" --version-code " + verCode);
        libCommand.add(" --version-name " + verName);
        libCommand.add(" --output-text-symbols " + project.getGenDirectory().getAbsolutePath());
        libCommand.add(" -f ");
        libCommand.add(" -v ");
        libCommand.add(" -m ");
        libCommand.add(" -S " + project.getResDir().getAbsolutePath());
        for (AndroidLibrary library : project.getLibraries()) {
            if (library.getResDir() != null) {
                libCommand.add(" -S " + library.getResDir().getAbsolutePath());

            }
        }
        libCommand.add(" -M " +project.getManifestFile().getAbsolutePath());
        libCommand.add(" -J " +project.getGenDirectory().getAbsolutePath());
        libCommand.add(" -F " +project.getResourceArsc().getAbsolutePath());

        String aaptCommand = TextUtils.join("",libCommand);

        Log.d("AAPT", aaptCommand);
        int val= exec(aaptCommand);
        File classesDir = freshFile(project.getBuildDir(),"classes");

        if (val == 0) {
            Log.d("Aapt",val + "  Aapt Successful");
            runCompiler(androidJar,classesDir);
        }else{
            taskProgress.onError("Aapt Failed");
            Log.d("Aapt", val + "  Aapt Failed");
        }

    }
    private void runCompiler(File androidJar, File classesDir) throws Exception {
        taskProgress.onProgress("Java Compiling...");
        Log.d("BuildTask", project.getClasspath());
        boolean systemExitWhenFinished = false;
        initErrFile();

        String[] compilerArgs = {
                "-classpath", project.getClasspath(),
                "-verbose",
                "-sourcepath", project.getSourcePath(),
                "-bootclasspath", androidJar.getAbsolutePath(),
                "-1.7",  //java version to use
                "-target", "1.7", //target java level
                "-proc:none",
                "-d", classesDir.getAbsolutePath(),
                project.getJavaDir().getAbsolutePath(),
                project.getGenDirectory().getAbsolutePath()
        };

        logd("Compiling", verbose);
        org.eclipse.jdt.internal.compiler.batch.Main compiler = new org.eclipse.jdt.internal.compiler.batch.Main(new PrintWriter(out), new PrintWriter(err), systemExitWhenFinished, null, null);
        if (compiler.compile(compilerArgs)){
            logd("Compile Success", verbose);
            dexLibs();

        }else {

            taskProgress.onError("Compile Error");
            logd("Compile Error", verbose);
            return;

        }

    }

    private void dexLibs() throws Exception {
        Log.d("BuildTask", "dexLibs()");
        dexedFiles = new ArrayList<>();
        String projectBx = FileUtil.readFile(MainActivity.getROOTDIR().concat("/app/project.bx"));
        String[] libsToAdd = projectBx.split("\n");
        for (File jarFolderLib: Utils.getAppLibrary().listFiles()) {
            for (String s: libsToAdd) {
                if (s.trim().equals(jarFolderLib.getName())) {
                    for (File jarLib : jarFolderLib.listFiles()) {
                        if (!jarLib.isFile() || !jarLib.getName().endsWith(".jar")) {
                            continue;
                        }
                        String md5 = Util.getMD5Checksum(jarLib);

                         newDexLib = new File(Utils.getDexedLibs(), jarLib.getName().replace(".jar", "-" + md5 + ".dex"));
                         dexedFiles.add(newDexLib);
                        System.out.println(newDexLib.getName());
                        if (!newDexLib.exists()) {
                            taskProgress.onProgress("Dexing Libs...");
                            String[] args = {"--verbose",
                                    "--no-strict",
                                    "--no-files",
                                    "--output=" + newDexLib.getAbsolutePath(), //output
                                    jarLib.getAbsolutePath() //input
                            };

                            com.duy.dx.command.dexer.Main.main(args);
                        }
                    }
                }
            }
        }

        Log.d("BuildTask", "To call dexClasses()");
        dexClasses();
    }

    private void dexClasses() throws Exception {
        Log.d("BuildTask", "dexClasses()");
        taskProgress.onProgress("Dexing Project...");
        ArrayList<String> command = new ArrayList<>();
        command.add("--verbose");
        command.add("--output=" + project.getDexFile().getAbsolutePath());
        command.add(project.getDirClasses().getAbsolutePath());
        String[] array = new String[command.size()];
        command.toArray(array);

        Log.d("Build", "Command" +command);
        int result = com.duy.dx.command.dexer.Main.main(array);
        if (result == 0){
            dexMerge();
        }else {
            taskProgress.onProgress("Dex Error");
        }
    }

    private void dexMerge() throws Exception {
        File[] dexedLibs = Utils.getDexedLibs().listFiles(pathname -> {
            return pathname.isFile() && pathname.getName().endsWith(".dex");
        });

        if (dexedLibs.length >= 1) {
            for (File dexLib : dexedLibs) {
                for (File dexedLib: dexedFiles) {
                    if (dexedLib.getName().equals(dexLib.getName())) {
                        taskProgress.onProgress("Dex Merging...");
                        Dex[] toBeMerge = {new Dex(project.getDexFile()), new Dex(dexLib)};
                        DexMerger merged = new DexMerger(toBeMerge, CollisionPolicy.KEEP_FIRST, new DxContext());
                        Dex merge = merged.merge();
                        merge.writeTo(project.getDexFile());
                    }

                }

            }
        }
        buildApk();
    }


    private void buildApk() throws Exception {
        taskProgress.onProgress("Bundling Apk...");

        if (project.getUnSignedApk().exists()) {
            project.getUnSignedApk().delete();
            Wood.GRADLE("Is unsigned exists? :" + project.getUnSignedApk().exists());
        }
        ApkBuilder apkbuilder = new ApkBuilder(project.getUnSignedApk(), project.getResourceArsc(), project.getDexFile(), null, null,
                System.out);
        apkbuilder.setDebugMode(false);
        if(project.getAssetsDir()!=null) {
            File[] assetFiles = project.getAssetsDir().listFiles();
            for (File assetFile : assetFiles) {
                if (assetFile.isDirectory()){
                    Wood.GRADLE("Asset file: "+ assetFile);
                    checkAssets(assetFile,apkbuilder);
                }else {
                    apkbuilder.addFile(assetFile, "assets/" + assetFile.getName());
                }
            }

        }

        apkbuilder.sealApk();

        zipSign();
    }

    private void zipSign() throws Exception {
        Wood.GRADLE("ZipSign() called");
        taskProgress.onProgress("Signing Apk...");
        ZipSigner zipsigner = new ZipSigner();
        zipsigner.setKeymode(ZipSigner.KEY_TESTKEY);
        zipsigner.signZip(project.getUnSignedApk().getAbsolutePath(), project.getSignedApk().getAbsolutePath());
        taskProgress.onProgress("Done");
        Util.openApk(context,project.getSignedApk());
        //project.getUnSignedApk().delete();
        project.clean();
    }
    private void checkAssets(File toadd, ApkBuilder builder){
        if (toadd.listFiles() == null){
            return;
        }
        for (File file: toadd.listFiles()){
            if (file.isDirectory()){
                checkAssets(file,builder);
            }else {
                try {
                    builder.addFile(file,"assets/".concat(toadd.getName()).concat(file.getName()));
                } catch (ApkCreationException e) {
                    e.printStackTrace();
                } catch (SealedApkException e) {
                    e.printStackTrace();
                } catch (DuplicateFileException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private String libPackageNames(){
        StringBuilder packs = new StringBuilder();
        for (AndroidLibrary lib: project.getLibraries()){
            File manifest = lib.getManifestFile();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream is = new FileInputStream(manifest);
                Document doc = builder.parse(is);
                XPathFactory xfact = XPathFactory.newInstance();
                XPath xPath = xfact.newXPath();
                org.w3c.dom.Element ele = (Element) xPath.evaluate("/manifest",doc, XPathConstants.NODE);

                String pack = ele.getAttribute("package");
                packs.append(pack);
                packs.append(":");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return packs.deleteCharAt(packs.lastIndexOf(":")).toString();
    }


    private void unpack(){
        buildFiles.clear();
        try {
            unpackAsset(context, "android.jar", buildFiles.androidJar);
        } catch (IOException e) {
            Logger.trace(e, true, System.err);
            Utils.showMessage(context, context.getString(R.string.upack_androidJar_failed));
            return;
        }
        try {
            unpackAapt(context, buildFiles.aapt);
        } catch (IOException e) {
            Logger.trace(e, true, System.err);;
            Utils.showMessage(context, context.getString(R.string.upack_aapt_failed));
            return;
        }

    }


    private void initErrFile(){
        try {
            File errFile = new File(FileUtil.getExternalStorageDir().concat("/BuildX/"),"errFile.txt");
            err = new PrintStream(errFile);
            System.setErr(err);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private File freshFile(File dir, String name) {
        File f = new File(dir, name);
        if (f.exists()) {
            deleteRecursive(f);
        }
        return f;
    }
    private void deleteRecursive(File file){
        if(file.isDirectory()){
            for(File f : file.listFiles()){
                deleteRecursive(f);
            }
        }else{
            file.delete();
        }
    }
    void logd(String msg, boolean verbose){
        Logger.logd(msg, verbose, out);
    }

    public int exec(String command) {
        try {
            Process pp = Runtime.getRuntime().exec(command);
            readStreams(pp, command);
            return pp.exitValue();
        } catch (Exception e) {
            trace(e);
            return -1;
        }
    }
    private void readStream(InputStream stream, Process pp) throws IOException {
        if(verbose) {
            if (stream == pp.getErrorStream()) {
                initErrFile();
                copyStream(stream, err);
            }else {
                copyStream(stream, out);
            }
        }
    }


    public long copyStream(InputStream is, OutputStream os) {
        final int BUFFER_SIZE = 8192;
        byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        int len = 0;
        try {
            while (-1 != (len = is.read(buf))) {
                os.write(buf, 0, len);
                total += len;
            }
        } catch (IOException ioe) {
            throw new RuntimeException("error reading stream", ioe);
        }
        return total;
    }

    private void readStreams(Process pp, String name) {
        if(verbose) {
            try {
                readStream(pp.getInputStream(),pp);
                readStream(pp.getErrorStream(),pp);
                pp.waitFor();
            } catch (IOException e) {
                trace(e);
            } catch (InterruptedException e) {
                trace(e);
            }
        }
    }
    void trace(Exception e){
        Logger.trace(e, err);
    }

    private static File unpackAsset(Context context, String assetName, File dest) throws IOException {
        if(!dest.exists()){
            InputStream assetIn = context.getAssets().open(assetName);
            dest.createNewFile();
            int length = 0;
            byte[] buffer = new byte[4096];
            FileOutputStream rawOut = new FileOutputStream(dest);
            while ((length = assetIn.read(buffer)) > 0) {
                rawOut.write(buffer, 0, length);
            }
            rawOut.flush();
            rawOut.close();
            assetIn.close();
        }
        return dest;
    }
    private static File unpackAapt(Context context, File aapt) throws IOException {
        if (!aapt.exists()) {
            String aaptToUse = null;
            boolean usePie = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
            String abi;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                String[] abis = Build.SUPPORTED_32_BIT_ABIS;
                for (String mAbi : abis) {
                    aaptToUse = getAaptFlavor(mAbi, usePie);
                    if (aaptToUse != null) {
                        break;
                    }
                }
            } else {
                aaptToUse = getAaptFlavor(Build.CPU_ABI, usePie);
            }
            if (aaptToUse == null) {
                aaptToUse = "aapt-arm";
            }
            if (usePie) {
                aaptToUse += "-pie";
            }
            unpackAsset(context, aaptToUse, aapt);
        }
        if(!aapt.canExecute()){
            aapt.setExecutable(true, true);
        }
        if(!aapt.canExecute()) {
            Runtime.getRuntime().exec("chmod 777 " + aapt.getAbsolutePath());
        }
        return aapt;
    }

    private static String getAaptFlavor(String abi, boolean usePie) {
        abi = abi.substring(0, 3).toLowerCase(Locale.ENGLISH);
        String aaptToUse = null;
        if (abi.equals("arm")) {
            aaptToUse = "aapt-arm";
        } else if (abi.equals("x86")) {
            aaptToUse = "aapt-x86";
        } else if (abi.equals("mip") && !usePie) {
            aaptToUse = "aapt-mip";
        }
        return aaptToUse;
    }


}