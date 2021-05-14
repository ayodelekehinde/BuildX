package skyestudios.buildx.models;

public class LogicModel {
    String logicName;
    String path;
    String size;

    public LogicModel(){
    }

    public LogicModel(String logicName, String path, String size){
        this.logicName = logicName;
        this.path = path;
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }




    public String getViewName() {
        return logicName;
    }

    public void setViewName(String logicName) {
        this.logicName = logicName;
    }
}
