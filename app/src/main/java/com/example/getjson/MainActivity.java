package com.example.getjson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "name";
    private static final String TAG_NAME = "job";
    private static final String TAG_ADDRESS ="many";

    private static TextView mTextViewResult;
    //private static TextView mTextTest;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    static ArrayList<Test> testList = new ArrayList<Test>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        //mTextTest = (TextView)findViewById(R.id.listView_main_list);
        MyAsynctask task = new MyAsynctask();
        task.execute("http://192.168.0.201:5000/Android/getjson.php","NAME","JOB","MANY");
        // 안드로이드 스튜디오 Emulator 로는 localhost:포트번호 접근이 불가능하다.
        // + 안드로이드는 기본적으로 HTTP conn 을 금지한다 - manifest 수정이 필요.
    }

    public static class MyAsynctask extends AsyncTask<String, Void, String> {
        public MyAsynctask(){
            super();
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings){
            // 서브스레드로 처리되는 곳.
            String serverURL = (String) strings[0];
            String key1 = (String) strings[1];
            String key2 = (String) strings[2];
            String key3 = (String) strings[3];

            String postParameters = "";
            // 전달할 post 문장 만들기 (postParameters)

            String json = "";
            try {
                // 2. HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송합니다.
                URL url = new URL(serverURL); // 주소가 저장된 변수를 이곳에 입력합니다.

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생합니다.
                httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생합니다.

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("key1", key1);
                    jsonObject.put("key2", key2);
                    jsonObject.put("key3", key3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json = jsonObject.toString();
                Log.e("json", "생성한 json : " + jsonObject.toString());

                // Set some headers to inform server about the type of the content
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-type", "application/json");

                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                httpURLConnection.setDoOutput(true);
                // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
                httpURLConnection.setDoInput(true);


                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(json.getBytes("UTF-8")); //전송할 데이터가 저장된 변수를 이곳에 입력합니다.

                outputStream.flush();
                outputStream.close();

                // 응답을 읽습니다.

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                    // 정상적인 응답 데이터
                    inputStream = httpURLConnection.getInputStream();
                } else {

                    // 에러 발생

                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result) {
            String ganfan = "";
            super.onPostExecute(result);

            //progressDialog.dismiss();
            mTextViewResult.setText(result);
            testJSONParsing(result);
            Log.d(TAG, "POST response  - " + result);
            for(int i=0; i<testList.size();i++){
                ganfan = ganfan + testList.get(i).getTest1() + "   "
                                + testList.get(i).getTest2() + "   "
                                + testList.get(i).getTest3() + "\n";
            }
            //mTextTest.setText(ganfan);
        }

    }

    private static void testJSONParsing(String json) // Json 포맷의 String형 변수를 넘겨줌.
    {
        try{
            JSONObject jsonObject = new JSONObject(json);

            JSONArray testArray = jsonObject.getJSONArray("Test");

            for(int i=0; i<testArray.length(); i++)
            {
                JSONObject movieObject = testArray.getJSONObject(i);

                Test test = new Test();

                test.setTest1(movieObject.getString("name"));
                test.setTest2(movieObject.getString("job"));
                test.setTest3(movieObject.getString("many"));

                testList.add(test);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}