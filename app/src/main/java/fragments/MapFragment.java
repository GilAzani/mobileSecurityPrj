package fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.racehw1.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.racehw1.databinding.FragmentMapBinding;

import java.util.ArrayList;

import model.RecordHolder;
import utils.SignalGenerator;

public class MapFragment extends Fragment {

    private ArrayList <RecordHolder>  recordHolders;
    private MarkerOptions [] markerOptions;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap1) {
                googleMap = googleMap1;
                setMap();
            }
        });

        return view;
    }

    public void zoom(int index) {
        if (index < recordHolders.size()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions[index].getPosition(), 15));
        }
        else{
            SignalGenerator.getInstance().toast("there is no record!");
        }
    }

    public void setRecordHolders(ArrayList<RecordHolder> recordHolders) {
        //googleMap.clear();
        this.recordHolders = recordHolders;
    }
        private void setMap(){
            markerOptions = new MarkerOptions[recordHolders.size()];
            for (int i = 0; i < markerOptions.length; i++) {
                markerOptions[i] = new MarkerOptions();
                markerOptions[i].position(new LatLng(recordHolders.get(i).getLatitude(),recordHolders.get(i).getLongitude()));
                googleMap.addMarker(markerOptions[i]);
            }

        }
    }
