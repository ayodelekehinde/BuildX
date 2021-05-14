package skyestudios.buildx.builderx;

public interface BuildTaskProgress {
    void onProgress(String progress);
    void onError(String error);
}
