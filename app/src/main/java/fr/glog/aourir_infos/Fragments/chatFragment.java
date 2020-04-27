package fr.glog.aourir_infos.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

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
import java.util.List;

import fr.glog.aourir_infos.Adapter.userAdapter;
import fr.glog.aourir_infos.R;
import fr.glog.aourir_infos.model.User;

public class chatFragment extends Fragment {
    private RecyclerView recyclerView;
    private userAdapter userAdap;
    private List<User> mUser;
    private ProgressBar progressBar;
    EditText searchUsers;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.usersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchUsers =view.findViewById(R.id.search_bar);
        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Search_users(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUser = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBarUsers);
        progressBar.setVisibility(View.VISIBLE);
        readUsers();

        return view;
    }

    private void Search_users(String s) {

        final  FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search")
                        .startAt(s)
                        .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert fUser != null;
                    if (!fUser.getUid().equals(user.id_user)) {
                        if (user.count_stat.equals("Yes")){
                            mUser.add(user);
                        }
                    }
                }

                userAdap = new userAdapter(getContext(),mUser, false);
                recyclerView.setAdapter(userAdap);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private  void readUsers(){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchUsers.getText().toString().equals("")){

                    mUser.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        User user = snapshot.getValue(User.class);
                        assert user!= null;
                        assert firebaseUser!= null;
                        if(!firebaseUser.getUid().equals(user.id_user)){
                            if (user.count_stat.equals("Yes")){
                                mUser.add(user);
                            }
                        }

                    }

                    userAdap = new userAdapter(getContext(),mUser, false);
                    recyclerView.setAdapter(userAdap);
                    progressBar.setVisibility(View.GONE);


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}