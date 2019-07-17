package com.gmail.tofibashers.mapwithtoolssample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.gmail.tofibashers.mapwithtools.*
import com.gmail.tofibashers.mapwithtools.zoom.UIZoomControlsType

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DslBuilderCreatedMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapWithToolsView: MapWithToolsView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dslbuilder_created_map)

        mapWithToolsView = mapWithToolsView(this) {
            zoomControlsType = UIZoomControlsType.WITH_LEVELS
            drawingToolsBar {
                mapControlTool {}
                polylineTool {
                    figureColorRes = R.color.tealA900
                }
                rectangleTool {
                    figureStrokeColorRes = R.color.tealA900
                }
                circleTool {
                    figureFillColorRes = R.color.yellowA200
                }
            }
            zoomRangeBar {
                levelsCount = 3
            }

        }

        findViewById<LinearLayout>(R.id.mapContainer)
                .addView(mapWithToolsView, LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT))

        mapWithToolsView.onCreate(savedInstanceState)
        mapWithToolsView.getMapAsync(this)
    }

    override fun onDestroy(){
        mapWithToolsView.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapWithToolsView.onResume()
    }

    override fun onPause() {
        mapWithToolsView.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapWithToolsView.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        mapWithToolsView.onStart()
    }

    override fun onStop() {
        mapWithToolsView.onStop()
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
