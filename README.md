# FlowLayout

---

流式布局，支持 `Adapter`,`notifyData` 和 `gravity属性`

![left](https://github.com/wangruning/MyFlowLayout/blob/master/image/left.png)

- `app:gravity="right"`

![right](https://github.com/wangruning/MyFlowLayout/blob/master/image/right.png)

- `app:gravity="center"`

![center](https://github.com/wangruning/MyFlowLayout/blob/master/image/center.png)

## 使用

- 直接在布局内放置

- 使用Adapter设置数据

```java
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
```

- 改变数据时，调用 `adapter.notifyDataSetChanged` 刷新显示

```java
// adapter notify data
textArray[0] = "Hello World!";
textArray[5] = "Hi Tom,How are you?";
tagAdapter.notifyDataSetChanged();
```

![setAdapter](https://github.com/wangruning/MyFlowLayout/blob/master/image/notify_data.png)