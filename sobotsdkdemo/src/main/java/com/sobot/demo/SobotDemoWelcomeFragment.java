package com.sobot.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.demo.activity.product.SobotDemoCloudCallActivity;
import com.sobot.demo.activity.product.SobotDemoCustomActivity;
import com.sobot.demo.activity.product.SobotDemoRobotActivity;
import com.sobot.demo.activity.product.SobotDemoWorkOrderActivity;

public class SobotDemoWelcomeFragment extends Fragment implements View.OnClickListener {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.sobot_demo_welcome_fragment, null);
        findViewsById();
        return view;
    }

    private void findViewsById() {
        LinearLayout sobot_robot_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_robot_layout);
        LinearLayout sobot_demo_custom_service_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_custom_service_layout);
        LinearLayout sobot_demo_cloud_call_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_cloud_call_layout);
        LinearLayout sobot_demo_work_roder_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_work_roder_layout);
        TextView sobot_tv_right = (TextView) view.findViewById(R.id.sobot_tv_right);
        sobot_tv_right.setOnClickListener(this);
        sobot_robot_layout.setOnClickListener(this);
        sobot_demo_custom_service_layout.setOnClickListener(this);
        sobot_demo_cloud_call_layout.setOnClickListener(this);
        sobot_demo_work_roder_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.sobot_demo_robot_layout:
                intent = new Intent(getActivity(), SobotDemoRobotActivity.class);
                startActivity(intent);
                break;
            case R.id.sobot_demo_custom_service_layout:
                intent = new Intent(getActivity(), SobotDemoCustomActivity.class);
                startActivity(intent);
                break;
            case R.id.sobot_demo_cloud_call_layout:
                intent = new Intent(getActivity(), SobotDemoCloudCallActivity.class);
                startActivity(intent);
                break;
            case R.id.sobot_demo_work_roder_layout:
                intent = new Intent(getActivity(),SobotDemoWorkOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.sobot_tv_right:
                SobotUtils.startSobot(getContext());
                break;
        }
    }
}