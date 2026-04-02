package com.example.travelguide.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * 崩溃测试工具类
 *
 * 用于主动触发不同类型的崩溃/异常，供腾讯 Bugly 采集，
 * 以验证移动端崩溃监控工具的捕获能力。
 *
 * 触发方式：在主界面菜单"崩溃测试"中弹出选择框，点击对应项即可触发。
 */
public class CrashTester {

    /**
     * 弹出崩溃测试选择框
     */
    public static void showCrashTestDialog(final Context context) {
        final String[] items = {
                "1. 空指针异常 (NullPointerException)",
                "2. 数组越界 (ArrayIndexOutOfBoundsException)",
                "3. 算术异常 (ArithmeticException)",
                "4. Bugly 官方 Java 崩溃测试",
                "5. Bugly 官方 Native 崩溃测试",
                "6. Bugly 官方 ANR 测试",
                "7. 主动上报捕获的异常 (不崩溃)",
        };

        new AlertDialog.Builder(context)
                .setTitle("选择要触发的崩溃/异常类型")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        trigger(which);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 根据序号触发对应异常
     */
    private static void trigger(int which) {
        switch (which) {
            case 0:
                triggerNullPointer();
                break;
            case 1:
                triggerArrayIndexOutOfBounds();
                break;
            case 2:
                triggerArithmetic();
                break;
            case 3:
                // Bugly 官方内置的 Java 崩溃测试
                CrashReport.testJavaCrash();
                break;
            case 4:
                // Bugly 官方内置的 Native 崩溃测试（JNI 层）
                CrashReport.testNativeCrash();
                break;
            case 5:
                // Bugly 官方内置的 ANR 测试
                CrashReport.testANRCrash();
                break;
            case 6:
                reportCaughtException();
                break;
            default:
                break;
        }
    }

    /** 触发空指针异常（未捕获 → 崩溃） */
    private static void triggerNullPointer() {
        String nullStr = null;
        // 故意对 null 调用方法，触发 NullPointerException 并导致 App 崩溃
        int length = nullStr.length();
    }

    /** 触发数组越界异常（未捕获 → 崩溃） */
    private static void triggerArrayIndexOutOfBounds() {
        int[] arr = new int[]{1, 2, 3};
        // 故意访问越界下标，触发 ArrayIndexOutOfBoundsException
        int v = arr[10];
    }

    /** 触发算术异常（除零，未捕获 → 崩溃） */
    private static void triggerArithmetic() {
        int a = 10;
        int b = 0;
        // 除零触发 ArithmeticException
        int c = a / b;
    }

    /**
     * 主动上报一个被捕获的异常（不会导致崩溃）
     * 用于演示 Bugly 的"已捕获异常上报"能力
     */
    private static void reportCaughtException() {
        try {
            // 模拟一段会抛异常的业务代码
            Integer.parseInt("not_a_number");
        } catch (Exception e) {
            // 捕获后主动上报给 Bugly（App 不会崩溃）
            CrashReport.postCatchedException(e);
        }
    }
}
