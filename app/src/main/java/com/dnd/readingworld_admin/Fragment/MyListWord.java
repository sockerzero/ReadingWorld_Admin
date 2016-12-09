package com.dnd.readingworld_admin.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.dnd.readingworld_admin.Adapter.MyListWord_ItemAdapter;
import com.dnd.readingworld_admin.Init.Init;
import com.dnd.readingworld_admin.Model.ContentWord;
import com.dnd.readingworld_admin.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;


public class MyListWord extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    ListView lv;
    ArrayList<ContentWord> List_contentWord;
    MyListWord_ItemAdapter adapter;
    TextToSpeech textToSpeech;
    static final String READING_REFERENCE = "readingworld";
    static final String READING_WORLD_REFERENCE = "readingworld_global_foradmin";
    static final String READING_WORLD_REFERENCE_USER = "readingworld_global_foruser";
    static final int Edititemlistview = 1996;
    static final int RESULT_OK = 1996;
    private DatabaseReference mDatabase;
    EditText edtSearch;



    public MyListWord() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list_word, container, false);
        setHasOptionsMenu(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtSearch = (EditText) view.findViewById(R.id.editTextSearch);
        lv = (ListView) view.findViewById(R.id.lvMylistword);

        List_contentWord = new ArrayList<ContentWord>();
        adapter = new MyListWord_ItemAdapter(getActivity(), R.layout.fmmylistword_listview_item, List_contentWord);
        lv.setAdapter(adapter);


        loadData();

        lv.setOnItemLongClickListener(this);
        lv.setOnItemClickListener(this);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void loadData()
    {
        mDatabase.child(READING_WORLD_REFERENCE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot parent, String s) {
                for (DataSnapshot child : parent.getChildren())
                {
                    ContentWord cont = child.getValue(ContentWord.class);
                    List_contentWord.add(new ContentWord(cont.getWordContent(), cont.getWordType(), cont.getLinkImage()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("What do you want?");
        dialogBuilder.setPositiveButton("Kill You !!!...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ContentWord pos = adapter.getItem(position);
                final String selectedKey = pos.getWordContent()+"_"+pos.getWordType();

                int i = 0;
                for(i=0;i<adapter.getList1().size();i++)
                {
                    if(List_contentWord.get(i).getWordContent().toLowerCase().contains(pos.getWordContent().toLowerCase())
                            &&  List_contentWord.get(i).getWordType().toLowerCase().contains(pos.getWordType().toLowerCase()))
                    {
                        break;
                    }
                }

                adapter.removeItemInarayContentAfterFilter(i);
                if(adapter.getList().size()!=adapter.getList1().size())
                    adapter.removeItemInarayContent(position);
                adapter.notifyDataSetChanged();

                //Delete node Firebase
                mDatabase.child(READING_WORLD_REFERENCE).runTransaction(new Transaction.Handler() {
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        for(MutableData parent : mutableData.getChildren())
                        {
                            for(MutableData data : parent.getChildren())
                            {
                                if(data.getKey().toLowerCase().toString().equals(selectedKey.toLowerCase().toString()))
                                {
                                    data.setValue(null);
                                    break;
                                }
                            }
                            break;
                        }
                        return Transaction.success(mutableData);
                    }
                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }

                });
                Init.initToast(getActivity(), "Delete Success, Goodbye you :(.");
            }
        }).setNeutralButton("Add to Listword!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int which) {

                ContentWord pos = adapter.getItem(position);
                String selectedKey = pos.getWordContent()+"_"+pos.getWordType();

                mDatabase.child(READING_WORLD_REFERENCE_USER).child(selectedKey).setValue(pos);

                Init.initToast(getActivity(), "Add to Listword successfully <3");
            }
        });
        dialogBuilder.create().show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_listview_mylistword_zoomitem);
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = (4 *metrics.heightPixels)/5;
        dialog.getWindow().setLayout( width,  height);

        final ImageView imgView = (ImageView) dialog.findViewById(R.id.imgViewZoom);


        ContentWord pos = adapter.getItem(position);

        Glide.with(this).load(pos.getLinkImage()).asBitmap().fitCenter().into(imgView);

        dialog.show();
    }


}

