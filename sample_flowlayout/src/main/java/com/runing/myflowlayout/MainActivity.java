package com.runing.myflowlayout;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runing.view.flowlayout.FlowLayout;
import com.runing.view.flowlayout.TagAdapter;

public class MainActivity extends AppCompatActivity {

    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mFlowLayout = (FlowLayout) findViewById(R.id.fl_content);
        //set adapter
        final String[] textArray = {"高冷", "学霸", "老司机", "女神", "技术宅", "暖男",
                "月光族", "女汉子", "素颜", "文艺青年"};

        TagAdapter tagAdapter = new TagAdapter() {
            @Override
            public int getCount() {
                return textArray.length;
            }

            @Override
            public Object getItem(int position) {
                return textArray[position];
            }

            @Override
            public View getView(FlowLayout parent, int position, Object item) {
                TextView textView = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 8, 8, 8);
                textView.setTextColor(Color.WHITE);
                textView.setLayoutParams(params);
                textView.setText((String) item);
                return textView;
            }
        };
        mFlowLayout.setAdapter(tagAdapter);

        // adapter notify data
        textArray[0] = "Hello World!";
        textArray[5] = "Hi Tom,How are you?";
        tagAdapter.notifyDataSetChanged();
        mFlowLayout.setGravity(FlowLayout.GRAVITY_CENTER);
    }
}
