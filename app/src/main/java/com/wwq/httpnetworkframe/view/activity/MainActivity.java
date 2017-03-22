package com.wwq.httpnetworkframe.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wwq.httpnetworkframe.R;
import com.wwq.httpnetworkframe.controller.library.retrofit.RetrofitLoginService;
import com.wwq.httpnetworkframe.view.base.BaseActivity;

import org.xutils.common.Callback;
//import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends BaseActivity {
    /**
     * 服务器地址
     */
    public static final String SERVER_URL = "http://192.168.0.232:8080";
    /**
     * 登录
     */
    public static final String SHOPPING_LOGIN = SERVER_URL + "/paopaokeji/shopping/login";
    @ViewInject(R.id.txt_json)
    private TextView txtJson;

    private String phoneStr = "13412345678";
    private String passwordStr = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化xUtils3
        x.view().inject(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    txtJson.setText((String) msg.obj);
                    break;
            }
        }
    };

    private void parseData(String result) {
        txtJson.setText(result);
    }

    @Event(value = R.id.btn_xutils, type = View.OnClickListener.class)
    private void onBtnXutilsClick(View view) {
        org.xutils.http.RequestParams params = new org.xutils.http.RequestParams(SHOPPING_LOGIN);
        params.addBodyParameter("phoneNumber", phoneStr);
        params.addBodyParameter("password", passwordStr);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Event(value = R.id.btn_okhttp, type = View.OnClickListener.class)
    private void onBtnOkHttpClick(View view) {
        MediaType mediaType = MediaType.parse("ContentType:application/x-www-form-urlencoded; charset=utf-8");
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("title", "wangshu")
//                .addFormDataPart("image", "wangshu.jpg",
//                        RequestBody.create(MEDIA_TYPE_PNG, new File("/sdcard/wangshu.jpg")))
//                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("phoneNumber", phoneStr)
                .add("password", passwordStr).build();
        Request request = new Request.Builder()
                .url(SHOPPING_LOGIN).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                Message msg = new Message();
//                msg.what = 101;
//                msg.obj = response.body().string();
//                handler.sendMessage(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parseData(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Event(value = R.id.btn_retrofit, type = View.OnClickListener.class)
    private void onBtnRetrofitClick(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL + "/paopaokeji/shopping/")
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
//                    //增加返回值为Gson的支持(以实体类返回)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    //增加返回值为Oservable<T>的支持
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        RetrofitLoginService requestSerives = retrofit.create(RetrofitLoginService.class);//这里采用的是Java的动态代理模式
        retrofit2.Call<String> call = requestSerives.getString(phoneStr, passwordStr);//传入我们请求的键值对的值
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
//                parseData(response.body().toString());
                Message msg = new Message();
                msg.what = 101;
                msg.obj = response.body().toString();
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {
            }
        });
    }

    @Event(value = R.id.btn_volley, type = View.OnClickListener.class)
    private void onBtnVolleyClick(View view) {
        RequestQueue mQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, SHOPPING_LOGIN,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseData(response);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("phoneNumber", phoneStr);
                map.put("password", passwordStr);
                return map;
            }
        };
        mQueue.add(stringRequest);
    }

    @Event(value = R.id.btn_async, type = View.OnClickListener.class)
    private void onBtnAsyncClick(View view) {
        RequestParams params = new RequestParams();
        params.put("phoneNumber", phoneStr);
        params.put("password", passwordStr);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(SHOPPING_LOGIN+"?", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                parseData(content);
            }
        });
    }
}
