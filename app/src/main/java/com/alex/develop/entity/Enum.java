package com.alex.develop.entity;

/**
 * Created by alex on 15-6-17.
 * 定义一些枚举类型
 */
public final class Enum extends BaseObject {

    public interface EnumType {}

    /**
     * 排序方式
     */
    public enum Order implements EnumType {
        ASC,// 正序
        DESC;// 逆序

        @Override
        public String toString() {
            switch (this) {
                case ASC:
                    return "A";
                case DESC:
                    return "D";
                default:
                    return super.toString();
            }
        }
    }

    /**
     * 周期
     */
    public enum Period implements EnumType {
        Day,// 天
        Week,// 周
        Month;// 月

        @Override
        public String toString() {
            switch (this) {
                case Day:
                    return "d";
                case Week:
                    return "w";
                case Month:
                    return "m";
                default:
                    return super.toString();
            }
        }
    }

    /**
     * 月份
     */
    public enum Month implements EnumType {
        Jan,
        Feb,
        Mar,
        Apr,
        May,
        Jun,
        Jul,
        Aug,
        Sep,
        Oct,
        Nov,
        Dec;

        /**
         * 根据索引生成{Month}类型对象
         * @param ordinal {Month} 枚举类型索引
         * @return {Month}枚举类型对象
         */
        public static Month build(int ordinal) {
            Month month = Jan;

            switch (ordinal) {
                case 0 :
                    month = Jan;
                    break;
                case 1 :
                    month = Feb;
                    break;
                case 2 :
                    month = Mar;
                    break;
                case 3 :
                    month = Apr;
                    break;
                case 4 :
                    month = May;
                    break;
                case 5 :
                    month = Jun;
                    break;
                case 6 :
                    month = Jul;
                    break;
                case 7 :
                    month = Aug;
                    break;
                case 8 :
                    month = Sep;
                    break;
                case 9 :
                    month = Oct;
                    break;
                case 10 :
                    month = Nov;
                    break;
                case 11 :
                    month = Dec;
                    break;
            }
            return month;
        }
    }

    /**
     * 查询股票的方式
     */
    public enum InputType {
        Numeric,// 数字代码
        Alphabet// 字母代码
    }

    /**
     * 软件使用数据API的来源
     */
    public enum API {
        Sina,
        Sohu,
        Yahoo
    }

    /**
     * 股票得上市状态
     */
    public enum ListStatus {
        L,  // 上市
        S,  // 暂停
        DE, // 已退市
        UN  // 未上市
    }

    /**
     * 触控屏幕时的操作模式
     */
    public enum ActionMode {
        None,// 不进行任何操作（屏蔽TouchEvent）
        Select,// 选择某天的K线数据
        Drag,// 拖拽查看历史行情
        Scale// 缩放
    }
}
