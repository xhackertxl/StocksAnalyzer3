package com.alex.develop.entity;

import com.alex.develop.util.UnitHelper;

/**
 * Created by alex on 15-8-4.
 * 根据画板的尺寸来计算K线显示的宽度、间距以及某种
 * 类型的值与画板尺寸单位px的比值（用于数值转换）
 */
public class Config {

    /**
     * 计算K线的宽度和间距(px)
     * @param width 画板（绘制K线区域）的宽度
     */
    public static void init(float width) {
        itemWidth = width / (ITEM_AMOUNTS + (1 + ITEM_AMOUNTS) * ITEM_SPACE_WIDTH_RATIO);
        itemSpace = itemWidth * ITEM_SPACE_WIDTH_RATIO;
        factor = 1.0f;
    }

    public static void setScaleFactor(float factor) {
        Config.factor = factor;
    }

    public static float getItemWidth() {
        return itemWidth * factor;
    }

    public static float getItemSpace() {
        return itemSpace * factor;
    }

    /**
     * 绘图区域的高度
     * @param height
     */
    public void setHeight(float height) {
        this.height = height;
        getRatio();
    }

    /**
     * 绘图区域表示的值的大小
     * @param value
     */
    public void setValue(float value) {
        this.value = value;
        getRatio();
    }

    /**
     * 设置y=0时，对应屏幕Y方向上的坐标(px)
     * @param px y=0时，对应在屏幕上的位置
     */
    public void setReferYpx(float px) {
        referY = px;
    }

    /**
     * 设置y=0时，对应的某种量的值
     * @param value y=0时，对应的某种量的值
     */
    public void setReferValue(float value) {
        referV = value;
    }

    /**
     * 将某种量转换为px
     * @param value 某种量的值
     * @return px
     */
    public float val2px(float value) {
        return referY - (value - referV) / ratio;
    }

    /**
     * px转某种量的值
     * @param px
     * @return
     */
    public float px2val(float px) {
        return referV + (referY - px) * ratio;
    }

    /**
     * 计算height和value的比值
     */
    private void getRatio() {
        if(0.0f < value && 0.0f < height) {
            ratio = value / height;
        }
    }

    public static final int ITEM_AMOUNTS = 30;// 初始状态下，屏幕上显示的K线个数
    public static final float ITEM_SPACE_WIDTH_RATIO = 0.3f;// K线间隔占K线宽度的比例

    public static final float ITEM_MARK_OFFSET_X = UnitHelper.dp2px(20);
    public static final float ITEM_MARK_OFFSET_Y = UnitHelper.dp2px(5);

    private static float itemWidth;// K线的宽度
    private static float itemSpace;// K线的间隔
    private static float factor;

    private float height;// 绘图区域的高度
    private float value;// 绘图区域表示值的大小

    private float referV;// y=0时，对应的某种量的值
    private float referY;// y=0时，对应在屏幕上的位置
    private float ratio;// 某种量与画布高度的比值

}
