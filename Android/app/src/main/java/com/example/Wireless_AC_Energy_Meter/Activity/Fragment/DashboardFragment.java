package com.example.Wireless_AC_Energy_Meter.Activity.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    //widges , varabile
    private TextView clicked;
    Button btn_start, btn_stop, btn_limit, btn_clear;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    GridLayout gridLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        showTime(view);
        return view;
    }

    private void showTime(View view) {
        gridLayout = view.findViewById(R.id.TimeGRID);
        btn_start = view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(buttonListener);
        btn_stop = view.findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(buttonListener);
        btn_limit = view.findViewById(R.id.btn_limit);
        btn_limit.setOnClickListener(buttonListener);
        btn_limit.setText("Limit: " + State.STATE_LIMIT + " kWh");
        btn_clear = view.findViewById(R.id.btn_Clear);
        btn_clear.setOnClickListener(buttonListener);

        progressDialog = new ProgressDialog(getActivity().getApplicationContext());


        //Manual
        CheckBox check_type = view.findViewById(R.id.check_type);
        if (State.STATE_TYPE.equals("Auto")){
            btn_start.setEnabled(false);
            btn_stop.setEnabled(false);
            check_type.setChecked(true);
        } else {
            enableDisableView(gridLayout, false);
            btn_start.setEnabled(true);
            btn_stop.setEnabled(true);
            check_type.setChecked(false);
        }

        check_type.setOnCheckedChangeListener(checkedChangeListener);


        progressDialog = new ProgressDialog(getActivity());
        // SQLite database handler

        //monday, tuesday, wednesday, thursday, friday, saturday, sunday

        TextView start_monday = view.findViewById(R.id.start_monday);
        start_monday.setText(State.START_MONDAY);
        TextView start_tuesday = view.findViewById(R.id.start_tuesday);
        start_tuesday.setText(State.START_TUESDAY);
        TextView start_wednesday = view.findViewById(R.id.start_wednesday);
        start_wednesday.setText(State.START_WEDNESDAY);
        TextView start_thursday = view.findViewById(R.id.start_thursday);
        start_thursday.setText(State.START_THURSDAY);
        TextView start_friday = view.findViewById(R.id.start_friday);
        start_friday.setText(State.START_FRIDAY);
        TextView start_saturday = view.findViewById(R.id.start_saturday);
        start_saturday.setText(State.START_SATURDAY);
        TextView start_sunday = view.findViewById(R.id.start_sunday);
        start_sunday.setText(State.START_SUNDAY);

        TextView end_monday = view.findViewById(R.id.end_monday);
        end_monday.setText(State.END_MONDAY);
        TextView end_tuesday = view.findViewById(R.id.end_tuesday);
        end_tuesday.setText(State.END_TUESDAY);
        TextView end_wednesday = view.findViewById(R.id.end_wednesday);
        end_wednesday.setText(State.END_WEDNESDAY);
        TextView end_thursday = view.findViewById(R.id.end_thursday);
        end_thursday.setText(State.END_THURSDAY);
        TextView end_friday = view.findViewById(R.id.end_friday);
        end_friday.setText(State.END_FRIDAY);
        TextView end_saturday = view.findViewById(R.id.end_saturday);
        end_saturday.setText(State.END_SATURDAY);
        TextView end_sunday = view.findViewById(R.id.end_sunday);
        end_sunday.setText(State.END_SUNDAY);

        for (int i = 0; i < gridLayout.getChildCount(); i++){
            TextView textView = (TextView) gridLayout.getChildAt(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked = (TextView) v;
                    String tag = v.getTag().toString();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeListner, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            Calendar.getInstance().get(Calendar.MINUTE), true );
                    switch (tag) {
                        case "time":
                            timePickerDialog.show();break;
                        case "text":
                            Toast.makeText(getActivity(), "click on time please", Toast.LENGTH_SHORT).show();break;
                        default:break;
                    }
                }
            });
        }

        if (State.STATE_EXCEEDED == 1) {
            new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exceed the Limit")
                    .setMessage("what do you want to do")
                    .setPositiveButton("Change the limit", dClickListener)//-1
                    .setNegativeButton("Nothing", dClickListener)//-2
                    .setNeutralButton("Reset energy", dClickListener)
                    .show();
        }


    }

    private DialogInterface.OnClickListener dClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    show();break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(getActivity(), Integer.toString(which), Toast.LENGTH_SHORT).show();break;
                case DialogInterface.BUTTON_NEUTRAL:
                    clearLimitData();break;
            }
        }
    };

    TimePickerDialog.OnTimeSetListener timeListner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String timePicked = "";
            if (hourOfDay < 9 && minute < 9)
                timePicked = "0" + hourOfDay + ":" + "0" + minute;
            else if (hourOfDay < 9 && minute > 9)
                timePicked = "0" + hourOfDay + ":" + minute;
            else if (hourOfDay > 9 && minute < 9)
                timePicked = hourOfDay + ":" + "0" + minute;
            else
                timePicked = hourOfDay + ":" + minute;
            //Toast.makeText(getActivity(), timePicked, Toast.LENGTH_SHORT).show();
            clicked.setText(timePicked);

            switch (clicked.getId()) {
                case R.id.start_monday:
                    State.START_MONDAY=timePicked;
                    break;
                case R.id.start_tuesday:
                    State.START_TUESDAY=timePicked;
                    break;
                case R.id.start_wednesday:
                    State.START_WEDNESDAY=timePicked;
                    break;
                case R.id.start_thursday:
                    State.START_THURSDAY=timePicked;
                    break;
                case R.id.start_friday:
                    State.START_FRIDAY=timePicked;
                    break;
                case R.id.start_saturday:
                    State.START_SATURDAY=timePicked;
                    break;
                case R.id.start_sunday:
                    State.START_SUNDAY=timePicked;
                    break;
                case R.id.end_monday:
                    State.END_MONDAY=timePicked;
                    break;
                case R.id.end_tuesday:
                    State.END_TUESDAY=timePicked;
                    break;
                case R.id.end_wednesday:
                    State.END_WEDNESDAY=timePicked;
                    break;
                case R.id.end_thursday:
                    State.END_THURSDAY=timePicked;
                    break;
                case R.id.end_friday:
                    State.END_FRIDAY=timePicked;
                    break;
                case R.id.end_saturday:
                    State.END_SATURDAY=timePicked;
                    break;
                case R.id.end_sunday:
                    State.END_SUNDAY=timePicked;
                    break;
                default:
                    Toast.makeText(getActivity(), "else", Toast.LENGTH_SHORT).show();break;
            }
            uploadTimeData();
        }
    };

    Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_start:State.STATE_STATE=1;uploadStateData();break;
                case R.id.btn_stop:State.STATE_STATE=0;uploadStateData();break;
                case R.id.btn_limit:show();break;
                case R.id.btn_Clear:clearLimitData();break;
                default:break;
            }
        }
    };


    int newNumber;
    private void show(){
        NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMaxValue(100);
        if (State.STATE_EXCEEDED == 1){
            numberPicker.setMinValue(State.STATE_LIMIT + 1);
        }
        else
            numberPicker.setMinValue(0);

        numberPicker.setValue(State.STATE_LIMIT + 1);
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Toast.makeText(getActivity(), "Changed", Toast.LENGTH_SHORT).show();
                newNumber = newVal;
            }
        };
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(numberPicker);
        builder.setTitle("Change limit")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "ok " + newNumber, Toast.LENGTH_SHORT).show();
                        //oldNumber = newNumber;
                        if (newNumber == 0)
                            State.STATE_LIMIT = -1;
                        else
                            State.STATE_LIMIT = newNumber;
                        btn_limit.setText("Limit: " + State.STATE_LIMIT + " kWh");
                        uploadLimitData();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "cancel " + oldNumber, Toast.LENGTH_SHORT).show();

                    }
                }).show();
    }


    CheckBox.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //Auto
            if (isChecked){
                State.STATE_TYPE = "Auto";
                enableDisableView(gridLayout, true);
                btn_start.setEnabled(false);
                btn_stop.setEnabled(false);
            } else {
                State.STATE_TYPE = "Manual";
                btn_start.setEnabled(true);
                btn_stop.setEnabled(true);
                enableDisableView(gridLayout, false);
            }
            uploadTypeData();
        }
    };

    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    private void uploadTimeData(){
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Update Time");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.TIME_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (!error)
                        Toast.makeText(getActivity(), "Time has been updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Please try again leater", Toast.LENGTH_SHORT).show();


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
                //monday, tuesday, wednesday, thursday, friday, saturday, sunday

                Map<String, String> params = new HashMap<>();
                params.put("start_id", Integer.toString(State.START_ID));
                params.put("start_monday", State.START_MONDAY+":00");
                params.put("start_tuesday", State.START_TUESDAY+":00");
                params.put("start_wednesday", State.START_WEDNESDAY+":00");
                params.put("start_thursday", State.START_THURSDAY+":00");
                params.put("start_friday", State.START_FRIDAY+":00");
                params.put("start_saturday", State.START_SATURDAY+":00");
                params.put("start_sunday", State.START_SUNDAY+":00");

                params.put("end_id", Integer.toString(State.END_ID));
                params.put("end_monday", State.END_MONDAY+":00");
                params.put("end_tuesday", State.END_TUESDAY+":00");
                params.put("end_wednesday", State.END_WEDNESDAY+":00");
                params.put("end_thursday", State.END_THURSDAY+":00");
                params.put("end_friday", State.END_FRIDAY+":00");
                params.put("end_saturday", State.END_SATURDAY+":00");
                params.put("end_sunday", State.END_SUNDAY+":00");
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    private void uploadTypeData(){
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Update type");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.TYPE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        State.STATE_TYPE = jsonObject.getString("type");
                        Toast.makeText(getActivity(), "type updated to " + State.STATE_TYPE, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();


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
                params.put("type", State.STATE_TYPE);
                params.put("id", Integer.toString(State.STATE_ID));
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    private void uploadStateData(){
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Update type");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.STATE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        State.STATE_STATE = jsonObject.getInt("state");
                        Toast.makeText(getActivity(), "state update to " + State.STATE_STATE, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();


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
                params.put("state", Integer.toString(State.STATE_STATE));
                params.put("id", Integer.toString(State.STATE_ID));
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    private void uploadLimitData(){
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Update limit");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.LIMIT_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        State.STATE_STATE = jsonObject.getInt("limit");
                        State.STATE_EXCEEDED = jsonObject.getInt("Exceeded");
                        Toast.makeText(getActivity(), "limit update to " + State.STATE_LIMIT, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();


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
                params.put("limit", Double.toString(State.STATE_LIMIT));
                params.put("id", Integer.toString(State.STATE_ID));
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    private void clearLimitData() {
        // Tag used to cancel the request
        String tag_string_req = "getting_data";
        progressDialog.setMessage("Clearing limit");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConnectionConfig.CLEAR_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        State.STATE_EXCEEDED = jsonObject.getInt("Exceeded");
                        Toast.makeText(getActivity(), "Energy has been rest" , Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();


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
                params.put("clear", "0");
                params.put("id", Integer.toString(State.STATE_ID));
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
