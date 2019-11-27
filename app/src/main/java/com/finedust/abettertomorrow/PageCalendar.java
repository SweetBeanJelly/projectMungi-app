package com.finedust.abettertomorrow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PageCalendar extends Fragment {

    Activity root;
    LayoutInflater layoutinflater;
    ViewGroup viewGroup;

    static ListView listView;
    int check;
    DataAdapter dataAdapter;
    Cursor cursor;
    DataCursorAdapter cursorAdapter;

    public PageCalendar() {
        //TODO CONSTRUCTOR
    }
    public static PageCalendar newInstance() {
        return new PageCalendar();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.page_calendar,container,false);

        layoutinflater = inflater;
        viewGroup = container;
        root=getActivity();

        listView = (ListView)view.findViewById(R.id.dataList);
        dataAdapter = new DataAdapter(getActivity());
        cursor = dataAdapter.data();
        cursorAdapter = new DataCursorAdapter(getActivity(), cursor);
        listView.setAdapter(cursorAdapter);
        registerForContextMenu(listView);

        Button btnAdd = (Button)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert();
            }
        });
        return view;
    }

    // Custom Alert
    public void alert() {
        final View promptUserView = layoutinflater.inflate(R.layout.sub_calendar_data_add,viewGroup, false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(root,R.style.MyAlertDialogStyle);
        alertDialogBuilder.setView(promptUserView);
        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.editTextCalendar);
        alertDialogBuilder.setTitle("메모").setMessage("다이어리에 작성 됩니다.");

        alertDialogBuilder.setNegativeButton("확인",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String description = userAnswer.getText().toString();
                if (description.trim().matches("")) {
                    Toast.makeText(root, "내용을 작성하세요", Toast.LENGTH_LONG).show();
                    userAnswer.setText("");
                } else {
                    dataAdapter = new DataAdapter(root);
                    long check = dataAdapter.insert(userAnswer.getText().toString());
                    if (check >= 0) {
                        Toast.makeText(root, "저장 완료", Toast.LENGTH_LONG).show();
                        cursor = dataAdapter.data();
                        cursorAdapter = new DataCursorAdapter(root, cursor);
                        listView.setAdapter(cursorAdapter);
                        registerForContextMenu(listView);
                    }
                }
            }
        });
        alertDialogBuilder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        // all set and time to build and show up!
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    class DataCursorAdapter extends CursorAdapter {
        public DataCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
            // TODO CONSTRUCTOR
        }
        @Override
        public void bindView(View arg0, Context arg1, Cursor arg2) {
            // TODO METHOD
            TextView time = (TextView)arg0.findViewById(R.id.textTime);
            String timer = arg2.getString(arg2.getColumnIndexOrThrow("date"));
            time.setText(timer);
            TextView description_t = (TextView)arg0.findViewById(R.id.textDescription);
            String description = arg2.getString(arg2.getColumnIndexOrThrow("description"));
            description_t.setText(description);
            check = arg2.getInt(0);
        }
        @Override
        public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
            // TODO METHOD
            return LayoutInflater.from(arg0).inflate(R.layout.list_calendar_item, arg2, false);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        // TODO METHOD
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, view.getId(), 0, "수정");
        menu.add(1, view.getId(), 1, "삭제");
        check = cursor.getInt(0);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO METHOD
        if (item.getTitle() == "삭제") {
            dataAdapter.delete(check);
            update();
        }
        if (item.getTitle() == "수정") {
            int cursor = check;
//            repair(cursor);
        }
        return super.onContextItemSelected(item);
    }

    public void update() {
        cursor = dataAdapter.data();
        cursorAdapter.swapCursor(cursor);
    }
//    public void repair(final int position) {
//        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//        alert.setTitle("내용 수정");
//        alert.setMessage("내용을 다시 작성 하세요");
//        final EditText content = new EditText(getActivity());
//        alert.setView(content);
//
//        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        alert.setPositiveButton("수정", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                String c_string = content.getText().toString();
//                dataAdapter.update(position,c_string);
//                update();
//            }
//        });
//        alert.show();
//    }

}
