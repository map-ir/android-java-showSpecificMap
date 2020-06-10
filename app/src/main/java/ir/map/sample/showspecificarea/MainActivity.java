package ir.map.sample.showspecificarea;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_map.MapirStyle;
import ir.map.sdk_map.maps.MapView;

import static okhttp3.internal.Util.UTF_8;

public class MainActivity extends AppCompatActivity {

    MapboxMap map;
    Style mapStyle;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setStyle(new Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapStyle = style;

                        addPolygon();
                    }
                });
            }
        });
    }

    private void addPolygon() {
        List<List<Point>> allPointList = new ArrayList<>();

        //region create world polygon
        List<Point> worldPoints = new ArrayList<>();

        worldPoints.add(Point.fromLngLat(180, -90));
        worldPoints.add(Point.fromLngLat(180, 90));
        worldPoints.add(Point.fromLngLat(-180, 90));
        worldPoints.add(Point.fromLngLat(-180, -90));
        worldPoints.add(Point.fromLngLat(180, -90));

        allPointList.add(worldPoints);
        //endregion create world polygon

        //region Add source to map
        String cityFeatureCollection = readJSONFromAsset("tehran_polygon").toString();

        allPointList.add(((Polygon) FeatureCollection.fromJson(cityFeatureCollection).features().get(0).geometry()).coordinates().get(0));

        Polygon polygon = Polygon.fromLngLats(allPointList);

        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(polygon));
        GeoJsonSource geoJsonSource = new GeoJsonSource("sample_source_id", featureCollection);

        mapStyle.addSource(geoJsonSource);
        //endregion Add source to map

        //region  Add layer to map
        FillLayer fillLayer = new FillLayer("sample_layer_id", "sample_source_id");
        fillLayer.setProperties(PropertyFactory.fillColor(Color.WHITE));

        mapStyle.addLayer(fillLayer);
        //endregion  Add layer to map

        //region Bind view to polygon region
        LatLng BOUND_CORNER_NW = new LatLng(36.20, 50.25);
        LatLng BOUND_CORNER_SE = new LatLng(34.82, 53.27);
        LatLng CENTER_POLYGON = new LatLng(35.61711, 51.83349);
        LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
                .include(BOUND_CORNER_NW)
                .include(BOUND_CORNER_SE)
                .build();

        map.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);
        map.setMinZoomPreference(6.4);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(CENTER_POLYGON, 6.4));
        //endregion Bind view to polygon region
    }

    public JSONObject readJSONFromAsset(String fileName) {
        try {
            InputStream is = getAssets().open(fileName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer, UTF_8));
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}