package org.qimi.lab.retrofittest;

public class TopoInfo {

    String version;
    String buildTime;

    public TopoInfo(String version, String buildTime) {
        this.version = version;
        this.buildTime = buildTime;
    }

    @Override
    public String toString() {
        return "TopoInfo{" +
                "version='" + version + '\'' +
                ", buildTime='" + buildTime + '\'' +
                '}';
    }
}
