package com.gmail.tofibashers.mapwithtoolssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.gmail.tofibashers.mapwithtools.DrawingToolsBar;
import com.gmail.tofibashers.mapwithtools.MapWithToolsView;
import com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView;
import com.gmail.tofibashers.mapwithtools.tool_view.MapControlToolView;
import com.gmail.tofibashers.mapwithtools.tool_view.PolylineToolView;
import com.gmail.tofibashers.mapwithtools.tool_view.RectangleToolView;
import com.gmail.tofibashers.mapwithtools.zoom.UIZoomControlsType;
import com.gmail.tofibashers.mapwithtools.zoom.ZoomRangeBarView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BuilderCreatedMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapWithToolsView mapWithToolsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_builder_created_map);

        mapWithToolsView = new MapWithToolsView.Builder(this)
                .setZoomControlsType(UIZoomControlsType.WITH_LEVELS)
                .setDrawingToolsBar(new DrawingToolsBar.Builder(this)
                        .addTool(new MapControlToolView.Builder(this))
                        .addTool(new PolylineToolView.Builder(this)
                                .setFigureColorRes(R.color.tealA900))
                        .addTool(new RectangleToolView.Builder(this)
                                .setFigureStrokeColorRes(R.color.tealA900))
                        .addTool(new CircleToolView.Builder(this)
                                .setFigureFillColorRes(R.color.yellowA200)))
                .setZoomLevelsView(new ZoomRangeBarView.Builder(this)
                        .setLevelsCount(3))
                .build();

        ((LinearLayout) findViewById(R.id.mapContainer))
                .addView(mapWithToolsView,
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

        mapWithToolsView.onCreate(savedInstanceState);
        mapWithToolsView.getMapAsync(this);
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
