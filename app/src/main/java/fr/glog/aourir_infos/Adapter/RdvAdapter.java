package fr.glog.aourir_infos.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.glog.aourir_infos.R;
import fr.glog.aourir_infos.RdvActivity;
import fr.glog.aourir_infos.model.Rdv;

public class RdvAdapter extends RecyclerView.Adapter<RdvAdapter.ViewHolder> {

    private Context mContext;
    private List<Rdv> mRdv;
    DatabaseReference mDatabase;
   // public List<String> list = new ArrayList<>();
    public HashMap<String,String> hashMap;





    public RdvAdapter(Context mContext, List<Rdv> mRdv) {
        this.mContext = mContext;
        this.mRdv = mRdv;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rdv_item, parent,false);



        return new RdvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final  Rdv rdv= mRdv.get(position);
        holder.Title.setText(rdv.title);
        holder.date.setText(rdv.date);
        holder.adress.setText(rdv.adress);


        Random random = new Random();
        int red = random.nextInt(255);
        int green= random.nextInt(255);
        int blue = random.nextInt(255);
        holder.RdvIcon.setBorderColor(Color.argb(255,red,green,blue));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RdvActivity.class);
                intent.putExtra( "rdvid",rdv.id_rdv);
                intent.putExtra( "rvdtype",rdv.Type);
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                delete_Rdv(rdv, holder);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRdv.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Title;
        public CircleImageView RdvIcon;
        public  TextView date;
        public  TextView adress;
        public ImageView checkbox;




        public  ViewHolder(View itemView){
            super(itemView);

            Title = itemView.findViewById(R.id.Title_Rdv);
            RdvIcon = itemView.findViewById(R.id.image_Rdv_item);
            date= itemView.findViewById(R.id.date_Rdv);
            adress= itemView.findViewById(R.id.adress_Rdv);
            checkbox = itemView.findViewById(R.id.selectedRDV);



        }
    }

    public  void delete_Rdv(final Rdv rdv, final ViewHolder holder){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (rdv.Type.equals("comittee")){

            mDatabase = FirebaseDatabase.getInstance().getReference("RdvComittee").child(rdv.id_rdv);
        }else if (rdv.Type.equals("publique")){
            mDatabase = FirebaseDatabase.getInstance().getReference("RdvPublic").child(rdv.id_rdv);
        }
        if (firebaseUser.getUid().equals(rdv.owner)){
            holder.checkbox.setVisibility(View.VISIBLE);
            AlertDialog.Builder alerteDialog = new AlertDialog.Builder(mContext);
            alerteDialog.setTitle("Delete the appointment!!");
            alerteDialog.setIcon(R.drawable.ic_delete_note);
            alerteDialog.setMessage("would you like to delete the appointment ?");
            alerteDialog.setCancelable(false);
            alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDatabase.removeValue();
                    Toast.makeText(mContext, "appointment "+rdv.getText()+" deleted .",Toast.LENGTH_LONG).show();

                }
            });
            alerteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(mContext, "Undo", Toast.LENGTH_LONG).show();
                    holder.checkbox.setVisibility(View.INVISIBLE);



                }
            });

            AlertDialog alertDialogfinal = alerteDialog.create();
            alertDialogfinal.show();
        }else {

        }


    }



}