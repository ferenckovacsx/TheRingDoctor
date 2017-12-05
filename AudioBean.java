package com.example.ferenckovacsx.theringdoctor;

/**
 * Created by ferenckovacsx on 2017-12-05.
 */

public class AudioBean {

    public String fileName;
    public String Uri;

    public AudioBean(String fileName, String uri) {
        this.fileName = fileName;
        Uri = uri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    @Override
    public String toString() {
        return "AudioBean{" +
                "fileName='" + fileName + '\'' +
                ", Uri='" + Uri + '\'' +
                '}';
    }
}
