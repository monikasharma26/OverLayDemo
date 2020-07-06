package com.example.overlaydemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final int REQUEST_CODE = 1;

    private Marker homeMarker;
    private Marker destMarker;

    Polyline polyline;
    Polygon polygon;
    public static final int POLYGON_SIDES = 3;
    List<Marker> markersList = new ArrayList();

    //Location with Location manger and listener
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMArker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();

        //Apply Long Press gesture
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
         //       Location location = new Location("Your Destination");
           //     location.setLatitude(latLng.latitude);
             //   location.setLongitude(latLng.longitude);
                //Set marker
                setMarker(latLng);
            }
            private void setMarker(LatLng latLng) {
              MarkerOptions options = new MarkerOptions().position(latLng)
                      .title("Your Destination");

          /*    if(destMarker != null) clearMap();

              destMarker = mMap.addMarker(options);
              drawLine();*/
          if(markersList.size() == POLYGON_SIDES)
              clearMap();
          markersList.add(mMap.addMarker(options));

            }
            private void drawLine() {
                PolylineOptions options = new PolylineOptions()
                                .color(Color.BLACK)
                        .width(10)
                        .add(homeMarker.getPosition(),destMarker.getPosition());
             polyline =    mMap.addPolyline(options);
             if(markersList.size() == POLYGON_SIDES)
                 drawShape();
            }

            private void drawShape() {
                PolygonOptions options = new PolygonOptions()
                        .fillColor(Color.GRAY)
                        .strokeColor(Color.RED)
                        .strokeWidth(5);
                for (int i = 0; i<POLYGON_SIDES; i++){
                    options.add(markersList.get(i).getPosition());
                }
                polygon = mMap.addPolygon(options);
            }

            private void clearMap() {
             /*   if(destMarker != null){
                    destMarker.remove();
                    destMarker = null;

                }*/
                //Remove Line from map
             // polyline.remove();

                for(Marker marker: markersList)
                    marker.remove();

                markersList.clear();
                polygon.remove();
                polygon = null;
            }

        });
    }





    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 0, locationListener);
    ///Show Last Location
    /*    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMArker(lastKnownLocation);*/
    }

    private void setHomeMArker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
       homeMarker =  mMap.addMarker(markerOptions);
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
       
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(ActivityCompat.checkSelfPermission
                    (this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 0, locationListener);
            }
        }
    }
}