package com.testdemo.testVerticalScrollView;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

import java.util.regex.Pattern;

/**
 * 按"137 6301 1886"格式显示电话
 * Created by greyson on 2018/9/4.
 */
public class PhoneEditText extends AppCompatEditText {

    public PhoneEditText(Context context) {
        super(context);
        init();
    }

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (text.length() <= 0) {
            return;
        }

        if (text.length() >= 10 && Pattern.compile("^\\d{3} \\d{4} \\d{1,4}$").matcher(text).matches()
                || text.length() >= 5 && Pattern.compile("^\\d{3} \\d{1,4}$").matcher(text).matches()
                || Pattern.compile("^\\d{1,3}$").matcher(text).matches()) {
            return;
        } else {

            boolean isAddAction = lengthBefore == 0 && lengthAfter == 1;

            int selection = getSelectionStart();
            StringBuilder result = new StringBuilder(text.toString().replace(" ", ""));
            if (result.length() < 4) {
                setText(result);
                setSelection(selection > result.length() ? result.length() : selection);
                return;
            }

            if (result.length() >= 4) {
                if (result.charAt(3) != ' ') {
                    result.insert(3, ' ');
                    if (isAddAction && selection == 4)
                        selection++;
                }
            }

            if (result.length() >= 9) {
                if (result.charAt(8) != ' ') {
                    result.insert(8, ' ');
                    if (isAddAction && selection == 9)
                        selection++;
                }
            }

            if (result.length() > 13) {//只保留13位
                result.delete(13, result.length());
            }

            setText(result);
            setSelection(selection > result.length() ? result.length() : selection);

        }
    }

    public String getPhone() {
        String result = getText().toString();
        if (result == null || result.length() == 0) {
            return "";
        } else {
            return result.replace(" ", "");
        }
    }
}
