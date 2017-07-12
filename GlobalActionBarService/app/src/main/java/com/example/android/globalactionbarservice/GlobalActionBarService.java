// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.android.globalactionbarservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityRecord;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Deque;

import static android.R.interpolator.linear;
import static android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;

public class GlobalActionBarService extends AccessibilityService {

    private boolean touchExploration;
    FrameLayout mLayout;

    @Override
    protected void onServiceConnected() {
        disableTouchExploration();
        touchExploration = false;
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);

        wm.addView(mLayout, lp);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String eventText = null;
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Clicked: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventText = "Hovered: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventText = "Selected: ";
                break;
        }

        eventText = eventText + event.getText();
        if(event.getText().toString().equals("[Do it]") && touchExploration) {
            disableTouchExploration();
            touchExploration = false;
        } else if (event.getText().toString().equals("[Do it]") && !touchExploration) {
            enableTouchExploration();
            touchExploration = true;
        }
        LinearLayout linear = (LinearLayout) mLayout.getChildAt(0);
        TextView tView = (TextView) linear.getChildAt(0);
        tView.setText(event.getText().toString());
        System.out.println(eventText);

//        AccessibilityNodeInfo source = event.getSource();
//        if (source == null) {
//            return;
//        }
//        int childCount = source.getChildCount();
//        System.out.println(source.getText());
//        LinearLayout linear = (LinearLayout) mLayout.getChildAt(0);
//        TextView tView = (TextView) linear.getChildAt(0);
//        tView.setText(source.getText());
//        if(childCount > 0) {
//            for(int i = 0; i < childCount; i++) {
//                AccessibilityNodeInfo child = source.getChild(i);
//                System.out.println(child.getText());
//            }
//        }




//        AccessibilityNodeInfo rowNode = getListItemNodeInfo(source);
//        if (rowNode == null) {
//            return;
//        }
//
//        // Using this parent, get references to both child nodes, the label and the checkbox.
//        AccessibilityNodeInfo labelNode = rowNode.getChild(0);
//        if (labelNode == null) {
//            rowNode.recycle();
//            return;
//        }
//
//        AccessibilityNodeInfo completeNode = rowNode.getChild(1);
//        if (completeNode == null) {
//            rowNode.recycle();
//            return;
//        }
//
//        // Determine what the task is and whether or not it's complete, based on
//        // the text inside the label, and the state of the check-box.
//        if (rowNode.getChildCount() < 2 || !rowNode.getChild(1).isCheckable()) {
//            rowNode.recycle();
//            return;
//        }
//
//        CharSequence taskLabel = labelNode.getText();
//        final boolean isComplete = completeNode.isChecked();
//
//        String completeStr = null;
//        if (isComplete) {
//            completeStr = getString(R.string.task_complete);
//        } else {
//            completeStr = getString(R.string.task_not_complete);
//        }
//
//
//        String taskStr = getString(R.string.task_complete_template, taskLabel, completeStr);
//        StringBuilder utterance = new StringBuilder(taskStr);
//
//        // The custom ListView added extra context to the event by adding an
//        // AccessibilityRecord to it. Extract that from the event and read it.
//        final int records = event.getRecordCount();
//        for (int i = 0; i < records; i++) {
//            AccessibilityRecord record = event.getRecord(i);
//            CharSequence contentDescription = record.getContentDescription();
//            if (!TextUtils.isEmpty(contentDescription )) {
//                utterance.append(SEPARATOR);
//                utterance.append(contentDescription);
//            }
//        }

    }

    @Override
    public void onInterrupt() {
        System.out.println("hello");
    }

    private void enableTouchExploration() {
        AccessibilityServiceInfo info = this.getServiceInfo();

        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        this.setServiceInfo(info);
    }

    private void disableTouchExploration() {
        AccessibilityServiceInfo serviceInfo = this.getServiceInfo();

        serviceInfo.flags &= ~AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        this.setServiceInfo(serviceInfo);
    }
}
