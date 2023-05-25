package com.example.ecommerceshop.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceshop.Adapter.AdapterListCategoryShop;
import com.example.ecommerceshop.Adapter.AdapterProductShop;
import com.example.ecommerceshop.Utils.Constants;
import com.example.ecommerceshop.Model.Product;
import com.example.ecommerceshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePageShopFragment extends Fragment {
    private View mview;
    private ImageView filterbtn;
    private TextView filtertv;
    private RecyclerView listproduct , statusList;
    private AdapterProductShop adapterProductShop;
    private AdapterListCategoryShop shop;
    private List<String> listCategory;
    private ArrayList<Product> products;
    private FirebaseAuth firebaseAuth;
    private EditText searchView;

    public HomePageShopFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_home_page_shop, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        listproduct=mview.findViewById(R.id.listproduct);
        filterbtn=mview.findViewById(R.id.imageView4);
        statusList=mview.findViewById(R.id.statusList);
        filtertv=mview.findViewById(R.id.filtertv);
        searchView=mview.findViewById(R.id.searchView);
        loadListcategory();
        loadAllProduct();
        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Category")
                        .setItems(Constants.categoryslist, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selected = Constants.categoryslist[i];
                                filtertv.setText(selected);
                                if(selected.equals("All items")) loadAllProduct();
                                else{
                                    LoadFilterProduct(selected);
                                }
                            }
                        }).show();
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapterProductShop.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return mview;
    }

    private void LoadFilterProduct(String selected) {
        products = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String category = ""+ds.child("productCategory").getValue();
                    if(category.equals(selected)){
                        Product product = ds.getValue(Product.class);
                        products.add(product);
                    }
                }

                adapterProductShop = new AdapterProductShop(getContext(), products);
                listproduct.setAdapter(adapterProductShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListcategory() {
        listCategory=new ArrayList<>();
        listCategory= Arrays.asList(Constants.categoryslist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        shop = new AdapterListCategoryShop(getContext(), listCategory);
        statusList.setLayoutManager(linearLayoutManager);
        statusList.setAdapter(shop);
    }

    private void loadAllProduct() {
        products = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Product product = ds.getValue(Product.class);
                    products.add(product);
                }

                adapterProductShop = new AdapterProductShop(getContext(), products);
                listproduct.setAdapter(adapterProductShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}