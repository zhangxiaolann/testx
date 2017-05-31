package com.shhb.gd.shop.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shhb.gd.shop.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchWindowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /** 哪一个Fragment*/
    private final int fType;
    /** recycler的数据 */
    private final List<Map<String, Object>> listMap;
    private static OnClickListener onClickListener;

    public SearchWindowAdapter(int type){
        fType = type;
        listMap = new ArrayList<>();
    }

    /**
     * 通过异步请求将列表的数据填充到Adapter
     * @param datas
     */
    public void addRecyclerData(List<Map<String, Object>> datas) {
        listMap.addAll(datas);
    }

    @Override
    public int getItemCount() {
        return listMap.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflate(viewGroup, R.layout.hot_view);
        return new RecyclerHolder(itemView);
    }

    /**
     * 填充页面的方法
     * @param parent
     * @param layoutRes
     * @return
     */
    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        onBindRecyclerHolder((RecyclerHolder) viewHolder,i);
    }

    /**
     * 将数据填充到recycler上
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(RecyclerHolder viewHolder, int position) {
        String name = listMap.get(position).get("name") + "";
        viewHolder.name.setText(name);
    }

    class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;

        /** 获取到recycler中的每一个View */
        public RecyclerHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.hotName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                default:
                    onClickListener.onClick(view,getPosition(),listMap);
                    break;
            }
        }
    }

    /** 定义所有单击事件接口 */
    public interface OnClickListener {
        void onClick(View view, int position, List<Map<String, Object>> listMap);
    }

    /** 所有单击事件的处理方法 */
    public void setOnClickListener(final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
