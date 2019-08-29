package org.qimi.lab.retrofittest;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitTest {

    public static void main(String[] args) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.42.13.136:13651/api/topo/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TopoService service = retrofit.create(TopoService.class);
        Call<TopoInfo> call = service.getInfo();
        try {
            System.out.println("info 1:"+call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Call<TopoInfo> call2 = service.getInfo();
        try {
            System.out.println("info 2:"+call2.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Call<TopoInfo> call3 = service.getInfo();
        try {
            System.out.println("info 3:"+call3.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Call<TopoInfo> call4 = service.getInfo();
        try {
            System.out.println("info 4:"+call4.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
