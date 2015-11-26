package com.alex.develop.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.alex.develop.entity.*;
import com.alex.develop.entity.Enum;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.task.QueryStockHistory;
import com.alex.develop.util.UnitHelper;

import java.util.Random;

/**
 * Created by alex on 15-6-14.
 * 自定义View，主要用于绘制K线图
 */
public class CandleView extends View {

    public interface onCandlestickSelectedListener {
        void onSelected(Candlestick candlestick);
    }

    public CandleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setStock(final Stock stock) {
        this.stock = stock;
        cs.setCandleList(this.stock.getCandleList());
        cd.setCandleList(this.stock.getCandleList());

        // 重置游标成功，则说明已经下载过数据，无需重复下载
        if(stock.resetCursor()) {
            updateParameters();
            mListener.onSelected(stock.getToday());
            return ;
        }

        // 下载数据并重置游标
        requestData(true);
    }

    public void setOnCandlestickSelectedListener(onCandlestickSelectedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        float divider = h * 0.75f;

        if(null != kArea) {
            kArea.right = w;
            kArea.bottom = divider;
            Config.init(kArea.width());
            kCfg.setReferYpx(kArea.bottom);
            kCfg.setHeight(kArea.height());
        }

        if(null != qArea) {
            qArea.right = w;
            qArea.top = divider;
            qArea.bottom = h;
            qCfg.setReferYpx(qArea.bottom);
            qCfg.setHeight(qArea.height());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN :
                onActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE :
                onActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(event);
                break;
        }

        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        pen.setStyle(Paint.Style.STROKE);
        pen.setColor(Color.parseColor("#424242"));

        // 绘制区域边框
        canvas.drawRect(kArea, pen);
        canvas.drawRect(qArea, pen);

        // 绘制K线
        drawCandlesticks(canvas);

        // 绘制十字线及纵横坐标
        drawTextAndLine(canvas);

        // for test
        if(0 < stock.getCandleList().size()) {
            pen.setColor(Color.YELLOW);
            Candlestick st = stock.getStartCandle();
            canvas.drawCircle(st.getCenterXofArea(), st.getCenterYofArea(), 10, pen);

            Candlestick ed = stock.getEndCandle();
            canvas.drawCircle(ed.getCenterXofArea(), ed.getCenterYofArea(), 10, pen);
        }
    }

    public void updateParameters() {
        CandleList data = stock.getCandleList();
        kCfg.setValue(data.getHighest() - data.getLowest());
        kCfg.setReferValue(data.getLowest());

        qCfg.setValue(data.getVolume());
        qCfg.setReferValue(0);

        highestValue.setText(data.getHighest());
        lowestValue.setText(data.getLowest());

        invalidate();
    }

