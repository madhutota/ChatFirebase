package com.dev.chatfirebase.activities;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.chatfirebase.R;
import com.dev.chatfirebase.activities.utils.UiIMageLoader;
import com.dev.chatfirebase.activities.utils.Utility;
import com.dev.chatfirebase.models.ChatModel;
import com.dev.chatfirebase.models.MapModel;
import com.dev.chatfirebase.models.MessageModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class MessageFirebaseAdapter extends FirebaseRecyclerAdapter<MessageModel, MessageFirebaseAdapter.ChatViewHolder> {

    private ClickListenerChatFirebase mClickListenerChatFirebase;

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;
    private String nameUser;
    ChatActivity chatActivity;

    public MessageFirebaseAdapter(DatabaseReference ref, String nameUser, ClickListenerChatFirebase mClickListenerChatFirebase, ChatActivity chatActivity) {
        super(MessageModel.class, R.layout.item_message_left, MessageFirebaseAdapter.ChatViewHolder.class, ref);
        this.nameUser = nameUser;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
        this.chatActivity = chatActivity;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new ChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel model = getItem(position);
        if (model.getMapModel() != null) {
            if (model.getFrom().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getFileModel() != null) {
            if (model.getFileModel().getType().equals("image") && model.getFrom().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getFrom().equals(nameUser)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }
    }

    @Override
    protected void populateViewHolder(ChatViewHolder viewHolder, MessageModel model, int position) {
        // viewHolder.setIvUser(model.getUserModel().getProfile_image());
        viewHolder.setTxtMessage(model.getMessage());
        viewHolder.tvIsLocation(View.GONE, null);
        if (model.getFileModel() != null) {
            viewHolder.tvIsLocation(View.GONE, null);
            viewHolder.setIvChatPhoto(model.getFileModel().getUrl_file());
        } else if (model.getMapModel() != null) {
            viewHolder.setIvChatPhoto(Utility.local(model.getMapModel().getLatitude(), model.getMapModel().getLongitude()));
            viewHolder.tvIsLocation(View.VISIBLE, model.getMapModel());
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTimestamp, tvLocation;
        EmojiconTextView txtMessage;
        ImageView ivUser, ivChatPhoto;


        public ChatViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView) itemView.findViewById(R.id.img_chat);
            ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MessageModel model = getItem(position);
            if (model.getMapModel() != null) {
                mClickListenerChatFirebase.clickImageMapChat(view, position, model.getMapModel().getLatitude(), model.getMapModel().getLongitude());
            } else {
                mClickListenerChatFirebase.clickImageChat(view, position, model.getFrom(), model.getFileModel().getUrl_file(), model.getFileModel().getUrl_file());
            }
        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            if (urlPhotoUser != null) {
                Picasso.with(ivUser.getContext()).
                        load(urlPhotoUser).centerCrop().resize(40, 40)
                        .placeholder(R.drawable.default_avatar).into(ivUser);
            }





            /*Glide.with(ivUser.getContext()).load(urlPhotoUser).
                    transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);*/
        }

        public void setTvTimestamp(Long timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(converteTimestamp(timestamp));
        }

        public void setIvChatPhoto(String url) {
            if (ivChatPhoto == null) return;
/*
            Glide.with(mContext).load(imgUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            Picasso.with(ivChatPhoto.getContext()).load(url).centerCrop().resize(100, 100)
                    .placeholder(R.drawable.default_avatar).into(ivChatPhoto);*/

            UiIMageLoader.URLpicLoading(ivChatPhoto, url, null, R.drawable.default_avatar);

            ivChatPhoto.setOnClickListener(this);
        }

        public void tvIsLocation(int visible, MapModel mapModel) {
            if (tvLocation == null) return;
            tvLocation.setVisibility(visible);
            if (mapModel != null) {
                tvLocation.setText(chatActivity.getResources().getString(R.string.address_text, mapModel.getPlace_name(), mapModel.getPlace_address(),
                        System.currentTimeMillis()));
            }
        }
    }

    private CharSequence converteTimestamp(Long mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(mileSegundos, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }


}
