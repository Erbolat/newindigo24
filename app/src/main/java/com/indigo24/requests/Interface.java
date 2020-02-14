package com.indigo24.requests;

import com.indigo24.objects.User;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Interface {

    public  static  String baseURL = "https://api.indigo24.xyz/api/v2.1/";
    public  static  String baseIMG = "https://api.indigo24.xyz/logos/";
    public  static  String baseAVATAR = "https://indigo24.com/uploads/avatars/";
    public  static  String baseAVATARold = "https://indigo24.com/uploads/avatars/";
    public static String tokenREG = "BGkA2as4#h_J@5txId3fEq6e!F80UMj197ZC";
    public static String tokenSMS = "2MSldk_7!FUh3zB18XoEfIe#nY69@0tcP5Q4";
    public static String tokenEXCHANGE = "#8kX1xtDr4qSY8_C9!N@cC9bvT0Pilk85DS32";
    public static String TAG = "INDIGO-";
    @FormUrlEncoded
    @POST("check/authentication")
    Call<ResponseBody> auth(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST("registration")
    Call<ResponseBody> reg(@Field("phone") String phone, @Field("password") String password,
    @Field("name") String name, @Field("device") String device, @Field("_token") String token);

    @FormUrlEncoded
    @POST("sms/send")
    Call<ResponseBody> sendSMS(@Field("phone") String phone, @Field("_token")  String token);

    @FormUrlEncoded
    @POST("check/sms")
    Call<ResponseBody> checkSMS(@Field("phone") String phone, @Field("code")  String code);


    @FormUrlEncoded
    @POST("create/pin")
    Call<ResponseBody> createPin(@Field("customerID") String id, @Field("unique") String unique, @Field("pinCode") String pincode);

    @FormUrlEncoded
    @POST("check/pin")
    Call<ResponseBody> checkPin(@Field("unique") String unique, @Field("customerID") String customerID, @Field("pinCode") String pincode);

    @FormUrlEncoded
    @POST("get/categories")
    Call<ResponseBody> getobj(@Field("unique") String unique, @Field("customerID") String customerID);

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
    @POST("get/exchanges")
    Call<ResponseBody> getExchange(@Field("unique") String unique, @Field("customerID") String customerID, @Field("_token") String token );


    @FormUrlEncoded
    @POST("service/pay")
    Call<ResponseBody> setPay(@Field("unique") String unique, @Field("customerID") String customerID, @Field("amount") String amount , @Field("serviceID") String serviceID, @Field("account") String account);

    @FormUrlEncoded
    @POST("check/send/money")
    Call<ResponseBody> sendMoney(@Field("unique") String unique, @Field("customerID") String customerID, @Field("amount") String amount , @Field("toID") String toID);


    @FormUrlEncoded
    @POST("check/send/money/phone")
    Call<ResponseBody> checkPhoneSendMoney(@Field("unique") String unique, @Field("customerID") String customerID, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("get/histories")
    Call<ResponseBody> getHistories(@Field("unique") String unique, @Field("customerID") String customerID);

    @FormUrlEncoded
    @POST("get/transactions")
    Call<ResponseBody> getTrans(@Field("unique") String unique, @Field("customerID") String customerID, @Field("page") String page);


    @FormUrlEncoded
    @POST("settings/save")
    Call<ResponseBody> editProfile(@Field("unique") String unique, @Field("customerID") String customerID, @Field("name") String name , @Field("city") String city );

    @FormUrlEncoded
    @POST("get/countries")
    Call<ResponseBody> getCountries(@Field("test") String test);


    @Multipart
    @POST("avatar/upload")
    Call<ResponseBody> uploadFile(@Part("unique") RequestBody un, @Part("customerID") RequestBody ws, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("check/phone")
    Call<ResponseBody> checkPhone(@Field("unique") String unique, @Field("phone") String phone);
}
