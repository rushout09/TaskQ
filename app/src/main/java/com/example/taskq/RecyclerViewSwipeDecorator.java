package com.example.taskq;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;



/**

 * A simple utility class to add a background and/or an icon while swiping a RecyclerView item left or right.

 */



public class RecyclerViewSwipeDecorator {

    private Context context;

    private Canvas canvas;

    private RecyclerView recyclerView;

    private RecyclerView.ViewHolder viewHolder;

    private float dX;

    private float dY;

    private int actionState;

    private boolean isCurrentlyActive;



    private int swipeLeftBackgroundColor;

    private int swipeLeftActionIconId;



    private int swipeRightBackgroundColor;

    private int swipeRightActionIconId;



    private int iconHorizontalMargin;



    private String mSwipeLeftText;

    private float mSwipeLeftTextSize = 14;

    private int mSwipeLeftTextUnit = TypedValue.COMPLEX_UNIT_SP;

    private int mSwipeLeftTextColor = Color.DKGRAY;

    private Typeface mSwipeLeftTypeface = Typeface.SANS_SERIF;



    private String mSwipeRightText;

    private float mSwipeRightTextSize = 14;

    private int mSwipeRightTextUnit = TypedValue.COMPLEX_UNIT_SP;

    private int mSwipeRightTextColor = Color.DKGRAY;

    private Typeface mSwipeRightTypeface = Typeface.SANS_SERIF;



    private RecyclerViewSwipeDecorator() {

        swipeLeftBackgroundColor = 0;

        swipeRightBackgroundColor = 0;

        swipeLeftActionIconId = 0;

        swipeRightActionIconId = 0;

    }



    /**

     * Create a @RecyclerViewSwipeDecorator

     * @param context A valid Context object for the RecyclerView

     * @param canvas The canvas which RecyclerView is drawing its children

     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to

     * @param viewHolder The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position

     * @param dX The amount of horizontal displacement caused by user's action

     * @param dY The amount of vertical displacement caused by user's action

     * @param actionState The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.

     * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state

     */

