package skyestudios.buildx.keystorecreator.keystoreviews;

public class KeyParams {
   public String path;
   public String name;
   public String keystorepw;
   public String keypass;
   public String rootDir;

    public KeyParams(String path, String name, String keystorepw, String keypass, String rootDir) {
        this.path = path;
        this.name = name;
        this.keystorepw = keystorepw;
        this.keypass = keypass;
        this.rootDir = rootDir;
    }
}
