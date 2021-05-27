package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handymanflex.Model.CommentModel;
import com.example.handymanflex.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.MyViewHolder> {
    Context context;
    List<CommentModel> commentModelList;

    public MyCommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_comment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
holder.comment_per_user.setText(commentModelList.get(position).getComment());
holder.comment_name.setText(commentModelList.get(position).getName());
holder.rating_bar_per_user.setRating(commentModelList.get(position).getRatingValue());
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
@BindView(R.id.comment_name)
        TextView comment_name;
        @BindView(R.id.rating_bar_per_user)
        RatingBar rating_bar_per_user;
        @BindView(R.id.comment_per_user)
        TextView comment_per_user;





        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);

        }
    }
}
