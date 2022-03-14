package com.example.getjson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private static String TAG = "phptest_MainActivity";  // 디버그 서칭.

    static ArrayList<Test> testList = new ArrayList<Test>();
    private static CustomAdapter mAdapter;
    public Button buttonInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_main);

        buttonInsert = (Button) findViewById(R.id.button_main_insert);
        MyAsynctask task = new MyAsynctask();
        task.execute("http://192.168.0.201:5000/Android/getjson.php","NAME","JOB","MANY","END");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view); // 리사이클러뷰
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager); // 리사이클러뷰에 리니어레이아웃매니저를 세팅.

        mAdapter = new CustomAdapter(testList);  // 어댑터를 리스트로 초기화.
        mRecyclerView.setAdapter(mAdapter); // 리사이클러뷰에 커스텀어댑터를 세팅.

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        buttonInsert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //mAdapter.notifyDataSetChanged();
            }
        });
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
            String key4 = (String) strings[4];

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
                    jsonObject.put("key4", key4);
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
            Log.d(TAG, "POST response  - " + result);  // POST response = result..json(String)
            //mTextViewResult.setText(result); // 응답 json String 출력.
            //파싱전
            testJSONParsing(result); // String result json ->> json object ->> testList<json>
            //파싱후
            for(int i=0; i<testList.size();i++){
                ganfan = ganfan + testList.get(i).getTest1() + "   "
                                + testList.get(i).getTest2() + "   "
                                + testList.get(i).getTest3() + "   "
                                + testList.get(i).getTest4() + "\n";
            }
            //mTextTest.setText(ganfan); // 응답 json (key:value) 출력.
            mAdapter.notifyDataSetChanged();
        }

    }

    private static void testJSONParsing(String json) // Json 포맷의 String형 변수를 넘겨줌.
    {
        try{
            JSONObject jsonObject = new JSONObject(json); // POST response -> JSONObject

            JSONArray testArray = jsonObject.getJSONArray("Test"); // take JSONArray -> if name "Test"

            for(int i=0; i<testArray.length(); i++)
            {
                JSONObject movieObject = testArray.getJSONObject(i);
                Test test = new Test();

                test.setTest1(movieObject.getString("name"));
                Log.d(TAG, i + " - name response  - " + test.getTest1());
                test.setTest2(movieObject.getString("job"));
                Log.d(TAG, i + " - job response  - " + test.getTest2());
                test.setTest3(movieObject.getString("many"));
                Log.d(TAG, i + " - many response  - " + test.getTest3());
                test.setTest4(movieObject.getString("end"));
                Log.d(TAG, i + " - end response  - " + test.getTest4());

                testList.add(test);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}