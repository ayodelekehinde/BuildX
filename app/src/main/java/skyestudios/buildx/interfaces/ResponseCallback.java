package skyestudios.buildx.interfaces;

import skyestudios.buildx.models.APIResponse;

public interface ResponseCallback {
    void onResponse(APIResponse result);
    void onError();
}
