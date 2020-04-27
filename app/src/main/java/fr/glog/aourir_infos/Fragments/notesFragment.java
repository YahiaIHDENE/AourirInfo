package fr.glog.aourir_infos.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.glog.aourir_infos.Adapter.RdvAdapter;
import fr.glog.aourir_infos.NewRdvActivity;
import fr.glog.aourir_infos.R;
import fr.glog.aourir_infos.model.Rdv;

public class notesFragment extends Fragment {

    private RecyclerView recyclerView_Rdv;
    private RdvAdapter rdvAdapter;
    private List<Rdv> mRdv;
    EditText searchRdv;
    TextView noarticle;
    ProgressBar progressBar;

    ImageButton btn_add;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        progressBar = view.findViewById(R.id.progressBarUsers);
        progressBar.setVisibility(View.VISIBLE);


        btn_add = view.findViewById(R.id.addNoteBtn);
        noarticle = view.findViewById(R.id.noarticle);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("admin").getValue().toString().equals("False")){
                    btn_add.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noarticle.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getActivity(), NewRdvActivity.class);
                intent.putExtra( "rvdtype","comittee");
                startActivity(intent);

            }
        });



        recyclerView_Rdv = view.findViewById(R.id.noteslist);
        recyclerView_Rdv.setHasFixedSize(true);
        recyclerView_Rdv.setLayoutManager(new LinearLayoutManager(getContext()));

        searchRdv =view.findViewById(R.id.search_bar_note);
        searchRdv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Search_Rdv(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRdv = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBarUsers);
        progressBar.setVisibility(View.VISIBLE);
        Read_Rdv();
        return view ;
    }

    private void Search_Rdv(String s) {

        final  FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("RdvComittee").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRdv.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rdv rdv = snapshot.getValue(Rdv.class);
                    assert rdv != null;
                    assert fUser != null;
                    if (fUser.getUid().equals(rdv.owner)) {
                        mRdv.add(rdv);
                    }
                }

                rdvAdapter = new RdvAdapter(getContext(),mRdv);
                recyclerView_Rdv.setAdapter(rdvAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Read_Rdv() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RdvComittee");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchRdv.getText().toString().equals("")){
                    mRdv.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Rdv rdv = snapshot.getValue(Rdv.class);
                        assert rdv!= null;
                        assert firebaseUser!= null;
                        for (Map.Entry mapentry : rdv.particip.entrySet()) {
                            if(firebaseUser.getUid().equals(mapentry.getValue())){
                                mRdv.add(rdv);
                            }
                        }

                    }

                    Collections.reverse(mRdv);
                    if (mRdv.isEmpty()){
                        noarticle.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                    rdvAdapter = new RdvAdapter(getContext(),mRdv);
                    recyclerView_Rdv.setAdapter(rdvAdapter);
                    progressBar.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}