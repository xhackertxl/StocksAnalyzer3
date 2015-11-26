package com.alex.develop.ui;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.alex.develop.entity.Enum.InputType;
import com.alex.develop.stockanalyzer.R;

/**
 * Created by alex on 15-8-15.
 * 一个输入股票代码的自定义键盘，便于股票搜索
 */
public class StockKeyboard extends KeyboardView {

    public StockKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    };

    public interface OnInputTypeChangeListener{
        void onChanged(InputType inputType);
    }

    @Override
    public void setKeyboard(Keyboard keyboard) {
        super.setKeyboard(keyboard);

        if(symbols == keyboard) {
            mListener.onChanged(InputType.Numeric);
        }

        if(qwerty == keyboard) {
            mListener.onChanged(InputType.Alphabet);
        }

        if(null != editText) {
            editText.setText("");
        }
    }

    public void setKeyboardLayout(int symbols, int qwerty, EditText editText) {
        this.symbols = new Keyboard(getContext(), symbols);
        this.qwerty = new Keyboard(getContext(), qwerty);
        this.editText = editText;
        setKeyboard(this.symbols);
        setOnKeyboardActionListener(onKeyboardActionListener);
    }

    public void setOnInputTypeChangeListener(OnInputTypeChangeListener listener) {
        mListener = listener;
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private OnKeyboardActionListener onKeyboardActionListener = new OnKeyboardActionListener() {

        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            // Get the EditText and its Editable
//            View focus = ((Activity) getContext()).getWindow().getCurrentFocus();
//            if(null == focus || EditText.class == focus.getClass()) {
//                return;
//            }

//            EditText editText = (EditText) focus;
            Editable editable = editText.getText();
            int start = editText.getSelectionStart();

            boolean insert = true;
            String data = null;

            if(KEY_600 == primaryCode) {
                data = getResources().getString(R.string.key_600);
            } else if(KEY_002 == primaryCode) {
                data = getResources().getString(R.string.key_002);
            } else if(KEY_300 == primaryCode) {
                data = getResources().getString(R.string.key_300);
            } else if(KEY_SPLIT == primaryCode) {
                String keyLabel = getResources().getString(R.string.key_split);
                if(0 < start) {
                    if (editable.charAt(start - 1) == keyLabel.charAt(0)) {
                        insert = false;
                    } else {
                        data = keyLabel;
                    }
                } else {
                    data = keyLabel;
                }
            } else if(KEY_DEL == primaryCode) {
                if(null != editable && 0 < start) {
                    editable.delete(start-1, start);
                }
                insert = false;
            } else if(KEY_ABC == primaryCode) {
                insert = false;
                setKeyboard(qwerty);
            } else if(KEY_OK == primaryCode) {
                insert = false;

            } else if(KEY_HID == primaryCode) {
                insert = false;
                hide();
            } else if(KEY_XXX == primaryCode) {
                insert = false;

            } else if(KEY_123 == primaryCode) {
                insert = false;
                setKeyboard(symbols);
            } else {
                data = Character.toString((char) primaryCode);
            }

            if(null != editable && insert) {
                editable.insert(start, data);
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }

        private final static int KEY_600 = 9901;
        private final static int KEY_002 = 9902;
        private final static int KEY_300 = 9903;
        private final static int KEY_SPLIT = 9904;
        private final static int KEY_DEL = 9905;
        private final static int KEY_ABC = 9906;
        private final static int KEY_OK  = 9907;
        private final static int KEY_HID = 9908;
        private final static int KEY_XXX = 9909;
        private final static int KEY_123 = 9910;
    };

    private EditText editText;
    private Keyboard symbols;// 数字键盘
    private Keyboard qwerty;// 字母键盘
    private OnInputTypeChangeListener mListener;// 输入方式
}
