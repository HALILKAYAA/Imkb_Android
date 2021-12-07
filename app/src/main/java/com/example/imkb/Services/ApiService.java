package com.example.imkb.Services;

import com.example.imkb.Models.DetailModels.DetailBody;
import com.example.imkb.Models.DetailModels.DetailModel;
import com.example.imkb.Models.HandshakeModel.HandshakeModel;
import com.example.imkb.Models.HandshakeModel.HandshakeReguestBody;
import com.example.imkb.Models.ListModel.ListBody;
import com.example.imkb.Models.ListModel.ListModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("api/handshake/start")
    Call<HandshakeModel> getFirstService (@Body HandshakeReguestBody body);

    @POST("api/stocks/list")
    Call<ListModel> getSecondListService (@Header("X-VP-Authorization") String authorization,
                                          @Header("Content-Type") String application,
                                          @Body ListBody body);

    @POST("api/stocks/detail")
    Call<DetailModel> getDetailService (@Header("X-VP-Authorization") String auth,@Header("Content-Type") String app,
                                        @Body DetailBody id);
}


