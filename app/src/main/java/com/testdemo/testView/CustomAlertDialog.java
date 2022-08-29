package com.testdemo.testView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.testdemo.R;


/**
 * 自定义对话框 AlertDialog
 */
public class CustomAlertDialog extends Dialog {

    private int iconId;
    private CharSequence titleStr;
    private CharSequence messageStr;
    private int messageTextGravity = Gravity.CENTER;
    private View customView;
    private CharSequence leftText;
    private CharSequence rightText;
    private int leftTextColor;
    private int rightTextColor;
    private boolean isHideAllBtn = false;
    private boolean isCancelable = false;
    private OnClickListener leftTextClickListener;
    private OnClickListener rightTextClickListener;

    private ImageView ivAlertIcon;
    private TextView tvAlertTitle;
    private TextView tvAlertMessage;
    private FrameLayout contentLayout;
    private TextView tvAlertLeft;
    private TextView tvAlertRight;
    private View btnDivider;
    private View btnPanelDivider;
    private LinearLayout buttonsPanel;

    protected CustomAlertDialog(Context context) {
        this(context, R.style.CustomDialog);
    }

    protected CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        setWindowLayoutParams();

        ivAlertIcon = findViewById(R.id.alertIcon);
        tvAlertTitle = findViewById(R.id.alertTitle);
        tvAlertMessage = findViewById(R.id.alertMessage);
        contentLayout = findViewById(R.id.customPanel);
        tvAlertLeft = findViewById(R.id.btnLeft);
        tvAlertRight = findViewById(R.id.btnRight);
        btnDivider = findViewById(R.id.btnDivider);
        btnPanelDivider = findViewById(R.id.btnPanelDivider);
        buttonsPanel = findViewById(R.id.buttonsPanel);

