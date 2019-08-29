package org.qimi.lab.retrofittest;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TopoService {

    @GET("info")
    Call<TopoInfo> getInfo();
}
