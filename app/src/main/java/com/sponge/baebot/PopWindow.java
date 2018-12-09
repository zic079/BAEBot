package com.sponge.baebot;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class PopWindow extends android.widget.PopupWindow
{
    Context ctx;
    TextView lblText;
    View popupView;

    public PopWindow(Context context, Task t)
    {
        super(context);

        ctx = context;
        popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
        setContentView(popupView);

        lblText = popupView.findViewById(R.id.pop_content);
        lblText.setText(t.getDescription());

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        setFocusable(true);

        // Removes default black background
        setBackgroundDrawable(new ColorDrawable());

    } // End constructor

    // Attaches the view to its parent anchor-view at position x and y
    public void show(View anchor, int x, int y) {
        showAtLocation(anchor, Gravity.CENTER, x, y);
    }
}
