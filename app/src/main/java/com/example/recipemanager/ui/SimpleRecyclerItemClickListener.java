package com.example.recipemanager.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleRecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private final OnItemClickListener listener;
    private final GestureDetector gestureDetector;

    public SimpleRecyclerItemClickListener(Context context, OnItemClickListener listener) {
        this.listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) { return true; }
            @Override
            public void onLongPress(MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && listener != null) {
                    listener.onItemLongClick(child, rv.getChildAdapterPosition(child));
                }
            }
        });
    }

    private RecyclerView rv;

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        this.rv = rv;
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(child, rv.getChildAdapterPosition(child));
            return true;
        }
        return false;
    }
}
