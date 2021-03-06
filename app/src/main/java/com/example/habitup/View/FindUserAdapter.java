package com.example.habitup.View;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitup.Controller.FollowController;
import com.example.habitup.Controller.HabitUpApplication;
import com.example.habitup.Model.UserAccount;
import com.example.habitup.Model.UserAccountList;
import com.example.habitup.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Used in the Find User view for showing user matches.
 *
 * Created by barboza on 11/25/17.
 */

public class FindUserAdapter extends RecyclerView.Adapter<FindUserAdapter.SearchResultHolder> {

    private ArrayList<UserAccount> resultsList;
    private Context context;

    public static class SearchResultHolder extends RecyclerView.ViewHolder {
        CircleImageView resultPhoto;
        TextView resultNickName;
        TextView resultName;
        Button followButton;

        public SearchResultHolder(View itemView) {
            super(itemView);
            resultPhoto = itemView.findViewById(R.id.search_photo);
            resultNickName = itemView.findViewById(R.id.search_nick_name);
            resultName = itemView.findViewById(R.id.search_name);
            followButton = itemView.findViewById(R.id.follow_button);
        }

        public void bind(UserAccount result) {
            if (result.getPhoto() != null) {
                try {
                    resultPhoto.setImageBitmap(result.getPhoto());
                } catch (Exception e) {
                    Log.i("Error:", "Failed to load photo for " + result.getUsername());
                }
            }

            resultNickName.setText(result.getRealname());
            resultName.setText(result.getUsername());

            Boolean visible = true;

            try {
                UserAccountList requestList = result.getRequestList();
                if (requestList.contains(HabitUpApplication.getCurrentUser().getUsername())) {
                    disableButton();
                    visible = false;
                } else {
                    enableButton();
                }
            } catch (Exception e) {
                Log.i("Error:", "Could not get request list for " + result.getUsername());
            }

            if (visible) {
                try {
                    UserAccountList friendList = HabitUpApplication.getCurrentUser().getFriendsList();
                    if (friendList.contains(result.getUsername())) {
                        disableButton();
                    } else {
                        enableButton();
                    }
                } catch (Exception e) {
                    Log.i("Error:", "Could not get friends list for " + result.getUsername());
                }
            }
        }

        public void disableButton() {
            this.followButton.setVisibility(View.INVISIBLE);
        }

        public void enableButton() {
            this.followButton.setVisibility(View.VISIBLE);
        }
    }

    public FindUserAdapter(Context context, ArrayList<UserAccount> resultsList) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_card, viewGroup, false);
        SearchResultHolder srh = new SearchResultHolder(v);
        return srh;
    }

    @Override
    public void onBindViewHolder(final SearchResultHolder holder, final int position) {
        final UserAccount user = resultsList.get(position);
        holder.bind(user);

        // If follow button is clicked
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount currentUser = HabitUpApplication.getCurrentUser();
                String name = user.getRealname();

                if (currentUser.getUID() == user.getUID()) {
                    // Check if requesting to follow the current user
                    Toast.makeText(context, "You cannot follow yourself", Toast.LENGTH_LONG).show();
                } else if (currentUser.getFriendsList().contains(user.getUsername())) {
                    // Check if already following
                    Toast.makeText(context, "You are already following " + name, Toast.LENGTH_LONG).show();
                } else if (user.getRequestList().contains(currentUser.getUsername())) {
                    // Check if already sent request
                    Toast.makeText(context, "You already sent a request to " + name, Toast.LENGTH_LONG).show();
                } else {
                    // Send friend request to user
                    FollowController.addFriendRequest(user, currentUser);
                    Toast.makeText(context, "A request was sent to " + name, Toast.LENGTH_LONG).show();

                    holder.disableButton();
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }
}
