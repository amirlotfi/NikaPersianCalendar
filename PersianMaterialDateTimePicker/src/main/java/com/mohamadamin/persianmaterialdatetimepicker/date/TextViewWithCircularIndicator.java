package com.mohamadamin.persianmaterialdatetimepicker.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.R;
import com.mohamadamin.persianmaterialdatetimepicker.utils.LanguageUtils;

public class TextViewWithCircularIndicator extends TextView {

    private static final int SELECTED_CIRCLE_ALPHA = 255;

    Paint mCirclePaint = new Paint();

    private final int mRadius;
    private final int mCircleColor;
    private final String mItemIsSelectedText;

    private boolean mDrawCircle;

    public TextViewWithCircularIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mCircleColor = res.getColor(R.color.mdtp_accent_color);
        mRadius = res.getDimensionPixelOffset(R.dimen.mdtp_month_select_circle_radius);
        mItemIsSelectedText = context.getResources().getString(R.string.mdtp_item_is_selected);

        init();
    }

    private void init() {
        mCirclePaint.setFakeBoldText(true);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setTextAlign(Align.CENTER);
        mCirclePaint.setStyle(Style.FILL);
        mCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);
    }

    public void drawIndicator(boolean drawCircle) {
        mDrawCircle = drawCircle;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (mDrawCircle) {
            final int width = getWidth();
            final int height = getHeight();
            int radius = Math.min(width, height) / 2;
            canvas.drawCircle(width / 2, height / 2, radius, mCirclePaint);
        }
        setSelected(mDrawCircle);
        super.onDraw(canvas);
    }

    @Override
    public CharSequence getContentDescription() {
        String itemText = LanguageUtils.getPersianNumbers(getText().toString());
        if (mDrawCircle) {
            return String.format(mItemIsSelectedText, itemText);
        } else {
            return itemText;
        }
    }
}
