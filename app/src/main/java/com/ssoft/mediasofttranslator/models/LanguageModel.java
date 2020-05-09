package com.ssoft.mediasofttranslator.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageModel {

    @SerializedName("dirs")
    @Expose
    private List<String> dirs = null;

    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }

}
