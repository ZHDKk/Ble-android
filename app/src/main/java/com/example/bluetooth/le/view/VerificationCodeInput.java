package com.example.bluetooth.le.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.bluetooth.le.R;

/**
 * Created by zhdk on 2017/10/16.
 */

public class VerificationCodeInput extends ViewGroup{
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_PASSWORD = "password";
    private static final String TYPE_PHONE = "phone";
    private static final String TAG = "VerificationCodeInput";
    private int box = 4;
    private int boxWidth = 120;
    private int boxHeight = 120;
    private int childHPadding = 14;
    private int childVPadding = 14;
    private String inputType = "password";
    private Drawable boxBgFocus = null;
    private Drawable boxBgNormal = null;
    private VerificationCodeInput.Listener listener;
    private EditText editText;
    private StringBuilder stringBuilder;

    public VerificationCodeInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.vericationCodeInput);
        this.box = a.getInt(R.styleable.vericationCodeInput_box, 4);
        this.childHPadding = (int)a.getDimension(R.styleable.vericationCodeInput_child_h_padding, 0.0F);
        this.childVPadding = (int)a.getDimension(R.styleable.vericationCodeInput_child_v_padding, 0.0F);
        this.boxBgFocus = a.getDrawable(R.styleable.vericationCodeInput_box_bg_focus);
        this.boxBgNormal = a.getDrawable(R.styleable.vericationCodeInput_box_bg_normal);
        this.inputType = a.getString(R.styleable.vericationCodeInput_inputType);
        this.initViews();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initViews() {
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    VerificationCodeInput.this.focus();
                    VerificationCodeInput.this.checkAndCommit();
                }

            }
        };
        OnKeyListener onKeyListener = new OnKeyListener() {
            public synchronized boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == 67) {
                    VerificationCodeInput.this.backFocus();
                }

                return false;
            }
        };

        for(int i = 0; i < this.box; ++i) {
             editText = new EditText(this.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.boxWidth,this.boxHeight);
            layoutParams.bottomMargin = this.childVPadding;
            layoutParams.topMargin = this.childVPadding;
            layoutParams.leftMargin = this.childHPadding;
            layoutParams.rightMargin = this.childHPadding;
            layoutParams.gravity = 17;
            editText.setOnKeyListener(onKeyListener);
            this.setBg(editText, false);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(17);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            if("number".equals(this.inputType)) {
                editText.setInputType(2);
            } else if("password".equals(this.inputType)) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
            } else if("text".equals(this.inputType)) {
                editText.setInputType(1);
            } else if("phone".equals(this.inputType)) {
                editText.setInputType(3);
            }

            editText.setId(i);
            editText.setEms(1);
            editText.addTextChangedListener(textWatcher);
            this.addView(editText, i);
        }

    }

    private void backFocus() {
        int count = this.getChildCount();
        for(int i = count-1; i >= 0; i--) {
            EditText editText = (EditText)this.getChildAt(i);
            if(!editText.getText().toString().trim().equals("") ) {
                editText.requestFocus();
                editText.setSelection(1);
                return;
            }
        }

    }

    private void focus() {
        int count = this.getChildCount();

        for(int i = 0; i < count; ++i) {
            EditText editText = (EditText)this.getChildAt(i);
            if(editText.getText().length()<1) {
                editText.requestFocus();
                return;
            }
        }

    }

    private void setBg(EditText editText, boolean focus) {
        if(this.boxBgNormal != null && !focus) {
            editText.setBackground(this.boxBgNormal);
        } else if(this.boxBgFocus != null && focus) {
            editText.setBackground(this.boxBgFocus);
        }

    }

    private void checkAndCommit() {
         stringBuilder = new StringBuilder();
        boolean full = true;

        for(int i = 0; i < this.box; ++i) {
            EditText editText = (EditText)this.getChildAt(i);
            String content = editText.getText().toString();
            if(content.length() == 0) {
                full = false;
                break;
            }

            stringBuilder.append(content);
        }

        Log.d("VerificationCodeInput", "checkAndCommit:" + stringBuilder.toString());
        if(full && this.listener != null) {
            this.listener.onComplete(stringBuilder.toString());
//            this.setEnabled(false);
        }

    }

    public void setEnabled(boolean enabled) {
        int childCount = this.getChildCount();

        for(int i = 0; i < childCount; ++i) {
            View child = this.getChildAt(i);
            child.setEnabled(enabled);
        }

    }

    public void clearText(){
        int count = this.getChildCount();
        for(int i = 0; i < count; ++i) {
            EditText editText = (EditText)this.getChildAt(i);
            editText.setText("");
            editText.clearFocus();
        }
    }

    public void setOnCompleteListener(VerificationCodeInput.Listener listener) {
        this.listener = listener;
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(this.getClass().getName(), "onMeasure");
        int count = this.getChildCount();

        for(int child = 0; child < count; ++child) {
            View cHeight = this.getChildAt(child);
            this.measureChild(cHeight, widthMeasureSpec, heightMeasureSpec);
        }

        if(count > 0) {
            View var9 = this.getChildAt(0);
            int var10 = var9.getMeasuredHeight();
            int cWidth = var9.getMeasuredWidth();
            int maxH = var10 + 2 * this.childVPadding;
            int maxW = (cWidth + this.childHPadding) * this.box + this.childHPadding;
            this.setMeasuredDimension(resolveSize(maxW, widthMeasureSpec), resolveSize(maxH, heightMeasureSpec));
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(this.getClass().getName(), "onLayout");
        int childCount = this.getChildCount();

        for(int i = 0; i < childCount; ++i) {
            View child = this.getChildAt(i);
            child.setVisibility(0);
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            int cl = i * (cWidth + this.childHPadding);
            int cr = cl + cWidth;
            int ct = this.childVPadding;
            int cb = ct + cHeight;
            child.layout(cl, ct, cr, cb);
        }

    }

    public interface Listener {
        void onComplete(String var1);
    }
}
