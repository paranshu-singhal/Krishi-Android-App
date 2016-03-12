package com.majors.paranshusinghal.krishi;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private static final String logTag = "myTAG";

    protected EditText mPerson;
    protected EditText mPassword;
    protected EditText mPhone;
    protected EditText mAddress1;
    protected EditText mAddress2;
    protected EditText mCity;
    protected EditText mState;
    protected EditText mCountry;

    private String mCountryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mPerson   = (EditText)findViewById(R.id.editTextPerson);
        mPassword = (EditText)findViewById(R.id.editTextPassword);
        mPhone    = (EditText)findViewById(R.id.editTextPhone);
        mAddress1 = (EditText)findViewById(R.id.editTextAddress1);
        mAddress2 = (EditText)findViewById(R.id.editTextAddress2);
        mCity    = (EditText)findViewById(R.id.editTextCity);
        mState    = (EditText)findViewById(R.id.editTextState);
        mCountry  = (EditText)findViewById(R.id.editTextCountry);

        mPerson.setError(null);
        mPassword.setError(null);
        mPhone.setError(null);
        mAddress1.setError(null);
        mAddress2.setError(null);
        mCity.setError(null);
        mState.setError(null);
        mCountry.setError(null);

        Bundle bundle = getIntent().getExtras();
        final String tag =bundle.getString("tag");
        Log.d(logTag, tag);

        Button mSubmitButton = (Button) findViewById(R.id.buttonSubmit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean success = AttemptRegister(mPerson.getText().toString(), mPassword.getText().toString(), mPhone.getText().toString(),
                        mAddress1.getText().toString(), mAddress2.getText().toString(), mCity.getText().toString(), mState.getText().toString(), mCountry.getText().toString());

                if(success) {
                    NewRegister reg = new NewRegister(mPerson.getText().toString(), mPassword.getText().toString(), mPhone.getText().toString(),
                            mAddress1.getText().toString(), mAddress2.getText().toString(),mCity.getText().toString() ,mState.getText().toString(),  mCountryString,tag);
                    reg.execute((Void) null);
                }
            }
        });
    }

    private boolean AttemptRegister(String name, String password, String phone, String address1, String address2,String city ,String state, String country){
        if(name.length()<=0){
            mPerson.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if(phone.length()!=10){
            mPhone.setError(getResources().getString(R.string.error_field));
            return false;
        }
        if(password.length()<5){
            mPassword.setError(getResources().getString(R.string.minimum_length_req));
            return false;
        }
        if(address1.length()<=0){
            mAddress1.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if(address2.length()<=0){
            mAddress2.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if(city.length()<=0){
            mCity.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if(state.length()<=0){
            mState.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if(country.length()<=0){ mCountryString = "India"; }
        else{ mCountryString = country; }
        return true;
    }

    private class NewRegister extends AsyncTask<Void,Void,String>{

        private String name;
        private String password;
        private String phone;
        private String address1;
        private String address2;
        private String City;
        private String state;
        private String country;
        private String tag;

        NewRegister(String name, String password, String phone, String address1, String address2, String city, String state, String country, String tag) {
            this.name = name;
            this.password = password;
            this.phone = phone;
            this.address1 = address1;
            this.address2 = address2;
            this.state = state;
            this.country = country;
            this.tag = tag;
            this.City=city;
        }

        @Override
        protected String doInBackground(Void... params) {

            String answer = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) (new URL("http://10.0.0.5/krishi/index.php")).openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String values = "tag=" + URLEncoder.encode("register", "UTF-8") +
                        "&name=" + URLEncoder.encode(name, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8") +
                        "&address1=" + URLEncoder.encode(address1, "UTF-8") +
                        "&address2=" + URLEncoder.encode(address2, "UTF-8") +
                        "&city=" + URLEncoder.encode(City, "UTF-8") +
                        "&state=" + URLEncoder.encode(state, "UTF-8") +
                        "&country=" + URLEncoder.encode(country, "UTF-8") +
                        "&occupation=" + URLEncoder.encode(tag, "UTF-8");

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(values);
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.d(logTag, String.format("code %d", conn.getResponseCode()));

                StringBuilder buffer = new StringBuilder();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }

                is.close();
                conn.disconnect();
                answer = buffer.toString();
            } catch (Throwable t) {
                Log.d(logTag, t.getMessage());
            }
            return answer;
        }
        @Override
        protected void onPostExecute(String answer) {
            Log.d(logTag, answer);
            try {
                JSONObject json = new JSONObject(answer);
                if(json.getString("success").equals("1")){
                    ContentValues values = new ContentValues();

                    values.put("unique_id", json.getJSONObject("user").getString("uid"));
                    values.put("name", name);
                    values.put("phone_no", phone);
                    values.put("password", password);
                    values.put("address1", address1);
                    values.put("address2", address2);
                    values.put("city", City);
                    values.put("state", state);
                    values.put("country", country);
                    values.put("tag", tag);

                    String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
                    Uri uriusers = Uri.parse("content://"+PROVIDER_NAME+"/users");
                    Uri uri = getContentResolver().insert(uriusers,values);
                    Log.d(logTag, uri.toString());
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_registration_successful), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(mPerson, json.getString("error_msg"), Snackbar.LENGTH_SHORT).show();
                }
            }
            catch (Throwable t){
                t.printStackTrace();
                Log.d(logTag, t.getMessage());
            }
        }
    }
}
