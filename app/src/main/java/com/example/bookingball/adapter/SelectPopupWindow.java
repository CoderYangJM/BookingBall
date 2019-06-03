package com.example.bookingball.adapter;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.bookingball.R;

public class SelectPopupWindow extends PopupWindow {
    private Button cancelButton;
    private Button deleteButton;
    private Button updateButton;
    private Button settleButton;
    private View menu;

    public SelectPopupWindow(View popupView) {
        super(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//宽覆盖屏幕,高包含内容
        this.menu = popupView;
        cancelButton = menu.findViewById(R.id.popup_cancel_button);
        deleteButton = menu.findViewById(R.id.popup_delete_button);
        updateButton = menu.findViewById(R.id.popup_update_button);
        settleButton = menu.findViewById(R.id.popup_settle_button);
        //点击框外销毁弹出框
        menu.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                int height = menu.findViewById(R.id.popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void setCancelButtonListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setDeleteButtonListener(View.OnClickListener listener) {
        deleteButton.setOnClickListener(listener);
    }

    public void setUpdateButtonListener(View.OnClickListener listener) {
        updateButton.setOnClickListener(listener);
    }

    public void setSettleButtonListener(View.OnClickListener listener) {
        settleButton.setOnClickListener(listener);
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public Button getSettleButton() {
        return settleButton;
    }
}
