package fr.glog.aourir_infos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
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
import java.util.HashMap;
import java.util.List;

import fr.glog.aourir_infos.Adapter.participantAdapter;
import fr.glog.aourir_infos.model.User;


public class DialogueParticipant extends AppCompatDialogFragment {

    public DialogueParticipant(HashMap<String, String> hashMap, String Type) {
        this.hashMap = hashMap;
        this.Type = Type;
    }

    public DialogueParticipant() {
    }

    public  HashMap<String, String> hashMap;
    public String Type;
    private RecyclerView recyclerView;
    private fr.glog.aourir_infos.Adapter.participantAdapter participantAdapter ;
    private List<User> mUser;
    private ProgressBar progressBar;
    EditText searchUsers;
    public  DialogueParticipantInterface listner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialogue,null);
        recyclerView = view.findViewById(R.id.usersListD);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        searchUsers =view.findViewById(R.id.search_barD);
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
        String textDialouge ="";
        readUsers();

        if (hashMap==null){
            textDialouge = "Add";
            builder.setView(view)
                    .setTitle("Add participators")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })

                    .setPositiveButton(textDialouge, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            HashMap<String,String> mapUsers;
                            mapUsers = participantAdapter.hashMap;
                            mapUsers.put("invite_0",firebaseUser.getUid());
                            listner.ApplyAdds(mapUsers);
                        }

                    });
        }else {

            String id_owner = (String)hashMap.get("invite_0");
            if (id_owner.equals(firebaseUser.getUid())){
                textDialouge = "Edit";
                builder.setView(view)
                        .setTitle("Edit the list of participators")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .setPositiveButton(textDialouge, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                HashMap<String,String> mapUsers;
                                mapUsers = participantAdapter.hashMap;
                                listner.ApplyAdds(mapUsers);
                            }

                        });

            }else {
                builder.setView(view)
                        .setTitle("List of participators")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String,String> mapUsers;
                                mapUsers = participantAdapter.hashMap;
                                listner.ApplyAdds(mapUsers);
                            }
                        });
            }

        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listner = (DialogueParticipantInterface) context;
    }

    public interface DialogueParticipantInterface{
        void ApplyAdds(HashMap<String, String> hashMap);
    }

    private void Search_users(String s) {

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
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
                            if (Type.equals("comittee")){
                                if (user.admin.equals("True")){
                                    mUser.add(user);
                                }
                            }else if(Type.equals("publique")){
                                mUser.add(user);
                            }
                        }
                    }
                }

                participantAdapter = new participantAdapter(getContext(),mUser,hashMap);
                recyclerView.setAdapter(participantAdapter);
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchUsers.getText().toString().equals("")){

                    mUser.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        assert user!= null;
                        assert firebaseUser!= null;
                        if(!firebaseUser.getUid().equals(user.id_user)) {
                            if (user.count_stat.equals("Yes")){
                                mUser.add(user);
                            }
                        }
                    }

                    participantAdapter = new participantAdapter(getContext(),mUser, hashMap);
                    recyclerView.setAdapter(participantAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
