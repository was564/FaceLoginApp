package com.example.facelogin;

import com.google.gson.annotations.SerializedName;

import java.io.File;

import retrofit2.http.Multipart;

public class FaceDataResource {

    @SerializedName("status")
    public int statusResult;
    @SerializedName("file_name")
    public String fileName;
    @SerializedName("name")
    public String name;
    @SerializedName("birth")
    public String birth;
}
