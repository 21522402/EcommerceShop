package com.example.ecommerceshop.Phat.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceshop.Phat.Adapter.AdapterOrderShop;
import com.example.ecommerceshop.Phat.Adapter.AdapterRatedProducts;
import com.example.ecommerceshop.Phat.Model.OrderShop;
import com.example.ecommerceshop.Phat.Model.Product;
import com.example.ecommerceshop.Phat.Model.RatedProduct;
import com.example.ecommerceshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewShopActivity extends AppCompatActivity {
    ImageView backbtn;
    RecyclerView listReview;
    FirebaseAuth firebaseAuth;
    ArrayList<String> ListProductId;
    ArrayList<RatedProduct> ratedProducts;
    AdapterRatedProducts adapterRatedProducts;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_shop);
        backbtn=findViewById(R.id.backbtn);
        listReview=findViewById(R.id.listReview);
        firebaseAuth=FirebaseAuth.getInstance();
        loadListRatedProductsId();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
    }

    private void loadListRatedProductsId() {
        ListProductId = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListProductId.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(uid).child("Customer").child("Reviews").orderByChild("shopId")
                            .equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                            String productId = "" + ds.child("productId").getValue();
                                            ListProductId.add(productId);

                                        }
                                        loadRatedProducts();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static Map<String, Integer> filterAndCountDuplicates(ArrayList<String> strings) {
        Map<String, Integer> uniqueStrings = new HashMap<>();

        // Đếm số lần xuất hiện của từng phần tử trong mảng
        for (String str : strings) {
            uniqueStrings.put(str, uniqueStrings.getOrDefault(str, 0) + 1);
        }
        return uniqueStrings;
    }
    private void loadRatedProducts() {
        ratedProducts=new ArrayList<>();
        Map<String, Integer> uniqueStrings =  filterAndCountDuplicates(ListProductId);
        for (Map.Entry<String, Integer> entry : uniqueStrings.entrySet()) {
            String str = entry.getKey();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("Shop").child("Products").child(str)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Product product = new Product();
                            product=snapshot.getValue(Product.class);
                            if(product!=null){
                                RatedProduct ratedProduct = new RatedProduct(product.getProductId(), product.getProductName()
                                        , product.getProductBrand(), product.getProductPrice(), entry.getValue(), product.getUriList());
                                ratedProducts.add(ratedProduct);
                            }
                            adapterRatedProducts = new AdapterRatedProducts(getApplicationContext(), ratedProducts);
                            adapterRatedProducts.notifyDataSetChanged();
                            listReview.setAdapter(adapterRatedProducts);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}
