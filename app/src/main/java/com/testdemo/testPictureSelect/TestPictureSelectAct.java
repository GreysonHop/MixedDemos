package com.testdemo.testPictureSelect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.testdemo.R;

/**
 * Created by Greyson on 2018/6/4.
 */

public class TestPictureSelectAct extends FragmentActivity {

    PictureSelectPanel panel;
    TextView pictureTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_test_picture_select);
        pictureTV = (TextView) findViewById(R.id.pictureTV);
        panel = new PictureSelectPanel(this, findViewById(R.id.pictureLayout));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (panel != null) {
            panel.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClick(View view) {
        if (pictureTV.isSelected()) {
            pictureTV.setSelected(false);
            panel.hide();
        } else {
            pictureTV.setSelected(true);
            panel.show();
        }
    }


}
