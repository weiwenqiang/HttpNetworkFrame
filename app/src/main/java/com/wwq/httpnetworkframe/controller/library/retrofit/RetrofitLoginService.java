package com.wwq.httpnetworkframe.controller.library.retrofit;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by wwq on 2017/3/22.
 */

public interface RetrofitLoginService {

    @POST("login")
    Call<String> getString(@Query("phoneNumber") String phoneNumber,
                           @Query("password") String password);
}
