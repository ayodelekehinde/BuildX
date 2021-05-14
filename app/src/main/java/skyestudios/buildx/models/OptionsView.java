package skyestudios.buildx.models;

public class OptionsView {
    private String name;
    private String impl;

    public OptionsView(String name, String impl){
        this.name = name;
        this.impl = impl;
    }

    public String getName() {
        return name;
    }

    public String getImpl() {
        return impl;
    }
}
