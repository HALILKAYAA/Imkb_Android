package com.example.imkb.View.navbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imkb.Extensions.Cryption;
import com.example.imkb.Extensions.ImkbAdapter;
import com.example.imkb.Extensions.OnItemClickListener;
import com.example.imkb.Models.HandshakeModel.HandshakeModel;
import com.example.imkb.Models.HandshakeModel.HandshakeReguestBody;
import com.example.imkb.Models.ListModel.ListBody;
import com.example.imkb.Models.ListModel.ListModel;
import com.example.imkb.Models.ListModel.Stocks;
import com.example.imkb.R;
import com.example.imkb.Services.ApiService;
import com.example.imkb.Services.HandshakeService;
import com.example.imkb.View.DetailScreenActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Hacim100 extends Fragment implements OnItemClickListener {
    private ApiService apiService;
    private ArrayList<Stocks> hacimYuzList;
    public  ArrayList<Stocks> hacimYuzListnew;
    public ImkbAdapter adapter;
    public RecyclerView recyclerView;
    private EditText editText;
    private HandshakeModel handshakeModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hacim100, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = HandshakeService.getRetrofitInstance().create(ApiService.class);
        hacimYuzList = new ArrayList<>();
        hacimYuzListnew = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recylerHacim100);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        editText = view.findViewById(R.id.searchImkb);
        getHandshakeService();
        doSearch();
    }

    private void doSearch() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setAdapter(ArrayList<Stocks> hacimYuzList) {
        adapter = new ImkbAdapter(getActivity(), hacimYuzListnew);
        recyclerView.setAdapter(adapter);
        adapter.setListener(this);
        adapter.notifyDataSetChanged();
    }

    private void getHandshakeService() {
        HandshakeReguestBody body = new HandshakeReguestBody();
        body.setDeviceId(Build.ID);
        body.setSystemVersion(String.valueOf(Build.VERSION.SDK_INT));
        body.setPlatformName("Android");
        body.setDeviceModel(Build.MODEL);
        body.setManifacturer(Build.MANUFACTURER);

        Call<HandshakeModel> call = apiService.getFirstService(body);
        call.enqueue(new Callback<HandshakeModel>() {
            @Override
            public void onResponse(Call<HandshakeModel> call, Response<HandshakeModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        handshakeModel = response.body();
                        getListService(handshakeModel);
                    }
                }
            }

            @Override
            public void onFailure(Call<HandshakeModel> call, Throwable t) {

            }
        });
    }

    private void getListService(final HandshakeModel model) {

        try {
            String ciphertextString = Cryption.encrypt("volume100".getBytes(),model.getAesKey().getBytes(),model.getAesIV().getBytes());
            ListBody body1 = new ListBody();
            body1.setPeriod(ciphertextString);

            Call<ListModel> call = apiService.getSecondListService(model.getAuthorization(), "application/json", body1);
            call.enqueue(new Callback<ListModel>() {
                @Override
                public void onResponse(@NonNull Call<ListModel> call, @NonNull Response<ListModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ListModel listModel = response.body();
                            hacimYuzList = listModel.getStocks();
                            for (Stocks stocksItem : hacimYuzList){
                                try {
                                    stocksItem.setSymbol(Cryption.decrypt(stocksItem.getSymbol().getBytes(),model.getAesKey().getBytes(),model.getAesIV().getBytes()));
                                    hacimYuzListnew.add(stocksItem);
                                    setAdapter(hacimYuzListnew);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<ListModel> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailScreenActivity.class);
        intent.putExtra("id",hacimYuzList.get(position).getId());
        intent.putExtra("aeskey",handshakeModel.getAesKey());
        intent.putExtra("aesIV",handshakeModel.getAesIV());
        intent.putExtra("auth",handshakeModel.getAuthorization());
        startActivity(intent);
    }
}
