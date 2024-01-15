package com.example.dungeonstroll;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExploreActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap gMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Marker> markers = new ArrayList<>();
    Marker locationMarker;
    private static final int REQUEST_CODE = 1;
    private boolean firstEncounter;
    private String dungeonName;
    private SQLiteManager sqLiteManager;
    private LocationRequest locationRequest;
    private Boolean zoomed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        registerReceiver(broadcast, new IntentFilter("finish_explore_activity"));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ExploreActivity.this);
        sqLiteManager = SQLiteManager.instanceOfDatabase(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getMyLocation();
    }

    private BroadcastReceiver broadcast = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_explore_activity")) {
                finish();
            }
        }
    };


    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                addLocationMarker();
                if(!zoomed){
                    zoomLocationMarker();
                }
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        addDungeonsMarkers();
        gMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTitle().equals("Dungeon")) {
            if(checkLocation(marker.getPosition())){
                dungeonName = marker.getSnippet().toLowerCase().replaceAll(" ", "_");
                gMap.clear();
                markers.clear();
                addLocationMarker();
                addEncountersMarkers(marker.getSnippet());
                firstEncounter = true;
                sqLiteManager.changeAchievmentToLost();
                sqLiteManager.addAchievment(new Achievment(Calendar.getInstance().getTime(), dungeonName,1));
            }
             return true;
        }else if(marker.getTitle().equals("Encounter")){
            if(checkLocation(marker.getPosition())) {
                Intent intent = new Intent(ExploreActivity.this, EncounterActivity.class);
                marker.remove();
                markers.remove(marker);
                if (firstEncounter) {
                    intent.putExtra("EncounterNr", "First");
                    firstEncounter = false;
                } else if (markers.size() == 0) {
                    intent.putExtra("EncounterNr", "Last");
                } else {
                    intent.putExtra("EncounterNr", "None");
                }
                intent.putExtra("DungeonName", dungeonName);
                startActivity(intent);
                if (markers.size() == 0) {
                    finish();
                }
            }
            return true;
        }
        return false;
    }

    private void addDungeonsMarkers() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.dungeons);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.trim().split(":");
                markers.add(gMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(s[0]),Double.parseDouble(s[1]))).title("Dungeon").snippet(s[2]).icon(BitmapDescriptorFactory.fromBitmap(newBitmap("gate")))));
            }
            reader.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("NULLPOINTER");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO");
        }catch(NumberFormatException e){
            e.printStackTrace();
            System.out.println("NUMBERFORMAT");
        }
    }

    private void addEncountersMarkers(String location){
        location = location.toLowerCase().replaceAll(" ", "_");
        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(location, "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] s = line.trim().split(":");
                markers.add(gMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(s[0]),Double.parseDouble(s[1]))).title("Encounter").icon(BitmapDescriptorFactory.fromBitmap(newBitmap("questionmark")))));
            }
            reader.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("NULLPOINTER");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO");
        }catch(NumberFormatException e){
            e.printStackTrace();
            System.out.println("NUMBERFORMAT");
        }

    }

    private void addLocationMarker(){
        if(locationMarker!=null){
            locationMarker.remove();
        }
        locationMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Current Location").icon(BitmapDescriptorFactory.fromBitmap(newBitmap("silhouette"))));
    }

    private void zoomLocationMarker(){
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        zoomed = true;
    }
    public Bitmap newBitmap(String name){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(name, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);
    }

    private Boolean checkLocation(LatLng latLng){
        Location tempLocation = new Location("");
        tempLocation.setLatitude(latLng.latitude);
        tempLocation.setLongitude(latLng.longitude);
        if(currentLocation.distanceTo(tempLocation) < 30){
            return true;
        }
        return false;
    }
}