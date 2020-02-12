package com.indigo24.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.indigo24.R;
import com.indigo24.activities.MainActivity;
import com.indigo24.activities.Profile;
import com.indigo24.objects.User;
import com.indigo24.objects.VolleyMultipartRequest;
import com.indigo24.requests.Interface;
import com.indigo24.requests.RetrofitClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.indigo24.requests.Interface.baseAVATAR;

public class profileEditFR extends Fragment {

    @BindView(R.id.editUsername)
    EditText editName;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.editCity)
    EditText editCity;
    @BindView(R.id.btnSave)
    Button btnSave;
    String unique, userID, name, city, url;
    private String upload_URL = "https://api.indigo24.site/api/v2.1/avatar/upload";
    private RequestQueue rQueue;
    private final int GALLERY = 1;
    public static int openEdit = 1;
    private final int RESULT_CANCELED = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile, container, false);
        ButterKnife.bind(this, v);
        openEdit = 1;
        requestMultiplePermissions();
        SharedPreferences sPref = getContext().getSharedPreferences("UserData", getContext().MODE_PRIVATE);
        userID = sPref.getString("id", "");
        unique = sPref.getString("unique", "");
        url = sPref.getString("url", "");
        Bundle args = getArguments();
        if (args != null) {
            editName.setText(args.getString("name"));
            editCity.setText(args.getString("city"));
            Picasso.get().load(args.getString("url")).transform(new CropCircleTransformation()).into(img);
        }

        return v;
    }

    @OnClick({R.id.btnSave, R.id.img})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                name = editName.getText().toString();
                city = editCity.getText().toString();
                if (city.isEmpty()) city = "";
                if (!name.isEmpty()) save();
                else
                    Toast.makeText(getContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, GALLERY);
                break;
        }
    }





    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    // imageView.setImageBitmap(bitmap);
                    Bitmap bOutput;
                    float degrees = 90; //rotation degree
                    Matrix matrix = new Matrix();
                    matrix.setRotate(degrees);
                    bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    img.setImageBitmap(bOutput);
                    uploadImage(bOutput);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    private void uploadImage(final Bitmap bitmap){
        swipe.setRefreshing(true);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        rQueue.getCache().clear();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                            Toast.makeText(getActivity(), "Успешно!", Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonObject.has("success") && !jsonObject.getBoolean("success") && jsonObject.has("message"))
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            swipe.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipe.setRefreshing(false);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Ошибка!", Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("unique", unique);
                params.put("customerID", userID);
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void save() {
        btnSave.setClickable(false);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.editProfile(unique, userID, name, city);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        if(jsonObject.has("message"))
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        getActivity().startActivity(new Intent(getActivity(), Profile.class));
                        getActivity().finish();
                    }
                    else {
                        if(jsonObject.has("message"))
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        btnSave.setClickable(true);
                    }
                } catch (JSONException | NullPointerException | IOException e) {
                    e.printStackTrace();
                    btnSave.setClickable(true);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                btnSave.setClickable(true);
                Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                          //  Toast.makeText(getContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                      //  Toast.makeText(getContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}
