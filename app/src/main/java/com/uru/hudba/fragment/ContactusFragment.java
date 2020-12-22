package com.uru.hudba.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uru.hudba.R;
import com.uru.hudba.activity.MainActivity;
import com.uru.hudba.adapter.ContactusAdapter;
import com.uru.hudba.base.BaseActivity;
import com.uru.hudba.base.BaseFragment;
import com.uru.hudba.constant.GlobalConstants;
import com.uru.hudba.model.ContactModel;

import java.util.ArrayList;

public class ContactusFragment extends BaseFragment implements View.OnClickListener{
    private View rootView;
    private ImageView back;
    private RecyclerView rv_streaming, rv_search, rv_library;
    private Button contact_btn;

    private ContactusAdapter adapter_streaming, adapter_search, adapter_library;
    private ArrayList<ContactModel> list_streaming = new ArrayList<>();
    private ArrayList<ContactModel> list_search = new ArrayList<>();
    private ArrayList<ContactModel> list_library = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).current_page = "contactus";
        rootView = inflater.inflate(R.layout.fragment_contactus, container, false);
        back = rootView.findViewById(R.id.back);
        back.setOnClickListener(this);
        rv_streaming = rootView.findViewById(R.id.recycler_view1);
        rv_search = rootView.findViewById(R.id.recycler_view2);
        rv_library = rootView.findViewById(R.id.recycler_view3);
        contact_btn = rootView.findViewById(R.id.contact_btn);
        contact_btn.setOnClickListener(this);

        list_streaming.clear();
        list_search.clear();
        list_library.clear();

        ContactModel stream1 = new ContactModel();
        stream1.setId("0");
        stream1.setTitle(getResources().getString(R.string.contactus_streaming1));
        stream1.setDescription(getResources().getString(R.string.contactus_streaming1_sub));
        list_streaming.add(stream1);

        ContactModel stream2 = new ContactModel();
        stream2.setId("1");
        stream2.setTitle(getResources().getString(R.string.contactus_streaming2));
        stream2.setDescription(getResources().getString(R.string.contactus_streaming2_sub));
        list_streaming.add(stream2);

        ContactModel stream3 = new ContactModel();
        stream3.setId("2");
        stream3.setTitle(getResources().getString(R.string.contactus_streaming3));
        stream3.setDescription(getResources().getString(R.string.contactus_streaming3_sub));
        list_streaming.add(stream3);

        adapter_streaming = new ContactusAdapter(getActivity(), list_streaming);
        rv_streaming.setAdapter(adapter_streaming);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_streaming.setLayoutManager(linearLayoutManager);

        ContactModel search1 = new ContactModel();
        search1.setId("0");
        search1.setTitle(getResources().getString(R.string.contactus_search1));
        search1.setDescription(getResources().getString(R.string.contactus_search1_sub));
        list_search.add(stream1);

        ContactModel search2 = new ContactModel();
        search2.setId("1");
        search2.setTitle(getResources().getString(R.string.contactus_searchg2));
        search2.setDescription(getResources().getString(R.string.contactus_search2_sub));
        list_search.add(stream2);

        adapter_search = new ContactusAdapter(getActivity(), list_search);
        rv_search.setAdapter(adapter_search);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_search.setLayoutManager(linearLayoutManager1);


        ContactModel library1 = new ContactModel();
        library1.setId("0");
        library1.setTitle(getResources().getString(R.string.contactus_library1));
        library1.setDescription(getResources().getString(R.string.contactus_library1_sub));
        list_library.add(stream1);

        ContactModel library2 = new ContactModel();
        library2.setId("1");
        library2.setTitle(getResources().getString(R.string.contactus_library2));
        library2.setDescription(getResources().getString(R.string.contactus_library2_sub));
        list_library.add(stream2);

        ContactModel library3 = new ContactModel();
        library3.setId("2");
        library3.setTitle(getResources().getString(R.string.contactus_library3));
        library3.setDescription(getResources().getString(R.string.contactus_library3_sub));
        list_library.add(stream3);

        adapter_library = new ContactusAdapter(getActivity(), list_library);
        rv_library.setAdapter(adapter_library);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_library.setLayoutManager(linearLayoutManager2);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                popFragment();
                break;
            case R.id.contact_btn:
                Intent officeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConstants.contact_link));
                startActivity(officeintent);
                break;
        }
    }
}
