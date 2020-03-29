package com.testdemo.testFlipView;

import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.testdemo.BaseActivity;
import com.testdemo.R;
import com.testdemo.databinding.ActTestDatabindingBinding;

/**
 * Create by Greyson on 2020/03/29
 */
public class TestDataBindingAct extends BaseActivity implements View.OnClickListener {

    ActTestDatabindingBinding mBinding;

    int time;

    /*@Override
    protected int getLayoutResId() {
        return R.layout.act_test_databinding;
    }*/

    @Override
    protected void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_test_databinding);
        mBinding.setListener(this);
        mBinding.setVisible(true);
        mBinding.setTitle("还没点击");
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "you click btn.", Toast.LENGTH_SHORT).show();
        mBinding.setTitle("第" + time++ + "次点击");
    }
}
