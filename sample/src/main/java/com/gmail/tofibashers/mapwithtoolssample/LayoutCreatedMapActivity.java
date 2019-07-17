package com.gmail.tofibashers.mapwithtoolssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gmail.tofibashers.mapwithtools.MapWithToolsView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LayoutCreatedMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapWithToolsView mapWithToolsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_created_map);
        mapWithToolsView = findViewById(R.id.map_with_tools_default);
    }

    @Override
    public void onDestroy(){
        mapWithToolsView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapWithToolsView.onResume();
    }

    @Override
    public void onPause() {
        mapWithToolsView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapWithToolsView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapWithToolsView.onStart();
    }

    @Override
    public void onStop() {
        mapWithToolsView.onStop();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34.0, 151.0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
