package com.testdemo.testCanDragScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import com.testdemo.R;

/**
 * Created by Greyson on 2018/4/16.
 */
public class DraggableScrollViewAct extends Activity {

    private View userContactLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_draggable_scroll);

        userContactLayout = findViewById(R.id.userContactLayout);
        findViewById(R.id.cardLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DraggableScrollViewAct", "cardLayout is clicked!!!!!");
                userContactLayout.setVisibility(userContactLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }
}
