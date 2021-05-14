package skyestudios.buildx.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
   public String projectName;
   public String packageName;
   /*
   path: Buildx/Projects/{projectName}
    */
   public String path;
   /*
   rootDir: Buildx/Projects/{projectName}/app
    */
   public String appDir;
   public String icon;

    public Project(){ }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(projectName);
        parcel.writeString(packageName);
        parcel.writeString(path);
        parcel.writeString(icon);
        parcel.writeString(appDir);
    }
}
