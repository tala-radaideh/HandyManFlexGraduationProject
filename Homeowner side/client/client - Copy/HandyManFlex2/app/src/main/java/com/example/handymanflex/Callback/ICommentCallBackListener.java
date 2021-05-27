package com.example.handymanflex.Callback;

import com.example.handymanflex.Model.CommentModel;

import java.util.List;

public interface ICommentCallBackListener {
    void OnCommentLoadSuccuss(List<CommentModel> commentModelList);
    void OnCommentLoadFailed(String message);
}
