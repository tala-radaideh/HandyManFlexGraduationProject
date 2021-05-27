package com.example.handymanflexpartserver.ui.most_popular;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Model.BestDealsModel;
import com.example.handymanflexpartserver.Model.MostPopularModel;
import com.example.handymanflexpartserver.callback.IBestDealsCallbackListener;
import com.example.handymanflexpartserver.callback.IMostPopularCallbackListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MostPopularViewModel extends ViewModel implements IMostPopularCallbackListener {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<MostPopularModel>> mostPopularListMutable;
    private IMostPopularCallbackListener mostPopularCallbackListener ;

    public MostPopularViewModel() { mostPopularCallbackListener = this; }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public MutableLiveData<List<MostPopularModel>> getMostPopularListMutable() {
        if(mostPopularListMutable ==null)
            mostPopularListMutable= new MutableLiveData<>();
        loadMostPopular();
        return mostPopularListMutable;
    }

    public void loadMostPopular() {

        List<MostPopularModel> temp = new ArrayList<>();
        DatabaseReference mostPopularRef= FirebaseDatabase.getInstance()
                .getReference(Common.MOST_POPULAR);
        mostPopularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mostPopularSnapShot : snapshot.getChildren())
                {
                    MostPopularModel mostPopularModel= mostPopularSnapShot.getValue(MostPopularModel.class);
                    mostPopularModel.setKey(mostPopularSnapShot.getKey());
                    temp.add(mostPopularModel);
                }
                mostPopularCallbackListener.onListMostPopularLoadSuccess(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostPopularCallbackListener.onListMostPopularLoadFailed(error.getMessage());
            }
        });
    }

    @Override
    public void onListMostPopularLoadSuccess(List<MostPopularModel> mostPopularModels) {
        mostPopularListMutable.setValue(mostPopularModels);
    }

    @Override
    public void onListMostPopularLoadFailed(String message) {
     messageError.setValue(message);
    }
}
