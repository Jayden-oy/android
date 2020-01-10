package cn.itcast.notepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.itcast.notepad.adapter.NotepadAdapter;
import cn.itcast.notepad.bean.NotepadBean;
import cn.itcast.notepad.database.SQLiteHelper;

public class NotepadActivity extends Activity {
ListView listView;
List<NotepadBean>list;
SQLiteHelper mSQLiteHelper;
NotepadAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        //用于显示记录的列表
        listView=(ListView)findViewById(R.id.listview);
        ImageView add=(ImageView)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(NotepadActivity.this,RecordActivity.class);
                startActivityForResult(intent,1);
            }
        });
        initData();
    }
    protected void initData(){
        mSQLiteHelper=new SQLiteHelper(this);//创建数据库
        showQueryData();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(NotepadActivity.this).setMessage("是否删除记录")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             NotepadBean notepadBean=list.get(position);
                             if (mSQLiteHelper.deleteData(notepadBean.getId())){
                                 list.remove(position);
                                 adapter.notifyDataSetChanged();
                                 Toast.makeText(NotepadActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                             }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog=builder.create();
                dialog.show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotepadBean notepadBean=list.get(position);
                Intent intent=new Intent(NotepadActivity.this,RecordActivity.class);
                intent.putExtra("id",notepadBean.getId());
                intent.putExtra("time",notepadBean.getNotepadTime());
                intent.putExtra("content",notepadBean.getNotepadContent());
                NotepadActivity.this.startActivityForResult(intent,1);
            }
        });
    }
    private void showQueryData(){
        if(list!=null){
            list.clear();
        }
        //从数据库中查询数据(保存的记录)
        list=mSQLiteHelper.query();
        adapter =new NotepadAdapter(this,list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==1&&requestCode==2){
            showQueryData();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        showQueryData();
    }
}
