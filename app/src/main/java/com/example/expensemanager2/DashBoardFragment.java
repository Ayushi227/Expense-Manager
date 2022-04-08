package com.example.expensemanager2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager2.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 //Use the {@link DashBoardFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    //Floating Button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview..
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolen
    private boolean isOpen=false;

    //Animation
    private Animation FadOpen,FadeClose;

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private FirebaseRecyclerAdapter incomeAdapter;
    private FirebaseRecyclerAdapter expenseAdapter;

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    /** public DashboardFragment() {
     // Required empty public constructor
     }*/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    /**public static DashboardFragment newInstance(String param1, String param2) {
     DashboardFragment fragment = new DashboardFragment();
     Bundle args = new Bundle();
     args.putString(ARG_PARAM1, param1);
     args.putString(ARG_PARAM2, param2);
     fragment.setArguments(args);
     return fragment;
     }
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**if (getArguments() != null) {
         mParam1 = getArguments().getString(ARG_PARAM1);
         mParam2 = getArguments().getString(ARG_PARAM2);
         }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
        //Connect floationg button to layout
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        //Connect floating text.
        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        //Animation connect..
        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if (isOpen) {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);

                    totalsum += data.getAmount();

                    String stResult = String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult + ".00");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data data = mysnapshot.getValue(Data.class);
                    totalsum += data.getAmount();
                    String strTotalSum = String.valueOf(totalsum);

                    totalExpenseResult.setText(strTotalSum + ".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);


        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);
        return myview;
    }

        //Floating button animation
        private void ftAnimation () {
            if (isOpen) {

                fab_income_btn.startAnimation(FadeClose);
                fab_expense_btn.startAnimation(FadeClose);
                fab_income_btn.setClickable(false);
                fab_expense_btn.setClickable(false);

                fab_income_txt.startAnimation(FadeClose);
                fab_expense_txt.startAnimation(FadeClose);
                fab_income_txt.setClickable(false);
                fab_expense_txt.setClickable(false);
                isOpen = false;

            } else {
                fab_income_btn.startAnimation(FadOpen);
                fab_expense_btn.startAnimation(FadOpen);
                fab_income_btn.setClickable(true);
                fab_expense_btn.setClickable(true);

                fab_income_txt.startAnimation(FadOpen);
                fab_expense_txt.startAnimation(FadOpen);
                fab_income_txt.setClickable(true);
                fab_expense_txt.setClickable(true);
                isOpen = true;

            }
        }
        private void addData () {
            fab_income_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incomeDataInsert();
                }
            });

            fab_expense_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expenseDataInsert();
                }
            });
        }

        public void incomeDataInsert () {
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
            mydialog.setView(myview);
            final AlertDialog dialog = mydialog.create();

            dialog.setCancelable(false);
            final EditText edtAmmount = myview.findViewById(R.id.ammount_edt);
            final EditText edtType = myview.findViewById(R.id.type_edt);
            final EditText edtNote = myview.findViewById(R.id.note_edt);

            Button btnSave = myview.findViewById(R.id.btnSave);
            Button btnCansel = myview.findViewById(R.id.btnCancel);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String type = edtType.getText().toString().trim();
                    String ammount = edtAmmount.getText().toString().trim();
                    String note = edtNote.getText().toString().trim();

                    if (TextUtils.isEmpty(type)) {
                        edtType.setError("Required Field..");
                        return;
                    }

                    if (TextUtils.isEmpty(ammount)) {
                        edtAmmount.setError("Required Field..");
                        return;
                    }

                    int ourammontint = Integer.parseInt(ammount);

                    if (TextUtils.isEmpty(note)) {
                        edtNote.setError("Required Field..");
                        return;
                    }

                    String id = mIncomeDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(ourammontint, type, note, id, mDate);
                    mIncomeDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();

                    ftAnimation();
                    dialog.dismiss();
                }
            });
            btnCansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ftAnimation();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        public void expenseDataInsert () {
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
            mydialog.setView(myview);

            final AlertDialog dialog = mydialog.create();

            dialog.setCancelable(false);

            EditText ammount = myview.findViewById(R.id.ammount_edt);
            EditText type = myview.findViewById(R.id.type_edt);
            EditText note = myview.findViewById(R.id.note_edt);

            Button btnSave = myview.findViewById(R.id.btnSave);
            Button btnCansel = myview.findViewById(R.id.btnCancel);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmAmmount = ammount.getText().toString().trim();
                    String tmtype = type.getText().toString().trim();
                    String tmnote = note.getText().toString().trim();

                    if (TextUtils.isEmpty(tmAmmount)) {
                        ammount.setError("Requires Fields...");
                        return;
                    }

                    int inamount = Integer.parseInt(tmAmmount);

                    if (TextUtils.isEmpty(tmtype)) {
                        type.setError("Requires Fields...");
                        return;
                    }
                    if (TextUtils.isEmpty(tmnote)) {
                        note.setError("Requires Fields...");
                        return;
                    }

                    String id = mExpenseDatabase.push().getKey();
                    String mDate = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(inamount, tmtype, tmnote, id, mDate);
                    mExpenseDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(), "Data added", Toast.LENGTH_SHORT).show();

                    ftAnimation();
                    dialog.dismiss();
                }
            });

            btnCansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ftAnimation();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();


        incomeAdapter = new FirebaseRecyclerAdapter<Data, DashBoardFragment.IncomeViewHolder>(options) {


            public DashBoardFragment.IncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new DashBoardFragment.IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false));
            }


            protected void onBindViewHolder(DashBoardFragment.IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeAmmount(model.getAmount());
                holder.setIncomeDate(model.getDate());


            }

        };
        incomeAdapter.startListening();
        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerOptions<Data> options_two = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();


        expenseAdapter = new FirebaseRecyclerAdapter<Data, DashBoardFragment.ExpenseViewHolder>(options_two) {


            public DashBoardFragment.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new DashBoardFragment.ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboart_expense, parent, false));
            }


            protected void onBindViewHolder(DashBoardFragment.ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseAmmount(model.getAmount());
                holder.setExpenseDate(model.getDate());


            }

        };
        expenseAdapter.startListening();
        mRecyclerExpense.setAdapter(expenseAdapter);
    }
    @Override
    public void onStop() {
        super.onStop();
        incomeAdapter.stopListening();
        expenseAdapter.stopListening();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){
            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);
        }

        public void setIncomeAmmount(int ammount){
            TextView mAmmount=mIncomeView.findViewById(R.id.ammount_income_ds);

            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);

        }

        public void setIncomeDate(String date){
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);

        }

    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){
            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }

        public void setExpenseAmmount(int ammount){
            TextView mAmmount=mExpenseView.findViewById(R.id.ammount_expense_ds);

            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);

        }

        public void setExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);

        }

    }
}