package com.example.pi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class selectOptionActivity extends AppCompatActivity{

    EditText feedback = null;
    int choice = 0, minPos = 0;
    String municipalityMails[] = {"bhattacharya.sayan30@gmail.com", "subhayanghosh2424@gmail.com"};
    Location municipalityLatLng[] = new Location[2];
    private FusedLocationProviderClient fusedLocationClient;
    String currentAddress;
    boolean locationSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //setting locations manually THIS IS A TEMPORARY PROCESS
        for(int i = 0;i < municipalityLatLng.length;i++)
            municipalityLatLng[i] = new Location("Municipality" + i);
        municipalityLatLng[0].setLatitude(22.48648);
        municipalityLatLng[0].setLongitude(88.2794);

        municipalityLatLng[1].setLatitude(20.2961);
        municipalityLatLng[1].setLongitude(85.8245);
        //DELETE UP TO THIS LATER

        final Button municipality = findViewById(R.id.municipality);
        final Button electricityOffice = findViewById(R.id.electricityOffice);
        final Button vet = findViewById(R.id.vet);
        final Button send = findViewById(R.id.send);

        feedback = findViewById(R.id.feedback);

        municipality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 1;
                municipality.setBackgroundResource(R.drawable.buttons_selected);
                electricityOffice.setBackgroundResource(R.drawable.buttons);
                vet.setBackgroundResource(R.drawable.buttons);
            }
        });

        electricityOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;
                municipality.setBackgroundResource(R.drawable.buttons);
                electricityOffice.setBackgroundResource(R.drawable.buttons_selected);
                vet.setBackgroundResource(R.drawable.buttons);
            }
        });

        vet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 3;
                municipality.setBackgroundResource(R.drawable.buttons);
                electricityOffice.setBackgroundResource(R.drawable.buttons);
                vet.setBackgroundResource(R.drawable.buttons_selected);
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

    }

    private void getLocation()
    {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (location != null) {
                                locationSet = true;
                                if(choice == 1)
                                {
                                    double min = location.distanceTo(municipalityLatLng[0]);
                                    for(int i = 1;i < municipalityLatLng.length;i++)
                                    {
                                        if(location.distanceTo(municipalityLatLng[i]) < min){
                                            min = location.distanceTo((municipalityLatLng[i]));
                                            minPos = i;
                                        }
                                    }
                                }
                                currentAddress = getAddress(location);
                            }
                            else
                            {
                                AlertDialog alert = new AlertDialog.Builder(selectOptionActivity.this).create();
                                alert.setTitle("Location Permission");
                                alert.setMessage("Please turn on your mobile's location before sending the problem.");
                                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            }
                        }
                    });
    }

    private void sendMail() {
        String receiverMail = "";
        String subject = "";
        getLocation();
        if(locationSet) {
            if(choice == 1) {
                receiverMail = municipalityMails[minPos];
                subject = "Problem in locality";
            }
            else if(choice == 2)
            {
                //receicerMail = electricOfficeMails[minPos];
                //subject = "Electricity Problem in locality";
            }
            else if(choice == 3)
            {
                //receiverMail = vetMails[minPos];
                //subject = "Injured animal in locality";
            }
            if(!receiverMail.equals("")) {
                String message = feedback.getText().toString() + '\n' + "Address of incident: -" + '\n' + currentAddress;
                Intent intent = getIntent();
                String imagePath = intent.getExtras().getString("imagePath");
                JavaMailAPI mailAPI = new JavaMailAPI(this, receiverMail, subject, imagePath, message);
                mailAPI.execute();
            }
            else
            {
                AlertDialog alert = new AlertDialog.Builder(selectOptionActivity.this).create();
                alert.setTitle("Error");
                alert.setMessage("Please select to whom the problem should be sent.");
                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        }


    }

    private String getAddress(Location l)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
        }catch(IOException e){
            e.printStackTrace();
        }
        String addressLine = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();

        String fullAddress = addressLine; //+ ", "+ city + ", " + state + ", " + country + ", Pincode-" + postalCode;
        return fullAddress;
    }

}
