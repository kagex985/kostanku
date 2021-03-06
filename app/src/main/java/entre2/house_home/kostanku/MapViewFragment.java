package entre2.house_home.kostanku;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Vector;

import entre2.house_home.kostanku.controllers.KosController;
import entre2.house_home.kostanku.models.Kos;
import entre2.house_home.kostanku.utilities.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback{

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleMap mGoogleMap;
    MapView mMapView;
    private Location location;
    View mView;
    private final LatLng mDefaultLocation = new LatLng(-6.201873, 106.781891);
    GPSTracker gpsTracker;
    private boolean mLocationPermissionGranted;
    private LatLng currLocation;
    private Location mLastKnownLocation;
    private Vector<Kos>kosan;
    private HashMap<Marker, String> mHashMap;
    private Marker selectedMarker;

    public MapViewFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.map);
        if(mMapView !=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

            gpsTracker = new GPSTracker(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_map_view,container,false);

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){

        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.addMarker(new MarkerOptions().position(mDefaultLocation));

//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(monas));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        getCurrentLocation();
        addProjectMarker();
        clickMarker();
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    private void getCurrentLocation(){
        try {
            location = gpsTracker.getLocation();

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            currLocation = new LatLng(latitude, longitude);

            drawCurrRadius(latitude, longitude);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currLocation));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        }catch (Exception e){
            Log.e("location", String.valueOf(e));
        }
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void drawCurrRadius(double latitude, double longitude) {

        Circle circle = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(500)
                .strokeColor(0xffff0000)
                .strokeWidth(1)
                .fillColor(0x44ff0000));
    }

    private void getProject(){
        KosController.getInstance();
        kosan = KosController.getAll();
    }

    private void addProjectMarker(){
        getProject();
//        Log.v("TEST","Kos"+kosan.get(0).getName());
        for (Kos kos:kosan) {
            isInDistance(kos.getLatitude(), kos.getLongitude(), kos.getName(), kos.getId());
        }

    }

    private void isInDistance(double targetLat, double targetLong, String projectName, String key){

        float[] dist = new float[1];

        Location.distanceBetween(location.getLatitude(), location.getLongitude(), targetLat, targetLong, dist);

        if(dist[0]/800 <= 1) {
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(targetLat, targetLong))
                    .title(projectName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            mHashMap.put(marker, key);
        }
    }

    private void clickMarker(){
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(selectedMarker != null && marker.toString().equals(selectedMarker.toString())) {
                    String key = mHashMap.get(marker);
                    Intent intent = new Intent(getContext(),KostDetailActivity.class);
                    intent.putExtra("id",key);
                    startActivity(intent);
                }
                selectedMarker = marker;
                return false;
            }
        });
    }
}
