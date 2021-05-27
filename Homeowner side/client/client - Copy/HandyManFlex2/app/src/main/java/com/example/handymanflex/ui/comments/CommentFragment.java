package com.example.handymanflex.ui.comments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.CommentModel;
import com.example.handymanflex.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyCommentAdapter;
import com.example.handymanflex.Callback.ICommentCallBackListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CommentFragment extends BottomSheetDialogFragment implements ICommentCallBackListener {
    private CommentViewHolder commentViewHolder;
    private Unbinder unbinder;
    @BindView(R.id.recycler_comment)
    RecyclerView recyclercomment;
    AlertDialog alertDialog;
    ICommentCallBackListener listener;
    private static CommentFragment instance;

    public static CommentFragment getInstance() {
        if (instance == null)
            instance = new CommentFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_comment_fragment, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        initViews();
        LoadCommentFromFireBase();
        return itemView;

    }

    private void LoadCommentFromFireBase() {
        alertDialog.show();
        //check order bty child by timestamp not found in firebase
        List<CommentModel> commentModelList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
                .child(Common.selectedService.getId()).limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot commentsnapshot : snapshot.getChildren()) {
                    CommentModel commentModel = commentsnapshot.getValue(CommentModel.class);
                    commentModelList.add(commentModel);

                }listener.OnCommentLoadSuccuss(commentModelList);
                commentViewHolder.getMutableLiveDataServiceList().observe(getViewLifecycleOwner(), commentModels -> {
                    MyCommentAdapter myCommentAdapter = new MyCommentAdapter(getContext(),commentModelList);
                    recyclercomment.setAdapter(myCommentAdapter);
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.OnCommentLoadFailed(error.getMessage());
            }
        });
    }

    private void initViews() {
        commentViewHolder = new ViewModelProvider(requireActivity()).get(CommentViewHolder.class);
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        recyclercomment.setHasFixedSize(true);
        LinearLayoutManager layoutManger = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        recyclercomment.setLayoutManager(layoutManger);
        recyclercomment.addItemDecoration(new DividerItemDecoration(getContext(), layoutManger.getOrientation()));

    }

    public CommentFragment() {
        listener = this;
    }

    @Override
    public void OnCommentLoadSuccuss(List<CommentModel> commentModelList) {
        alertDialog.dismiss();
commentViewHolder.setCommentList(commentModelList);
    }

    @Override
    public void OnCommentLoadFailed(String message) {
        alertDialog.dismiss();
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();

    }
}
