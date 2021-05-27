package com.example.handymanflex.ui.Serviceslist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.example.handymanflex.ChatActivity;
import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.CategoryModel;
import com.example.handymanflex.Model.ServiceModel;
import com.example.handymanflex.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyServiceListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import EventBus.MenuItemBack;
public class serviceslistFragment extends Fragment {

    private serviceslistViewModel serviceslistViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_services_list)
    RecyclerView recycler_services_list;
    MyServiceListAdapter adapter;
    LayoutAnimationController layoutAnimationController;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serviceslistViewModel =
                new ViewModelProvider(this).get(serviceslistViewModel.class);
        View root = inflater.inflate(R.layout.fragment_services_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        serviceslistViewModel.getMutableLiveDataservicelist().observe(getViewLifecycleOwner(), serviceModels -> {
           if(serviceModels !=null)
           {
               adapter = new MyServiceListAdapter(getContext(),serviceModels);
               recycler_services_list.setAdapter(adapter);
               recycler_services_list.setLayoutAnimation(layoutAnimationController);
           }

        });

        return root;
    }

    private void initViews() {
        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        setHasOptionsMenu(true);
        recycler_services_list.setHasFixedSize(true);
        recycler_services_list.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);



    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        // Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Clear text when click to clear button on search view
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view -> {
            EditText ed= (EditText) searchView.findViewById(R.id.search_src_text);

            //Clear text
            ed.setText("");

            //Clear query
            searchView.setQuery(" ",false);

            //Collapse the action view
            searchView.onActionViewCollapsed();

            //Collapse the search widget
            menuItem.collapseActionView();

            //Restore result to original
            serviceslistViewModel.getMutableLiveDataservicelist();

        });

    }

    private void startSearch(String query) {
        List<ServiceModel> resultList = new ArrayList<>();
        for(int i=0; i<Common.categorySelected.getServices().size(); i++)
        {
            ServiceModel serviceModel = Common.categorySelected.getServices().get(i);
            if(serviceModel.getName().toLowerCase().contains(query))
                resultList.add(serviceModel);
        }
        serviceslistViewModel.getMutableLiveDataservicelist().setValue(resultList);
    }

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

}