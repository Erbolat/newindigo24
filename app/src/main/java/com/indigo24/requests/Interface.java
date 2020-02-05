package com.indigo24.requests;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Interface {

    public  static  String baseURL = "https://api.indigo24.site/api/v2.1/";
    public  static  String baseIMG = "https://api.indigo24.site/logos/";
    public  static  String baseAVATAR = "https://indigo24.site/uploads/avatars/";
    public static String tokenREG = "BGkA2as4#h_J@5txId3fEq6e!F80UMj197ZC";
    @FormUrlEncoded
    @POST("check/authentication")
    Call<ResponseBody> auth(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST("registration")
    Call<ResponseBody> reg(@Field("phone") String phone, @Field("password") String password,
    @Field("name") String name, @Field("device") String device, @Field("_token") String token);

    @FormUrlEncoded
    @POST("create/pin")
    Call<ResponseBody> createPin(@Field("customerID") String id, @Field("unique") String unique, @Field("pinCode") String pincode);

    @FormUrlEncoded
    @POST("check/pin")
    Call<ResponseBody> checkPin(@Field("unique") String unique, @Field("customerID") String customerID, @Field("pinCode") String pincode);

    @FormUrlEncoded
    @POST("get/categories")
    Call<ResponseBody> getCateg(@Field("unique") String unique, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("get/services")
    Call<ResponseBody> getServices(@Field("unique") String unique, @Field("customerID") String customerID, @Field("categoryID") String categoryID );

    @FormUrlEncoded
    @POST("get/profile")
    Call<ResponseBody> getProfile(@Field("unique") String unique, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("get/payments")
    Call<ResponseBody> getPayments(@Field("unique") String unique, @Field("customerID") String customerID, @Field("serviceID") String serviceID );


    @FormUrlEncoded
    @POST("service/pay")
    Call<ResponseBody> setPay(@Field("unique") String unique, @Field("customerID") String customerID, @Field("amount") String amount , @Field("serviceID") String serviceID, @Field("account") String account);


    @FormUrlEncoded
    @POST("settings/save")
    Call<ResponseBody> editProfile(@Field("unique") String unique, @Field("customerID") String customerID, @Field("name") String name , @Field("city") String city );


}
