package skyestudios.buildx.interfaces;

public interface DownloadListener {
    void done();
    void error();
    void exists();
}
