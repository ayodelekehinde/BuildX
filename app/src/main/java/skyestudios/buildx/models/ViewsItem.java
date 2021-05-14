package skyestudios.buildx.models;

public class ViewsItem extends ViewsModel{
    private String name;
    private String path;
    private String size;
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getViewName() {
        return name;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setViewName(String viewName) {
        this.name = viewName;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }
}
