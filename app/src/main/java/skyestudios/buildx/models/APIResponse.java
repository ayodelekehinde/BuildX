package skyestudios.buildx.models;

public class APIResponse {
    public int responseCode;
    public String response;
    public APIResponse(int responseCode, String response){
        this.responseCode = responseCode;
        this.response = response;
    }
}
