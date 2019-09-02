 # MapWithTools

![MapWithTools](/images/sample.png)

**MapWithTools** is a flexible wrapper for android GoogleMaps Api, 
that adds some features:

  * Bar with drawing tools, for user-manual drawing with gestures 
  * Zooming bar with manual setting zoom level
  * Easy initialization from Kotlin, Java and XML 
  
## Install

Add the following to root build.gradle file:

``` groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Then add library to your dependencies in module's build.gradle file:
``` groovy
dependencies {
	implementation 'com.github.TofiBashers:MapWithTools:1.0'
}
```

## Usage

The library support 3 ways for initialization:

In Kotlin file, simple use special DSL:

``` kotlin 
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
```

In .java file, use Builder:
``` java 
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
```
 
If you need fully customized view's, add view with XML:

``` xml
<com.gmail.tofibashers.mapwithtools.MapWithToolsView 
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    app:uiZoomControlsType="withLevels" >

    <com.gmail.tofibashers.mapwithtools.DrawingToolsBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal" >

        <com.gmail.tofibashers.mapwithtools.tool_view.RectangleToolView
            android:layout_width="10dp"
            android:layout_height="10dp"
            app:strokeColor="@color/tealA900" />

        <com.gmail.tofibashers.mapwithtools.tool_view.CircleToolView
            android:layout_width="10dp"
            android:layout_height="10dp"
            app:strokeColor="@color/yellowA200" />

    </com.gmail.tofibashers.mapwithtools.DrawingToolsBar>

    <com.gmail.tofibashers.mapwithtools.zoom.ZoomRangeBarView
        android:layout_width="10dp"
        android:layout_height="40dp"
        android:orientation="vertical"
        android:layout_gravity="bottom|right"
        app:levelsCount="7"/>
</com.gmail.tofibashers.mapwithtools.MapWithToolsView>

```

 Also, MapWithToolsView must be override same lifecycle
 events as GoogleMaps MapView. For full sample of usage,
 see sample.
 

## In next release: 
  * Support of modification currently drawed figures
  * Automatically set size MapWithTools child components (drawing and zoom bars)