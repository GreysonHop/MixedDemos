package com.testdemo.testRecyclerViewType.itemtouchhelperdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.testdemo.R;
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.demochannel.ChannelActivity;
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.demodrag.DragActivity;


/**
 * Created by YoKeyword on 16/1/4.
 */
public class ItemMoveMainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_item_move);

        Button mBtnDrag = (Button) findViewById(R.id.btn_drag);
        Button mBtnChannel = (Button) findViewById(R.id.btn_channl);
        mBtnDrag.setOnClickListener(this);
        mBtnChannel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_drag:
                startActivity(new Intent(this, DragActivity.class));
                break;
            case R.id.btn_channl:
                startActivity(new Intent(this, ChannelActivity.class));
                break;
        }
    }
}