    public RecyclerViewSwipeDecorator(Context context, Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        this();

        this.context = context;

        this.canvas = canvas;

        this.recyclerView = recyclerView;

        this.viewHolder = viewHolder;

        this.dX = dX;

        this.dY = dY;

        this.actionState = actionState;

        this.isCurrentlyActive = isCurrentlyActive;

        this.iconHorizontalMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());

    }



    /**

     * Set the background color for either (left/right) swipe directions

     * @param backgroundColor The resource id of the background color to be set

     */

    public void setBackgroundColor(int backgroundColor) {

        this.swipeLeftBackgroundColor = backgroundColor;

        this.swipeRightBackgroundColor = backgroundColor;

    }



    /**

     * Set the action icon for either (left/right) swipe directions

     * @param actionIconId The resource id of the icon to be set

     */

    public void setActionIconId(int actionIconId) {

        this.swipeLeftActionIconId = actionIconId;

        this.swipeRightActionIconId = actionIconId;

    }



    /**

     * Set the background color for left swipe direction

     * @param swipeLeftBackgroundColor The resource id of the background color to be set

     */

    public void setSwipeLeftBackgroundColor(int swipeLeftBackgroundColor) {

        this.swipeLeftBackgroundColor = swipeLeftBackgroundColor;

    }



    /**

     * Set the action icon for left swipe direction

     * @param swipeLeftActionIconId The resource id of the icon to be set

     */

    public void setSwipeLeftActionIconId(int swipeLeftActionIconId) {

        this.swipeLeftActionIconId = swipeLeftActionIconId;

    }



    /**

     * Set the background color for right swipe direction

     * @param swipeRightBackgroundColor The resource id of the background color to be set

     */

    public void setSwipeRightBackgroundColor(int swipeRightBackgroundColor) {

        this.swipeRightBackgroundColor = swipeRightBackgroundColor;

    }



    /**

     * Set the action icon for right swipe direction

     * @param swipeRightActionIconId The resource id of the icon to be set

     */

    public void setSwipeRightActionIconId(int swipeRightActionIconId) {

        this.swipeRightActionIconId = swipeRightActionIconId;

    }



    /**

     * Set the label shown when swiping right

     * @param label a String

     */

    public void setSwipeRightLabel(String label) {

        mSwipeRightText = label;

    }



    /**

     * Set the size of the text shown when swiping right

     * @param unit TypedValue (default is COMPLEX_UNIT_SP)

     * @param size the size value

     */

    public void setSwipeRightTextSize(int unit, float size) {

        mSwipeRightTextUnit = unit;

        mSwipeRightTextSize = size;

    }



    /**

     * Set the color of the text shown when swiping right

     * @param color the color to be set

     */

    public void setSwipeRightTextColor(int color) {

        mSwipeRightTextColor = color;

    }



    /**

     * Set the Typeface of the text shown when swiping right

     * @param typeface the Typeface to be set

     */

    public void setSwipeRightTypeface(Typeface typeface) {

        mSwipeRightTypeface = typeface;

    }



    /**

     * Set the horizontal margin of the icon (default is 16dp)

     * @param iconHorizontalMargin the margin in pixels

     */

    public void setIconHorizontalMargin(int iconHorizontalMargin) {

        this.iconHorizontalMargin = iconHorizontalMargin;

    }



    /**

     * Set the label shown when swiping left

     * @param label a String

     */

    public void setSwipeLeftLabel(String label) {

        mSwipeLeftText = label;

    }



    /**

     * Set the size of the text shown when swiping left

     * @param unit TypedValue (default is COMPLEX_UNIT_SP)

     * @param size the size value

     */

    public void setSwipeLeftTextSize(int unit, float size) {

        mSwipeLeftTextUnit = unit;

        mSwipeLeftTextSize = size;

    }



    /**

     * Set the color of the text shown when swiping left

     * @param color the color to be set

     */

    public void setSwipeLeftTextColor(int color) {

        mSwipeLeftTextColor = color;

    }



    /**

     * Set the Typeface of the text shown when swiping left

     * @param typeface the Typeface to be set

     */

    public void setSwipeLeftTypeface(Typeface typeface) {

        mSwipeLeftTypeface = typeface;

    }



    /**

     * Decorate the RecyclerView item with the chosen backgrounds and icons

     */

    public void decorate() {

        try {

            if ( actionState != ItemTouchHelper.ACTION_STATE_SWIPE ) return;
            if ( dX > 0 ) {

                // Swiping Right

                if ( swipeRightBackgroundColor != 0 ) {

                    final ColorDrawable background = new ColorDrawable(swipeRightBackgroundColor);

                    background.setBounds(0, viewHolder.itemView.getTop(), viewHolder.itemView.getLeft() + (int) dX, viewHolder.itemView.getBottom());

                    background.draw(canvas);

                }



                int iconSize = 0;

                if ( swipeRightActionIconId != 0 ) {

                    Drawable icon = ContextCompat.getDrawable(context, swipeRightActionIconId);

                    iconSize = icon.getIntrinsicHeight();

                    int halfIcon = iconSize / 2;

                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 - halfIcon);

                    icon.setBounds(iconHorizontalMargin, top, iconHorizontalMargin + icon.getIntrinsicWidth(), top + icon.getIntrinsicHeight());

                    icon.draw(canvas);

                }



                if ( mSwipeRightText != null && mSwipeRightText.length() > 0 ) {

                    TextPaint textPaint = new TextPaint();

                    textPaint.setAntiAlias(true);

                    textPaint.setTextSize(TypedValue.applyDimension(mSwipeRightTextUnit, mSwipeRightTextSize, context.getResources().getDisplayMetrics()));

                    textPaint.setColor(mSwipeRightTextColor);

                    textPaint.setTypeface(mSwipeRightTypeface);



                    int textTop = (int) (viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 ) + textPaint.getTextSize()/2);

                    canvas.drawText(mSwipeRightText, iconHorizontalMargin + iconSize + (iconSize > 0 ? iconHorizontalMargin/2 : 0), textTop,textPaint);

                }


            } else if (dX < 0) {

                // Swiping Left

                if ( swipeLeftBackgroundColor != 0 ) {

                    final ColorDrawable background = new ColorDrawable(swipeLeftBackgroundColor);

                    background.setBounds(viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());

                    background.draw(canvas);

                }



                int imgLeft = viewHolder.itemView.getRight();

                if ( swipeLeftActionIconId != 0 ) {

                    Drawable icon = ContextCompat.getDrawable(context, swipeLeftActionIconId);

                    int halfIcon = icon.getIntrinsicHeight() / 2;

                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2 - halfIcon);

                    imgLeft = viewHolder.itemView.getRight() - iconHorizontalMargin - halfIcon * 2;

                    icon.setBounds(imgLeft, top, viewHolder.itemView.getRight() - iconHorizontalMargin, top + icon.getIntrinsicHeight());

                    icon.draw(canvas);

                }



                if ( mSwipeLeftText != null && mSwipeLeftText.length() > 0 ) {

                    TextPaint textPaint = new TextPaint();

                    textPaint.setAntiAlias(true);

                    textPaint.setTextSize(TypedValue.applyDimension(mSwipeLeftTextUnit, mSwipeLeftTextSize, context.getResources().getDisplayMetrics()));

                    textPaint.setColor(mSwipeLeftTextColor);

                    textPaint.setTypeface(mSwipeLeftTypeface);



                    float width = textPaint.measureText(mSwipeLeftText);

                    int textTop = (int) (viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2) + textPaint.getTextSize() / 2);

                    canvas.drawText(mSwipeLeftText, imgLeft - width - ( imgLeft == viewHolder.itemView.getRight() ? iconHorizontalMargin : iconHorizontalMargin/2 ), textTop, textPaint);

                }

            }

        } catch(Exception e) {

            Log.e(this.getClass().getName(), e.getMessage());

        }

    }



    /**

     * A Builder for the RecyclerViewSwipeDecorator class

     */

    public static class Builder {

        private RecyclerViewSwipeDecorator mDecorator;



        /**

         * Create a builder for a RecyclerViewsSwipeDecorator

         * @param context A valid Context object for the RecyclerView

         * @param canvas The canvas which RecyclerView is drawing its children

         * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to

         * @param viewHolder The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position

         * @param dX The amount of horizontal displacement caused by user's action

         * @param dY The amount of vertical displacement caused by user's action

         * @param actionState The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.

         * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state

         */

        public Builder(Context context , Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            mDecorator = new RecyclerViewSwipeDecorator(

                    context,

                    canvas,

                    recyclerView,

                    viewHolder,

                    dX,

                    dY,

                    actionState,

                    isCurrentlyActive

            );

        }



        /**

         * Adds a background color to both swiping directions

         * @param color A single color value in the form 0xAARRGGBB

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addBackgroundColor(int color) {

            mDecorator.setBackgroundColor(color);

            return this;

        }



        /**

         * Add an action icon to both swiping directions

         * @param drawableId The resource id of the icon to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addActionIcon(int drawableId) {

            mDecorator.setActionIconId(drawableId);

            return this;

        }



        /**

         * Add a background color while swiping right

         * @param color A single color value in the form 0xAARRGGBB

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addSwipeRightBackgroundColor(int color) {

            mDecorator.setSwipeRightBackgroundColor(color);

            return this;

        }



        /**

         * Add an action icon while swiping right

         * @param drawableId The resource id of the icon to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addSwipeRightActionIcon(int drawableId) {

            mDecorator.setSwipeRightActionIconId(drawableId);

            return this;

        }



        /**

         * Add a label to be shown while swiping right

         * @param label The string to be shown as label

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addSwipeRightLabel(String label) {

            mDecorator.setSwipeRightLabel(label);

            return this;

        }



        /**

         * Set the color of the label to be shown while swiping right

         * @param color the color to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setSwipeRightLabelColor(int color) {

            mDecorator.setSwipeRightTextColor(color);

            return this;

        }



        /**

         * Set the size of the label to be shown while swiping right

         * @param unit the unit to convert from

         * @param size the size to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setSwipeRightLabelTextSize(int unit, float size) {

            mDecorator.setSwipeRightTextSize(unit, size);

            return this;

        }



        /**

         * Set the Typeface of the label to be shown while swiping right

         * @param typeface the Typeface to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setSwipeRightLabelTypeface(Typeface typeface) {

            mDecorator.setSwipeRightTypeface(typeface);

            return this;

        }



        /**

         * Adds a background color while swiping left

         * @param color A single color value in the form 0xAARRGGBB

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addSwipeLeftBackgroundColor(int color) {

            mDecorator.setSwipeLeftBackgroundColor(color);

            return this;

        }



        /**

         * Add an action icon while swiping left

         * @param drawableId The resource id of the icon to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addSwipeLeftActionIcon(int drawableId) {

            mDecorator.setSwipeLeftActionIconId(drawableId);

            return this;

        }



        /**

         * Add a label to be shown while swiping left

         * @param label The string to be shown as label

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder addSwipeLeftLabel(String label) {

            mDecorator.setSwipeLeftLabel(label);

            return this;

        }



        /**

         * Set the color of the label to be shown while swiping left

         * @param color the color to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setSwipeLeftLabelColor(int color) {

            mDecorator.setSwipeLeftTextColor(color);

            return this;

        }



        /**

         * Set the size of the label to be shown while swiping left

         * @param unit the unit to convert from

         * @param size the size to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setSwipeLeftLabelTextSize(int unit, float size) {

            mDecorator.setSwipeLeftTextSize(unit, size);

            return this;

        }



        /**

         * Set the Typeface of the label to be shown while swiping left

         * @param typeface the Typeface to be set

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setSwipeLeftLabelTypeface(Typeface typeface) {

            mDecorator.setSwipeLeftTypeface(typeface);

            return this;

        }



        /**

         * Set icon horizontal margin (default is 16dp)

         * @param pixels margin in pixels

         * @return This instance of @RecyclerViewSwipeDecorator.Builder

         */

        public Builder setIconHorizontalMargin(int pixels) {

            mDecorator.setIconHorizontalMargin(pixels);

            return this;

        }



        /**

         * Create a RecyclerViewSwipeDecorator

         * @return The created @RecyclerViewSwipeDecorator

         */

        public RecyclerViewSwipeDecorator create() {

            return mDecorator;

        }

    }

}