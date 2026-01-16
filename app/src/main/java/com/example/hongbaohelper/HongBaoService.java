package com.example.hongbaohelper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class HongBaoService extends AccessibilityService {

    private static final String TAG = "HongBaoService";

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "无障碍服务已连接");

        // 配置服务信息：监听微信，响应界面变化
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100; // 毫秒
        info.packageNames = new String[]{"com.tencent.mm"}; // 微信包名
        this.setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 确保事件来自微信
        if (event.getPackageName() == null || !event.getPackageName().toString().contains("tencent.mm")) {
            return;
        }

        // 获取当前窗口根节点
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }

        try {
            // 策略一：查找并点击“领取红包”
            List<AccessibilityNodeInfo> receiveNodes = rootNode.findAccessibilityNodeInfosByText("领取红包");
            for (AccessibilityNodeInfo node : receiveNodes) {
                if (node != null && node.getParent() != null) {
                    // 找到可点击的父节点进行点击
                    node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "已尝试点击‘领取红包’");
                    break; // 点击一个即可
                }
            }

            // 策略二：查找并点击“拆红包”
            List<AccessibilityNodeInfo> openNodes = rootNode.findAccessibilityNodeInfosByText("拆红包");
            for (AccessibilityNodeInfo node : openNodes) {
                if (node != null && node.isClickable()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "已尝试点击‘拆红包’");
                    break;
                }
            }

            // 策略三：遇到这些情况，模拟按下返回键退出详情页
            String[] backKeywords = {"红包详情", "手慢了", "已领取", "过期"};
            for (String keyword : backKeywords) {
                List<AccessibilityNodeInfo> backNodes = rootNode.findAccessibilityNodeInfosByText(keyword);
                if (!backNodes.isEmpty()) {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    Log.d(TAG, "发现‘" + keyword + "‘，已返回");
                    break;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "处理红包时发生异常", e);
        } finally {
            // 非常重要！释放节点资源，防止内存泄漏
            if (rootNode != null) {
                rootNode.recycle();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "服务被中断");
    }
}
