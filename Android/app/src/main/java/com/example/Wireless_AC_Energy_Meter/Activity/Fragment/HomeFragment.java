package com.example.Wireless_AC_Energy_Meter.Activity.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.Wireless_AC_Energy_Meter.Activity.MainActivity;
import com.example.Wireless_AC_Energy_Meter.Data.State;
import com.example.Wireless_AC_Energy_Meter.Helper.AppController;
import com.example.Wireless_AC_Energy_Meter.Helper.ConnectionConfig;
import com.example.Wireless_AC_Energy_Meter.Helper.SQLiteHandler;
import com.example.Wireless_AC_Energy_Meter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteHandler db;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        progressDialog = new ProgressDialog(getActivity());

        //get all data from server
        getAllData();
        getState();

        return view;
    }

    private  void getAllData() {
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Getting All Data");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.GET_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonDayArray = jsonObject.getJSONArray("Days");
                    JSONArray jsonPeriodArray = jsonObject.getJSONArray("Periods");
                    JSONArray jsonWeekArray = jsonObject.getJSONArray("Weeks");
                    JSONArray jsonMonthArray = jsonObject.getJSONArray("Months");
                    ArrayList<String> energy = new ArrayList<>();
                    ArrayList<String> power = new ArrayList<>();
                    ArrayList<String> current = new ArrayList<>();
                    ArrayList<String> voltage = new ArrayList<>();
                    ArrayList<String> powerFactor = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();
                    ArrayList<String> period = new ArrayList<>();
                    //DAYS
                    for (int i = 0; i < jsonDayArray.length(); i++) {
                        energy.add(jsonDayArray.getJSONObject(i).getString("Energy"));
                        power.add(jsonDayArray.getJSONObject(i).getString("Power"));
                        current.add(jsonDayArray.getJSONObject(i).getString("Current"));
                        voltage.add(jsonDayArray.getJSONObject(i).getString("Voltage"));
                        powerFactor.add(jsonDayArray.getJSONObject(i).getString("Power_Factor"));
                        time.add(jsonDayArray.getJSONObject(i).getString("Day"));
                    }
                    db.deleteDays();
                    for (int i = 0; i < jsonDayArray.length(); i++) {
                        db.addDay(energy.get(i), power.get(i), current.get(i), voltage.get(i), powerFactor.get(i), time.get(i));
                    }
                    energy.clear();
                    power.clear();
                    current.clear();
                    voltage.clear();
                    powerFactor.clear();
                    time.clear();
                    //PERIODS
                    for (int i = 0; i < jsonPeriodArray.length(); i++) {
                        energy.add(jsonPeriodArray.getJSONObject(i).getString("Energy"));
                        power.add(jsonPeriodArray.getJSONObject(i).getString("Power"));
                        current.add(jsonPeriodArray.getJSONObject(i).getString("Current"));
                        voltage.add(jsonPeriodArray.getJSONObject(i).getString("Voltage"));
                        powerFactor.add(jsonPeriodArray.getJSONObject(i).getString("Power_Factor"));
                        time.add(jsonPeriodArray.getJSONObject(i).getString("Day"));
                        period.add(jsonPeriodArray.getJSONObject(i).getString("Period"));

                    }
                    db.deletePeriods();
                    for (int i = 0; i < jsonPeriodArray.length(); i++) {
                        db.addPeriod(energy.get(i), power.get(i), current.get(i), voltage.get(i), powerFactor.get(i), time.get(i), period.get(i));
                    }
                    energy.clear();
                    power.clear();
                    current.clear();
                    voltage.clear();
                    powerFactor.clear();
                    time.clear();
                    //WEEKS
                    for (int i = 0; i < jsonWeekArray.length(); i++) {
                        energy.add(jsonWeekArray.getJSONObject(i).getString("Energy"));
                        power.add(jsonWeekArray.getJSONObject(i).getString("Power"));
                        current.add(jsonWeekArray.getJSONObject(i).getString("Current"));
                        voltage.add(jsonWeekArray.getJSONObject(i).getString("Voltage"));
                        powerFactor.add(jsonWeekArray.getJSONObject(i).getString("Power_Factor"));
                        time.add(jsonWeekArray.getJSONObject(i).getString("Week"));
                    }
                    db.deleteWeeks();
                    for (int i = 0; i < jsonWeekArray.length(); i++) {
                        db.addWeek(energy.get(i), power.get(i), current.get(i), voltage.get(i), powerFactor.get(i), time.get(i));
                    }
                    energy.clear();
                    power.clear();
                    current.clear();
                    voltage.clear();
                    powerFactor.clear();
                    time.clear();
                    //MONTHS
                    for (int i = 0; i < jsonMonthArray.length(); i++) {
                        energy.add(jsonMonthArray.getJSONObject(i).getString("Energy"));
                        power.add(jsonMonthArray.getJSONObject(i).getString("Power"));
                        current.add(jsonMonthArray.getJSONObject(i).getString("Current"));
                        voltage.add(jsonMonthArray.getJSONObject(i).getString("Voltage"));
                        powerFactor.add(jsonMonthArray.getJSONObject(i).getString("Power_Factor"));
                        time.add(jsonMonthArray.getJSONObject(i).getString("Month"));
                    }
                    db.deleteMonths();
                    for (int i = 0; i < jsonMonthArray.length(); i++) {
                        db.addMonth(energy.get(i), power.get(i), current.get(i), voltage.get(i), powerFactor.get(i), time.get(i));
                    }
                    Toast.makeText(getActivity(), "All Table has been Created", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "+" + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("code", "getData");
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    private void getState() {
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Getting All Data");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.GET_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonStartObject = jsonObject.getJSONObject("Start");
                    JSONObject jsonEndObject = jsonObject.getJSONObject("End");
                    JSONObject jsonStateObject = jsonObject.getJSONObject("State");

                    State.START_ID = jsonStartObject.getInt("ID");
                    State.START_MONDAY = jsonStartObject.getString("Monday").substring(0, 8 - 3);
                    State.START_TUESDAY = jsonStartObject.getString("Tuesday").substring(0, 8 - 3);
                    State.START_WEDNESDAY = jsonStartObject.getString("Wednesday").substring(0, 8 - 3);
                    State.START_THURSDAY = jsonStartObject.getString("Thursday").substring(0, 8 - 3);
                    State.START_FRIDAY = jsonStartObject.getString("Friday").substring(0, 8 - 3);
                    State.START_SATURDAY = jsonStartObject.getString("Saturday").substring(0, 8 - 3);
                    State.START_SUNDAY = jsonStartObject.getString("Sunday").substring(0, 8 - 3);

                    State.END_ID = jsonEndObject.getInt("ID");
                    State.END_MONDAY = jsonEndObject.getString("Monday").substring(0, 8 - 3);
                    State.END_TUESDAY = jsonEndObject.getString("Tuesday").substring(0, 8 - 3);
                    State.END_WEDNESDAY = jsonEndObject.getString("Wednesday").substring(0, 8 - 3);
                    State.END_THURSDAY = jsonEndObject.getString("Thursday").substring(0, 8 - 3);
                    State.END_FRIDAY = jsonEndObject.getString("Friday").substring(0, 8 - 3);
                    State.END_SATURDAY = jsonEndObject.getString("Saturday").substring(0, 8 - 3);
                    State.END_SUNDAY = jsonEndObject.getString("Sunday").substring(0, 8 - 3);

                    State.STATE_ID = jsonStateObject.getInt("ID");
                    State.STATE_PRICE = jsonStateObject.getDouble("Price");
                    State.STATE_STATE = jsonStateObject.getInt("State");
                    State.STATE_TYPE = jsonStateObject.getString("Type");
                    State.STATE_LIMIT = jsonStateObject.getInt("Energy_Limit");
                    State.STATE_EXCEEDED = jsonStateObject.getInt("Exceeded");



                    Toast.makeText(getActivity(), "State has been updated", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "+" + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("code", "getState");
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }


    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
