package com.ucd.pepeclub.exerciseapp;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    Context context;
    private List<User> userList;



    public LeaderboardAdapter(Context context, List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    public List<User> getUserList(){
            return userList;
    }


    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LeaderboardViewHolder viewHolder = new LeaderboardViewHolder(inflater.inflate(R.layout.friends_listview_layout, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder viewHolder, int position) {
        User user = userList.get(position);
        System.out.println(user.getName()+" "+user.getId() + " "+user.getPoints());
        //viewHolder.profilePic.setProfileId(user.getId());
        Picasso.with(context).load("https://graph.facebook.com/"+user.getId()+"/picture?type=large").into(viewHolder.profilePic);
        viewHolder.rank.setText(user.getRank());
        viewHolder.name.setText(user.getName());
        viewHolder.points.setText(user.getPoints());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView rank, name, points;
        ImageView profilePic;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.rank);
            name = itemView.findViewById(R.id.name);
            points = itemView.findViewById(R.id.points);
            profilePic = itemView.findViewById(R.id.friends_pic);
        }
    }

}
