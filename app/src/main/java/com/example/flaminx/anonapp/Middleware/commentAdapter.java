package com.example.flaminx.anonapp.Middleware;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.flaminx.anonapp.AnonApp;
import com.example.flaminx.anonapp.Pojo.Comment;
import com.example.flaminx.anonapp.Pojo.Post;
import com.example.flaminx.anonapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Flaminx on 30/06/2017.
 */

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder> {


    private ArrayList<Comment> commentObject;
    private int lastObject = -1;
    private int animCutoff;
    private View inflatedView;

    public commentAdapter(ArrayList<Comment> input) {
        animCutoff = input.size();
        commentObject = input;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_row, parent, false);
        return new ViewHolder(inflatedView, lastObject);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Comment rowPost = commentObject.get(position);
        holder.bind(rowPost);
        setAnimation(holder.itemView, position);


    }

    public void addVote(int pos) {
        Comment rowComment = commentObject.get(pos);
        updateScore(rowComment, "1", pos);
        //commentObject.remove(pos);
        //commentObject.add(pos,rowComment);
    }

    public void removeVote(int pos) {
        Comment rowComment = commentObject.get(pos);
        updateScore(rowComment, "0", pos);
        //commentObject.remove(pos);
        //commentObject.add(pos,rowComment);
    }

    public void addAll(ArrayList<Comment> update) {
        lastObject = -1;
        animCutoff = update.size();
        commentObject.addAll(0, update);
        notifyDataSetChanged();
    }

    private void setAnimation(View vta, int pos) {
        if (pos > lastObject && pos < animCutoff) {
            Animation anim = AnimationUtils.loadAnimation(inflatedView.getContext(), android.R.anim.fade_in);
            vta.startAnimation(anim);
            lastObject = pos;
        }
    }


    @Override
    public int getItemCount() {
        return commentObject.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView commentText;
        private TextView commentDate;
        private TextView commentScore;
        private LayerDrawable commentStyle;
        private Comment mComment;
        private ImageView commentImage;
        private View resourceGet;

        public ViewHolder(View v, int count) {
            super(v);

            commentText = (TextView) v.findViewById(R.id.comment_text);
            commentDate = (TextView) v.findViewById(R.id.comment_date);
            commentScore = (TextView) v.findViewById(R.id.comment_score);
            commentStyle = (LayerDrawable) v.getResources().getDrawable(R.drawable.comment_layers);
            commentImage = (ImageView) v.findViewById(R.id.commentstyleimage);
            resourceGet = v;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                v.setElevation(10);
            }
            v.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
            Context context = itemView.getContext();
            mComment.Delete(context, mComment.getCommentId());
            return true;
        }

        public void bind(Comment comment) {
            mComment = comment;
            commentText.setText(comment.getCommentText());
            commentDate.setText(comment.getCommentDate());
            commentScore.setText(Integer.toString(comment.getCommentScore()));

            String cstyle = Integer.toString(comment.getCommentStyle());
            Drawable theDrawable;
            switch (cstyle.charAt(2)) {
                case '1':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.capsule_symbol);
                    break;
                case '2':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.capsule_symbol);
                    break;
                case '3':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.capsule_symbol);
                    break;
                case '4':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.diamond_symbol);
                    break;
                case '5':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.diamond_symbol);
                    break;
                case '6':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.diamond_symbol);
                    break;
                case '7':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.circle_symbol);
                    break;
                case '8':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.circle_symbol);
                    break;
                case '9':
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.circle_symbol);
                    break;
                default:
                    theDrawable = resourceGet.getResources().getDrawable(R.drawable.circle_symbol);
                    break;
            }

            commentStyle.setDrawableByLayerId(R.id.symbol, theDrawable);
            int color = colourSelect(cstyle.charAt(0),true);
            Drawable first = commentStyle.findDrawableByLayerId(R.id.commentcircle);
            first.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            first.mutate();
            color = colourSelect(cstyle.charAt(1),false);
            Drawable second = commentStyle.findDrawableByLayerId(R.id.symbol);
            second.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            second.mutate();
            commentImage.setBackground(commentStyle);
        }

        private int colourSelect(char res, boolean BorS) {
            int color;
            if(BorS) {
                switch (res) {
                    case '1':
                        color = Color.parseColor("#d11141");
                        break;
                    case '2':
                        color = Color.parseColor("#00b159");
                        break;
                    case '3':
                        color = Color.parseColor("#00aedb");
                        break;
                    case '4':
                        color = Color.parseColor("#f37735");
                        break;
                    case '5':
                        color = Color.parseColor("#ffc425");
                        break;
                    case '6':
                        color = Color.parseColor("#a200ff");
                        break;
                    case '7':
                        color = Color.parseColor("#f47835");
                        break;
                    case '8':
                        color = Color.parseColor("#8ec127");
                        break;
                    case '9':
                        color = Color.parseColor("#d41243");
                        break;
                    default:
                        color = Color.parseColor("#d11141");
                        break;
                }

            }
            else
            {
                switch (res) {
                    case '1':
                        color = Color.parseColor("#511b78");
                        break;
                    case '2':
                        color = Color.parseColor("#52c131");
                        break;
                    case '3':
                        color = Color.parseColor("#5fbbf4");
                        break;
                    case '4':
                        color = Color.parseColor("#c8d848");
                        break;
                    case '5':
                        color = Color.parseColor("#ee8a23");
                        break;
                    case '6':
                        color = Color.parseColor("#c5c5c5");
                        break;
                    case '7':
                        color = Color.parseColor("#638d71");
                        break;
                    case '8':
                        color = Color.parseColor("#813b3b");
                        break;
                    case '9':
                        color = Color.parseColor("#44487e");
                        break;
                    default:
                        color = Color.parseColor("#c6c75b");
                        break;
                }
            }
            return color;
        }
    }

    private void updateScore(final Comment comment, String aOrM, final int position) {
        final String addOrRemove = aOrM;
        final String id = AnonApp.getInstance().getUserId();
        final String pid = Integer.toString(comment.getCommentId());
        final String POST_URL = AnonApp.getInstance().getWebAddress() + "/comments/score";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        comment.setCommentScore(Integer.parseInt(response));
                        notifyItemChanged(position);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {



                        if (error instanceof ServerError) {

                            if (error.networkResponse.statusCode == 409) {

                                Toast.makeText(inflatedView.getContext(), R.string.ohMyGodThisShouldntHappen, Toast.LENGTH_LONG).show();
                            } else if (error.networkResponse.statusCode == 400) {
                                Toast.makeText(inflatedView.getContext(), R.string.voteTwice, Toast.LENGTH_LONG).show();
                            }
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(inflatedView.getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
                        }
                        notifyItemChanged(position);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", id);
                params.put("comment_id", pid);
                params.put("AorS", addOrRemove);

                return params;
            }

        };

        AnonApp.getInstance().addToReqQ(stringRequest);
    }


}
