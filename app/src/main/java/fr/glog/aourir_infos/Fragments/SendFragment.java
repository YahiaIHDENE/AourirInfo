package fr.glog.aourir_infos.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.glog.aourir_infos.Adapter.userAdapter;
import fr.glog.aourir_infos.R;
import fr.glog.aourir_infos.model.Chatlist;
import fr.glog.aourir_infos.model.User;


public class SendFragment extends Fragment {

    private RecyclerView recyclerView_send;
    private userAdapter userAdapter1;
    private List<User> mUser;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    String userID;

    private List<Chatlist> listUsers;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send, container, false);
        progressBar = view.findViewById(R.id.progressBar_profile);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView_send =view.findViewById(R.id.RecyclerView_send);
        recyclerView_send.setHasFixedSize(true);
        recyclerView_send.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listUsers = new ArrayList<>();

        userID = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    listUsers.add(chatlist);

                    }
                Chat_list();
                progressBar.setVisibility(View.GONE);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       // updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }


    /*private void  updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }*/

    private  void  Chat_list(){

        mUser = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : listUsers){
                        if (user.id_user.equals(chatlist.getId())){
                            if (user.count_stat.equals("Yes")){
                                mUser.add(user);
                            }

                        }
                    }
                }

                userAdapter1 = new userAdapter(getContext(), mUser, true);
                recyclerView_send.setAdapter(userAdapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}