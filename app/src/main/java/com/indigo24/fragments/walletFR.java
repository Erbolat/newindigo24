package com.indigo24.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.indigo24.R;
import com.indigo24.activities.MainActivity;
import com.indigo24.requests.Interface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.InvalidMarkException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class walletFR extends Fragment {

    @BindViews({R.id.btnPayobj, R.id.btnSendFriend,R.id.btnPay})
    View[] viewBtns;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_wallet, container, false);
        ButterKnife.bind(this,v);

        return  v;
    }

    @OnClick({R.id.btnPayobj ,R.id.btnSendFriend, R.id.btnPay, R.id.btnHistorySend,
            R.id.btnHistoryTrans, R.id.btnExchange})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.btnPayobj:
            case R.id.btnPay: {
                openFragment(new allPaysFR());
                break;
            }
            case R.id.btnSendFriend: {
                openFragment(new sendFriendFR());
                break;
            }
            case R.id.btnHistorySend: {
                openFragment(new historySendFR());
                break;
            }
            case R.id.btnHistoryTrans: {
                openFragment(new historyTransFR());
                break;
            }
            case R.id.btnExchange: {
                openFragment(new exchangeFR());
                break;
            }
        }
    }


    void openFragment(Fragment fr){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fr);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
