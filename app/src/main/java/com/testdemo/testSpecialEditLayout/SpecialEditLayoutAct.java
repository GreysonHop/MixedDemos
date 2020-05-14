package com.testdemo.testSpecialEditLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.R;
import com.testdemo.testSpecialEditLayout.popupList.TestPopupListActivity;
import com.testdemo.testView.shader.PictureWithTextDrawable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Greyson on 2018/10/15.
 */

public class SpecialEditLayoutAct extends Activity {
    private PhoneReceiver phoneReceiver;
    private ToolLayout toolLayout;
    private EditText editText;
    private ImageView ivGift;

    private LinearLayout fl_content;
    private TextView message_tv_content;
    private PopupWindow mPopupWindow;
    private MenuLinearLayout mMenuLinearLayout;

    private TextView tv_test_clickable;

    private MenuPopUp menuPopUp;
    private float mOffsetX;
    private float mOffsetY;

    private final int MENU_ID_MY = 10086;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_special_edit_layout);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        ImageView iv = findViewById(R.id.iv_picture);
        tv_test_clickable = findViewById(R.id.tv_test_clickable);
        toolLayout = findViewById(R.id.layout_tool);
        editText = findViewById(R.id.et_msg);
        ivGift = findViewById(R.id.iv_gift);

        fl_content = findViewById(R.id.fl_content);
        message_tv_content = findViewById(R.id.message_tv_content);

        tv_test_clickable.post(() -> {
            Layout layout = tv_test_clickable.getLayout();
            if (layout != null) {
                if (layout.getEllipsisCount(tv_test_clickable.getLineCount() - 1) > 0) {
                    Toast.makeText(this, "有省略号哦。", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "没有省略号呢！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == editText) {
                    toolLayout.fullEdit(hasFocus);
                }
            }
        });*/
        PictureWithTextDrawable drawable = new PictureWithTextDrawable(
                getResources().getDrawable(R.drawable.call_icon_gift)
                , getResources().getDrawable(R.drawable.galata)
                , "图片已过期");
        drawable.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        iv.setImageDrawable(drawable);

        setCustomSelectableTextCallBack();
        observeKeyboardHeight();
        setTextClickable();
        setTelephony();
        setLocation();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setCustomSelectableTextCallBack() {
        fl_content.setOnClickListener((v) -> {
            startActivity(new Intent(this, TestPopupListActivity.class));
            Log.d("greyson", "onClick() fl_content");
        });

        tv_test_clickable.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.d("greyson", "onCreateActionMode mode="+mode+"_menu=" + menu.size());
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                Log.d("greyson", "onPrepareActionMode mode="+mode+"_menu=" + menu);
                if (menu.findItem(MENU_ID_MY) == null) {
                    menu.add(Menu.NONE, MENU_ID_MY, 0, "myMenu)");
                    return true;
                }
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Log.d("greyson", "onActionItemClicked mode="+mode+"_menuItem=" + item);
                if (item.getItemId() == MENU_ID_MY) {
                    Toast.makeText(SpecialEditLayoutAct.this, "点击了自定义菜单项", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d("greyson", "onDestroyActionMode mode="+mode);
            }
        });


        fl_content.setOnTouchListener((view, event) -> {
            mOffsetX = event.getX();
            mOffsetY = event.getY();
            return false;
        });

        fl_content.setOnLongClickListener((v) -> {
            System.out.println("greyson:" +message_tv_content.getSelectionStart() + "_" + message_tv_content.getSelectionEnd());
            CharSequence sp = message_tv_content.getText();
            if (sp instanceof Spannable) {
                Selection.selectAll((Spannable) sp);
            }
            System.out.println("greyson:" +message_tv_content.getSelectionStart() + "_" + message_tv_content.getSelectionEnd());
            showMenuPopup();
            return true;
        });

    }

    private void observeKeyboardHeight() {
        toolLayout.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            //System.out.println("greyson onLayoutChange: " + left + " - " + right + " - " + top + " - " + bottom
            //    + " \n" + oldLeft + " - " + oldRight + " - " + oldTop + " - " + oldBottom);

            Rect rect = new Rect();
            // 获取当前页面窗口的显示范围
            toolLayout.getWindowVisibleDisplayFrame(rect);
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            int keyboardHeight = screenHeight - rect.bottom; // 拟定输入法的高度
            //if (Math.abs(keyboardHeight) > screenHeight / 4) {// 超过屏幕四分之一则表示弹出了输入法
            if (Math.abs(keyboardHeight) > screenHeight / 4) {
                toolLayout.fullEdit(true);
            } else {
                if (toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIDDLE
                        || toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIN) {
                    return;
                }
                toolLayout.fullEdit(false);
            }

        });

        /*final View main = toolLayout.getRootView();
        main.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        System.out.println("greyson -------");
                        Rect rect = new Rect();
                        main.getWindowVisibleDisplayFrame(rect);
                        int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                        int screenHeight = main.getRootView().getHeight();//屏幕高度
                        if (mainInvisibleHeight > screenHeight / 4) {
                            toolLayout.fullEdit(true);
                        } else {
                            if (toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIDDLE
                                    || toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIN) {
                                return;
                            }
                            toolLayout.fullEdit(false);
                        }
                    }
                }
        );*/
    }

    //定位、获取设备位置、所在国家等信息
    private void setLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Log.d("greyson", "" + mManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));//这个判断是否必须
        Location mLocation = mManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);//GPS_PROVIDER一直返回null?

        try {
            double mLat = mLocation.getLatitude();//获取纬度
            double mLng = mLocation.getLongitude();//获取经度
            Log.d("greyson", "lat= " + mLat + ", lng= " + mLng);
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> list = gc.getFromLocation(mLat, mLng, 5);
            for (Address address : list) {
                Log.d("greyson", "address = " + address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PhoneStateListener myPhoneCallListener;
    //监听电话状态的两种方式
    private void setTelephony() {
        //第一种方式，需要权限
        IntentFilter filters = new IntentFilter();
        filters.addAction("android.intent.action.PHONE_STATE");
        phoneReceiver = new PhoneReceiver(type -> {//0未知 1挂断 2接通、拨打 3响铃
            Log.d("greyson", "PhoneReceiver: type = " + type);
            if (type == 3) {
                Toast.makeText(SpecialEditLayoutAct.this, "TestDemo提醒您，电话来啦！", Toast.LENGTH_SHORT).show();
            }
        });
        registerReceiver(phoneReceiver, filters);

        ivGift.setOnClickListener((v) -> {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_NONE);
        });

        //第二方式，不知道是否需要权限。而且网上有一个人说小米5X监听不到？
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            myPhoneCallListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:// 电话挂断
                            Log.d("greyson", "onCallStateChanged: 空闲...");
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK: //电话通话的状态
                            Log.d("greyson", "onCallStateChanged: 通话中...");
                            break;
                        case TelephonyManager.CALL_STATE_RINGING: //电话响铃的状态
                            Log.d("greyson", "onCallStateChanged: 响铃");
                            break;
                    }
                }
            };
            // 注册来电监听
            tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void showMenuPopup() {
        List<String> menuList = Arrays.asList(
                /*"拷贝--你狗狗的DNA-pu na na na!!?!",
                "全部删除",
                "转发",
                "随便点",
                "引用",
                "删除",
                "多选",
                "销毁"*/
                "recall", "copy", "forward", "quote", "alerts", "delete", "multiple selection"
        );

         /*if (mPopupWindow == null) {
                mPopupWindow = new PopupWindow(this);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(null);
                mMenuLinearLayout = new MenuLinearLayout(this);
                mMenuLinearLayout.setOnMenuClickListener((view) -> {
                    Toast.makeText(this, "you click: " + ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                    mPopupWindow.dismiss();
                });
                mPopupWindow.setContentView(mMenuLinearLayout);
                mMenuLinearLayout.setMenuList(menuList);
            }
            mPopupWindow.showAsDropDown(fl_content);*/

        if (menuPopUp == null) {
            menuPopUp = new MenuPopUp(this);
            menuPopUp.setMenuList(menuList);
            menuPopUp.setOnMenuClickListener((view, position) -> {
                System.out.println(view + " -- " + position);
                Toast.makeText(this, "press: " + position, Toast.LENGTH_SHORT).show();
            });
        }
        menuPopUp.showPopupWindow(fl_content, mOffsetX, mOffsetY, false, true);
//      menuPopUp.showPopupWindow(fl_content);
    }

    private void setTextClickable() {
        tv_test_clickable.setHighlightColor(getResources().getColor(R.color.transparent));
        SpannableStringBuilder spannableStBuilder = new SpannableStringBuilder();
        spannableStBuilder.append("回复").append(" ");
        int colorStart = spannableStBuilder.length() - 1;

        spannableStBuilder.append("Anne");
        int colorEnd = spannableStBuilder.length();

        spannableStBuilder.append(" : ");
        int clickableEnd = spannableStBuilder.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (widget instanceof TextView) {
                    Toast.makeText(SpecialEditLayoutAct.this, "you click: " + ((TextView) widget).getText().toString(), Toast.LENGTH_SHORT).show();
                    //to show the input panel
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }
        };
        spannableStBuilder.setSpan(clickableSpan, colorStart, clickableEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), colorStart, colorEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), colorEnd, clickableEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStBuilder.append("I am Iron man! Can you beat me!---- But I never give up!");

        tv_test_clickable.setMovementMethod(LinkMovementMethod.getInstance());
        tv_test_clickable.setText(spannableStBuilder);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_emoji:
                testSomeMethod();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (phoneReceiver != null) {
            unregisterReceiver(phoneReceiver);
            phoneReceiver = null;
        }
    }

    private void testSomeMethod() {
        int h1 = toolLayout.getContext().getResources().getDisplayMetrics().heightPixels;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int h2 = metrics.heightPixels;

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int h3 = point.y;

        System.out.println("屏幕高：" + h1 + " - " + h2 + " - " + h3);

        Rect rect = new Rect();
        toolLayout.getRootView().getWindowVisibleDisplayFrame(rect);
        Rect rect2 = new Rect();
        toolLayout.getWindowVisibleDisplayFrame(rect2);
        System.out.println("rect：" + rect.height() + " - " + rect2.height()
                + "\n" + rect.hashCode() + " - " + rect2.hashCode() + "\n" + rect.bottom + " - " + rect2.bottom
                + "\n" + rect.top + " - " + rect2.top);

        System.out.println("rootView : " + toolLayout.getRootView() + " - " + editText.getRootView());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (editText.hasFocus()) {
                editText.clearFocus();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}


/**
 * 去电、来电监听的广播
 * <p>
 * Created by hwk on 2018/7/12.
 */
class PhoneReceiver extends BroadcastReceiver {
    private boolean isListen = false;
    private PhoneListener phoneListener;

    public PhoneReceiver(PhoneListener phoneListener) {
        this.phoneListener = phoneListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isListen) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            if (tm != null) {
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
                isListen = true;
            }
        }
        //去电
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //这个地方只有你拨号的瞬间会调用
            Log.e("PhoneReceiver", "call OUT:" + phoneNumber);
        }
    }

    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            //state 当前状态 incomingNumber,貌似没有去电的API
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (TextUtils.isEmpty(incomingNumber)) {
                        phoneListener.status(0);
                    } else {
                        phoneListener.status(1);
                    }
                    //不管是去电还是来电通话结束都会调用，不管是不是你挂的
                    Log.e("PhoneReceiver", "挂断" + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    phoneListener.status(2);
                    //如果是来电，这个必须点击接听按钮才会调用
                    //如果是拨打，那么一开始就会调用，并不是打通了之后才会调用
                    Log.e("PhoneReceiver", "摘机状态，接听或者拨打" + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    phoneListener.status(3);
                    //只有来电的时候会调用
                    Log.e("PhoneReceiver", "响铃:来电号码:" + incomingNumber);
                    break;
            }
        }
    };

    public interface PhoneListener {
        void status(int type);//0未知 1挂断 2接通、拨打 3响铃
    }
}
