package com.majors.paranshusinghal.krishi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "com.majors.paranshusinghal.krishi";
    private static final String TAGlog = "myTAG";

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.phone);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mNewUserButton = (Button) findViewById(R.id.new_register_button);
        mNewUserButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogInterface();
                    }
                });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String text) {
        return (text.matches("[0-9]+") && text.length()==10);
    }
    private boolean isPasswordValid(String password) {
        return password.length() >= 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
        Uri uri = Uri.parse("content://"+PROVIDER_NAME+"/numbers");
        return new CursorLoader(this,uri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> phones = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            phones.add(cursor.getString(cursor.getColumnIndex("number")));
            cursor.moveToNext();
        }
        addPhonesToAutoComplete(phones);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addPhonesToAutoComplete(List<String> phonesCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, phonesCollection);
        //Log.d(TAGlog,phonesCollection.toString());
        mEmailView.setAdapter(adapter);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mPhone;
        private final String mPassword;

        UserLoginTask(String Phone, String password) {
            mPhone = Phone;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {

            String localhost = getResources().getString(R.string.localhost);
            String answer=null;
            try{
                HttpURLConnection conn = (HttpURLConnection) (new URL(localhost+"/krishi/index.php")).openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String values = "tag=login"+
                        "&phone="+ URLEncoder.encode(mPhone,"UTF-8")+
                        "&password="+URLEncoder.encode(mPassword,"UTF-8");

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(values);
                dataOutputStream.flush();
                dataOutputStream.close();

                //Log.d(TAGlog, String.format("code %d",conn.getResponseCode()));

                StringBuilder buffer = new StringBuilder();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while (  (line = br.readLine()) != null ) {
                    buffer.append(line);
                }

                is.close();
                conn.disconnect();
                answer = buffer.toString();
            }
            catch (Throwable t){
                t.printStackTrace();
                Log.d(TAGlog,t.getMessage());
            }
            return answer;
        }

        @Override
        protected void onPostExecute(final String answer) {

            //Log.d(TAGlog, answer);
            String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
            mAuthTask = null;
            showProgress(false);
            JSONObject json;
            try {
                json = new JSONObject(answer);
                if(json.getString("success").equals("1")){
                    ContentValues values = new ContentValues();
                    values.put("number", json.getJSONObject("user").getString("phone_no"));
                    Uri uri = Uri.parse("content://"+PROVIDER_NAME+"/numbers");
                    getContentResolver().insert(uri,values);

                    addUserSqlite(json);
                    //Log.d(TAGlog, uri.toString());

                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.user_login_successful), Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("phone", mPhone);
                    Intent intent;
                    switch (json.getJSONObject("user").getString("tag"))
                    {
                        case "Buyer":
                            intent = new Intent(LoginActivity.this, BuyerDashboardActivity.class);
                            break;
                        default:
                            intent = new Intent(LoginActivity.this, SellerDashboardActivity.class);
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    try {
                        mPasswordView.setError(json.getString("error_msg"));
                        mPasswordView.requestFocus();
                    }
                    catch (Throwable t){
                        t.printStackTrace();
                        Log.d(TAGlog, t.getMessage());
                    }
                }
            }
            catch (Throwable t){
                t.printStackTrace();
                Log.d(TAGlog, t.getMessage());
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void dialogInterface() {

        final String array[] = {"Farmer", "Buyer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(R.string.pick_one)
                    .setItems(array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAGlog, String.format("%d", which));
                            Bundle bundle = new Bundle();
                            bundle.putString("tag", array[which]);
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            intent.putExtra("callingActivity", "LoginActivity");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }).show();
    }

    public void addUserSqlite(JSONObject json){
        String PROVIDER_NAME = "com.majors.paranshusinghal.krishi.phones";
        try {
            json = json.getJSONObject("user");
            ContentValues vals = new ContentValues();
            vals.put("unique_id", json.getString("uid"));
            vals.put("name", json.getString("name"));
            vals.put("phone_no", json.getString("phone_no"));
            vals.put("address1", json.getString("address1"));
            vals.put("address2", json.getString("address2"));
            vals.put("city", json.getString("city"));
            vals.put("state", json.getString("state"));
            vals.put("country", json.getString("country"));
            vals.put("tag", json.getString("tag"));

            Uri uriusers = Uri.parse("content://"+PROVIDER_NAME+"/users");
            getContentResolver().insert(uriusers,vals);
        }
        catch (Throwable t){
            Log.d(TAGlog, t.getMessage());
            t.printStackTrace();
        }
    }
}


