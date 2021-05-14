package skyestudios.buildx.layoutinflator;

public abstract class DynamicoListener {

    public abstract void onSuccess(String message);

    public abstract void onError(String message);
}