    public void cancelTask() {
        if(null != task && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    private void onActionDown(MotionEvent event) {
        downMillis = System.currentTimeMillis();
        down.set(event.getX(), event.getY());
        moveDis = 0;

        if(2 == event.getPointerCount()) {
            downDisWith2Pointer = Math.abs(event.getX(0) - event.getX(1));
        }
    }

    private void onActionMove(MotionEvent event) {
        moveMillis = System.currentTimeMillis();

        // 单指操作
        if(1 == event.getPointerCount()) {

            touch.set(event.getX(), event.getY());

            float moveX = touch.x - down.x;

            if(setMode) {

                /**
                 * {Enum.ActionMode.Select} 和 {Enum.ActionMode.Drag} 的区分
                 *
                 * 在ActionDown之后的{MODE_SELECT_TRIGGER}时间内，触点的水平位移
                 * Math.abs(moveX)小于{TREMBLE_LIMIT}距离[即：手指在抖动，并没有
                 * 发生实际位移]时为{Select}模式，否则为{Drag}模式
                 *
                 */

                if (MODE_SELECT_TRIGGER < moveMillis-downMillis) {
                    if(TREMBLE_LIMIT > Math.abs(moveX)) {
                        setMode = false;
                        mode = Enum.ActionMode.Select;
//                        Toast.makeText(getContext(), "Select Mode", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(TREMBLE_LIMIT < Math.abs(moveX)) {
                        setMode = false;
                        mode = Enum.ActionMode.Drag;
//                        Toast.makeText(getContext(), "Drag Mode", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            switch (mode) {
                case Select : {
                    selectCandlestick();
                    break;
                }
                case Drag : {
                    moveVisualZone();
                    break;
                }
            }

        }

        if(2 == event.getPointerCount()) {
            if(setMode) {
                setMode = false;
                mode = Enum.ActionMode.Scale;
                Toast.makeText(getContext(), "Scale Mode", Toast.LENGTH_SHORT).show();
            }

//            float moveDisWith2Pointer = Math.abs(event.getX(0) - event.getX(1));
//            if(moveDisWith2Pointer + 10 > downDisWith2Pointer) {
//                Config.setScaleFactor(moveDisWith2Pointer / downDisWith2Pointer);
//                updateParameters();
//            }
        }
    }

    private void onActionUp(MotionEvent event) {
        setMode = true;
        drawCross = false;
    }

    private void requestData(final boolean resetCursor) {
        task = new QueryStockHistory(stock) {

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

                if(resetCursor && stock.resetCursor()) {
                    updateParameters();
                    mListener.onSelected(stock.getToday());
                }
            }
        };

        task.execute(Enum.Period.Day);
    }

    private void selectCandlestick() {

        // 使得十字线自动吸附K线
        String[] temp  = String.format("%.2f", (touch.x-kArea.left) / (Config.getItemWidth() + Config.getItemSpace())).split("\\.");
        int intSub = Integer.valueOf(temp[0]);
        float floatSub = Float.valueOf("0." + temp[1]);

        if(floatSub > Config.ITEM_SPACE_WIDTH_RATIO/(1+Config.ITEM_SPACE_WIDTH_RATIO)) {
            ++intSub;
        }

        cs.copy(stock.getStart());
        cs.move(intSub);

        CandleList data = stock.getCandleList();
        Candlestick candle = data.get(cs.node).get(cs.candle);

        touch.x = candle.getCenterXofArea();
        mListener.onSelected(candle);

        float value = 0.00f;
        if(kArea.top < touch.y && touch.y < kArea.bottom) {
            value = kCfg.px2val(touch.y);
            inKArea = true;
        }

        if(qArea.top < touch.y && touch.y < qArea.bottom) {
            value = qCfg.px2val(touch.y);
            inKArea = false;
        }

        textValue.setText(value);
        dateValue.setText(candle.getDate());
        drawCross = true;
    }

    private void moveVisualZone() {
        final float dx = touch.x - down.x;
        moveDis += dx;

        int length = (int) (-dx / (Config.getItemWidth()+Config.getItemSpace()));
//        if(1 <= length) {
            stock.moveCursor(length);
            updateParameters();
//            moveDis = 0;
//        }

//        down.x = touch.x;
    }

    /**
     * 绘制K线
     */
    private void drawCandlesticks(Canvas canvas) {

        float x = kArea.left + Config.getItemSpace();

        CandleList data = stock.getCandleList();
        Cursor ed = stock.getEnd();
        Cursor st = stock.getStart();
        cd.copy(st);

        // 是否绘制MA
        boolean[] drawMA = new boolean[Constant.MA_COUNT];

        if(0 < data.size()) {
            for (int i = st.node; i >= ed.node; --i) {
                Node node = data.get(i);

                // 计算特殊情况下遍历的开始和结束的位置
                int start = i == st.node ? st.candle : 0;
                int stop = i == ed.node ? ed.candle : node.size() - 1;

                for (int j = start; j <= stop; ++j) {
                    Candlestick candle = node.get(j);
                    candle.drawCandle(x, kCfg, canvas, pen);
                    candle.drawVOL(x, qCfg, canvas, pen);
                    x += Config.getItemWidth() + Config.getItemSpace();

                    cd.node = i;
                    cd.candle = j;
                    cd.calculateMA();

                    for(int k=0; k<maPath.length; ++k) {
                        final float ma = candle.getMaByIndex(k);
                        if(drawMA[k]) {
                            maPath[k].lineTo(candle.getCenterXofArea(), kCfg.val2px(ma));
                        } else {
                            if(0.0f < ma) {
                                maPath[k].reset();
                                maPath[k].moveTo(candle.getCenterXofArea(), kCfg.val2px(ma));
                                drawMA[k] = true;
                            }
                        }
                    }
                }
            }
        }

        pen.setStyle(Paint.Style.STROKE);
        for(int i=0; i<maPath.length; ++i) {

            if(drawMA[i]) {
                pen.setColor(getResources().getColor(colors[i]));
                canvas.drawPath(maPath[i], pen);
            }
        }
    }

    private void drawTextAndLine(Canvas canvas) {

        CandleList data = stock.getCandleList();

        // 绘制可视区域内股票的最高价和最低价
        if(0 < data.size()) {
            Candlestick highest = data.getCandlestickHigh();
            Candlestick lowest = data.getCandlestickLow();

            float[] high = getDrawTextXY(highest, true, highestValue);
            pen.setColor(highestValue.getTextColor());
            canvas.drawLine(high[0], high[1], high[2], high[3], pen);
            highestValue.draw(high[4], high[5], canvas);

            float[] low = getDrawTextXY(lowest, false, lowestValue);
            pen.setColor(lowestValue.getTextColor());
            canvas.drawLine(low[0], low[1], low[2], low[3], pen);
            lowestValue.draw(low[4], low[5], canvas);
        }

        // 绘制十字线及其对应得坐标
        if (drawCross && Enum.ActionMode.Select == mode) {

            // 绘制十字线
            pen.setColor(Color.WHITE);
            canvas.drawLine(kArea.left, touch.y, kArea.right, touch.y, pen);
            canvas.drawLine(touch.x, 0, touch.x, height, pen);

            // 绘制横坐标

            float x1 = 0;
            float y1 = touch.y - textValue.getBound().height() / 2;

            // 考虑[上下]边界情况
            if(inKArea) {
                y1 = kArea.top > y1 ? kArea.top : y1;
                if (kArea.bottom < y1 + textValue.getBound().height()) {
                    y1 = kArea.bottom - textValue.getBound().height();
                }
            } else {
                y1 = qArea.top > y1 ? qArea.top : y1;
                if (qArea.bottom < y1 + textValue.getBound().height()) {
                    y1 = qArea.bottom - textValue.getBound().height();
                }
            }

            // 手指移出kArea顶部的时候显示最大值
            if(y1 == kArea.top) {
                textValue.setText(data.getHighest());
            }

            textValue.draw(x1, y1, canvas);// 绘制K线纵坐标(价格)

            float x2 = touch.x - dateValue.getBound().width() / 2;
            float y2 = kArea.bottom - dateValue.getBound().height();

            // 考虑[左右]边界情况
            x2 = 0 > x2 ? 0 : x2;
            if(kArea.right < x2 + dateValue.getBound().width()) {
                x2 = kArea.right - dateValue.getBound().width();
            }
            dateValue.draw(x2, y2, canvas);// 绘制K线日期
        }
    }

    /**
     * 计算绘制最高价和最低价所需的坐标信息
     * @param candle 目标K线
     * @param highOrLow true，最高价；false，最低价
     * @param textValue 将被绘制的价格字符串
     * @return
     *
     * [0] : 价格指示线条的起始坐标X
     * [1] : 价格指示线条的起始坐标Y
     * [2] : 价格指示线条的结束坐标X
     * [3] : 价格指示线条的结束坐标Y
     * [4] : 价格字符串绘制的起始坐标X
     * [5] : 价格字符串绘制的起始坐标Y
     */
    private float[] getDrawTextXY(Candlestick candle, boolean highOrLow, TextValue textValue) {

        float[] data = new float[6];

        if(null == candle) {
            return data;
        }

        data[0] = candle.getCenterXofArea();
        data[1] = kCfg.val2px(highOrLow ? candle.getHigh() : candle.getLow());

        if(highOrLow) {
            data[3] = data[1] + Config.ITEM_MARK_OFFSET_Y;
        } else {
            data[3] = data[1] - Config.ITEM_MARK_OFFSET_Y;
        }

        data[5] = data[3] - textValue.getBound().height() / 2;

        if(kArea.left > data[0]-Config.ITEM_MARK_OFFSET_X-textValue.getBound().width()) {// 必须绘制在右侧
            data[2] = data[0] + Config.ITEM_MARK_OFFSET_X;
            data[4] = data[2];
        } else if(kArea.right < data[0]+Config.ITEM_MARK_OFFSET_X+highestValue.getBound().width()) {// 必须绘制在左侧
            data[2] = data[0] - Config.ITEM_MARK_OFFSET_X;
            data[4] = data[2] - textValue.getBound().width();
        } else {// 左右皆可

            // 保证当最高价K线和最低价K线都在屏幕中间时，两者的指示线方向相反
            boolean flag = highOrLow ? highLeft : lowLeft;

            if(flag) {// 左侧
                data[2] = data[0] - Config.ITEM_MARK_OFFSET_X;
                data[4] = data[2] - textValue.getBound().width();
            } else {// 右侧
                data[2] = data[0] + Config.ITEM_MARK_OFFSET_X;
                data[4] = data[2];
            }
        }

        return data;
    }

    private void initialize() {
        pen = new Paint();
        pen.setTextSize(30);
        pen.setAntiAlias(true);
        pen.setStyle(Paint.Style.FILL_AND_STROKE);
        pen.setStrokeWidth(UnitHelper.dp2px(1));

        maPath = new Path[Constant.MA_COUNT];
        for(int i=0; i<maPath.length; ++i) {
            maPath[i] = new Path();
        }

        colors = new int[] {
                R.color.ma_color_0,
                R.color.ma_color_1,
                R.color.ma_color_2,
                R.color.ma_color_3,
                R.color.ma_color_4,
                R.color.ma_color_5
        };

        down = new PointF();
        touch = new PointF();
        kArea = new RectF();
        kCfg = new Config();

        qArea = new RectF();
        qCfg = new Config();

        final float textSize = UnitHelper.sp2px(15);

        textValue = new TextValue();
        textValue.setTextSize(textSize);

        dateValue = new TextValue();
        dateValue.setTextSize(textSize);

        highestValue = new TextValue();
        highestValue.setTextSize(textSize);
        highestValue.setShowBorder(false);
        highestValue.setAlpha(255);

        lowestValue = new TextValue();
        lowestValue.setTextSize(textSize);
        lowestValue.setShowBorder(false);
        lowestValue.setAlpha(255);

        cs = new Cursor();
        cd = new Cursor();

        highLeft = new Random().nextBoolean();
        lowLeft = !highLeft;

        inKArea = true;
        setMode = true;

        mode = Enum.ActionMode.Select;
    }

    private Paint pen;// 画笔
    private Path[] maPath;
    private int[] colors;

    private PointF down;// 落点
    private PointF touch;// 触点，当用户点击K线图形时，绘制十字线，用于告知用户当前查看的是那一天的K线

    private RectF kArea;// 绘制K线部分图形区域
    private Config kCfg;// K线图的配置信息
    private RectF qArea;// 绘制指标部分区域
    private Config qCfg;// 指标图的配置信息

    private TextValue textValue;
    private TextValue dateValue;
    private TextValue highestValue;
    private TextValue lowestValue;

    private Stock stock;
    private Cursor cs;// 指向当前K线的游标
    private Cursor cd;// 指向当前正在被绘制的K线的游标
    private QueryStockHistory task;

    private onCandlestickSelectedListener mListener;

    private boolean drawCross;// 是否绘制十字线
    private boolean highLeft;// 绘制最高价的指示线是否向左
    private boolean lowLeft;// 绘制最低价的指示线是否向左
    private boolean inKArea;// 手指是否在K线区域内
    private boolean setMode;// 是否允许设置模式

    private Enum.ActionMode mode;// 触摸屏幕时的操作

    private int width;// View的宽度
    private int height;// View的高度

    private long downMillis;// ActionDown的时刻
    private long moveMillis;// ActionMove的时刻

    private float moveDis;
    private float downDisWith2Pointer;

    private final int MODE_SELECT_TRIGGER = 100;
    private final int TREMBLE_LIMIT = (int) UnitHelper.dp2px(2);
}