        initUI();
        setCanceledOnTouchOutside(isCancelable);
        setCancelable(isCancelable);
    }

    private void setWindowLayoutParams() {
        Window window = getWindow();
        if (window == null) return;

        DisplayMetrics dm = window.getContext().getResources().getDisplayMetrics();
        float maxScreenWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450f + 80f, dm);

        WindowManager.LayoutParams lp = window.getAttributes();
        if (dm.widthPixels > maxScreenWidth) {
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450f + 80f, dm);
        } else {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private void initUI() {
        if (iconId > 0) {
            ivAlertIcon.setVisibility(View.VISIBLE);
            ivAlertIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconId));
        }

        if (titleStr != null) {
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(titleStr);
        }

        if (messageStr != null) {
            tvAlertMessage.setVisibility(View.VISIBLE);
            tvAlertMessage.setText(messageStr);
            tvAlertMessage.setGravity(messageTextGravity);
        }

        if (customView != null) {
            contentLayout.addView(customView);
        }

        // 下面判断交互按钮状态
        boolean isLeftBtnVisible = false;
        boolean isRightBtnVisible = false;
        if (!TextUtils.isEmpty(leftText)) {
            isLeftBtnVisible = true;
            tvAlertLeft.setVisibility(View.VISIBLE);
            tvAlertLeft.setText(leftText);
        }

        if (!TextUtils.isEmpty(rightText)) {
            isRightBtnVisible = true;
            tvAlertRight.setVisibility(View.VISIBLE);
            tvAlertRight.setText(rightText);
        }

        if (leftTextColor != 0) {
            tvAlertLeft.setTextColor(leftTextColor);
        }

        if (rightTextColor != 0) {
            tvAlertRight.setTextColor(rightTextColor);
        }

        tvAlertLeft.setOnClickListener(v -> {
            if (leftTextClickListener != null) {
                leftTextClickListener.onClick(CustomAlertDialog.this, 0);
            }
            dismiss();
        });

        tvAlertRight.setOnClickListener(v -> {
            if (rightTextClickListener != null) {
                rightTextClickListener.onClick(CustomAlertDialog.this, 1);
            }
            dismiss();
        });

        if (isHideAllBtn) { // 整个 buttons panel 不可见
            btnPanelDivider.setVisibility(View.GONE);
            buttonsPanel.setVisibility(View.GONE);
        } else if (isLeftBtnVisible && isRightBtnVisible) {
            btnDivider.setVisibility(View.VISIBLE);
        } else if (!isLeftBtnVisible && !isRightBtnVisible) {
            btnPanelDivider.setVisibility(View.GONE);
            buttonsPanel.setVisibility(View.GONE);
        } else {
            btnDivider.setVisibility(View.GONE);
        }

    }

    public void setIcon(@DrawableRes int iconId) {
        this.iconId = iconId;
    }

    @Override
    public void setTitle(CharSequence titleStr) {
        this.titleStr = titleStr;
    }

    private void setMessage(CharSequence message) {
        this.messageStr = message;
    }

    private void setMessageTextGravity(int gravity) {
        this.messageTextGravity = gravity;
    }

    public void setView(View customView) {
        this.customView = customView;
    }

    private void setLeftText(CharSequence leftText) {
        this.leftText = leftText;
    }

    private void setRightText(CharSequence rightText) {
        this.rightText = rightText;
    }

    private void setLeftTextColor(int leftTextColor) {
        this.leftTextColor = leftTextColor;
    }

    private void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
    }

    private void setLeftTextClickListener(OnClickListener leftTextClickListener) {
        this.leftTextClickListener = leftTextClickListener;
    }

    private void setRightTextClickListener(OnClickListener rightTextClickListener) {
        this.rightTextClickListener = rightTextClickListener;
    }

    private void setHideAllBtn(boolean hideAllBtn) {
        isHideAllBtn = hideAllBtn;
    }

    private void setIsCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public static class Builder {
        private final Context mContext;
        @DrawableRes
        private int iconId;
        private CharSequence titleText;
        private CharSequence messageText;
        private int messageTextGravity = Gravity.CENTER;
        private View view;
        private CharSequence leftText;
        private CharSequence rightText;
        private int leftTextColor;
        private int rightTextColor;
        private OnClickListener leftTextClickListener;
        private OnClickListener rightTextClickListener;
        private boolean hideAllBtn = false;
        private boolean cancelable = false;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setIcon(@DrawableRes int iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder setTitle(@StringRes int titleId) {
            titleText = mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            titleText = title;
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            messageText = mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            messageText = message;
            return this;
        }

        public Builder setMessageTextGravity(int gravity) {
            messageTextGravity = gravity;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setLeftText(@StringRes int leftTextId) {
            this.leftText = mContext.getText(leftTextId);
            return this;
        }

        public Builder setLeftText(CharSequence leftText) {
            this.leftText = leftText;
            return this;
        }

        public Builder setRightText(@StringRes int rightTextId) {
            this.rightText = mContext.getText(rightTextId);
            return this;
        }

        public Builder setRightText(CharSequence rightText) {
            this.rightText = rightText;
            return this;
        }

        public Builder setLeftTextColor(@ColorInt int leftTextColor) {
            this.leftTextColor = leftTextColor;
            return this;
        }

        public Builder setRightTextColor(@ColorInt int rightTextColor) {
            this.rightTextColor = rightTextColor;
            return this;
        }

        public Builder setLeftClickListener(final OnClickListener listener) {
            leftTextClickListener = listener;
            return this;
        }

        public Builder setRightClickListener(final OnClickListener listener) {
            rightTextClickListener = listener;
            return this;
        }

        public Builder setHideAllBtn(boolean hideAllBtn) {
            this.hideAllBtn = hideAllBtn;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public CustomAlertDialog create() {
            CustomAlertDialog dialog = new CustomAlertDialog(mContext);
            dialog.setIcon(iconId);
            dialog.setTitle(titleText);
            dialog.setMessage(messageText);
            dialog.setMessageTextGravity(messageTextGravity);
            dialog.setView(view);
            dialog.setLeftText(leftText);
            dialog.setRightText(rightText);
            dialog.setLeftTextColor(leftTextColor);
            dialog.setRightTextColor(rightTextColor);
            dialog.setLeftTextClickListener(leftTextClickListener);
            dialog.setRightTextClickListener(rightTextClickListener);
            dialog.setHideAllBtn(hideAllBtn);
            dialog.setIsCancelable(cancelable);
            return dialog;
        }

        public CustomAlertDialog show() {
            final CustomAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }

        public CustomAlertDialog showWithNoBar() {
            final CustomAlertDialog dialog = create();
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.show();
            fullScreen(dialog.getWindow().getDecorView());
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            return dialog;
        }

        private void fullScreen(View view){
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    /**
     * 会默认显示"取消"和"确定"两个按键的提醒型对话框的Builder
     */
    public static class CommonAlertBuilder extends Builder {
        public CommonAlertBuilder(Context context) {
            super(context);
            setLeftText(R.string.cancel);
            setRightText(R.string.confirm);
        }
    }

    /**
     * 会默认显示单个按键，"好的"的提示型对话框的Builder
     */
    public static class HintAlertBuilder extends Builder {
        public HintAlertBuilder(Context context) {
            super(context);
            setRightText(R.string.ok);
        }
    }
}
