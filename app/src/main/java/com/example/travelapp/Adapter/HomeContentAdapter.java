package com.example.travelapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.R;
import com.example.travelapp.home.HomeContent;

import java.util.List;

public class HomeContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<HomeContent> contentList;

    public HomeContentAdapter(List<HomeContent> contentList) {
        this.contentList = contentList;
    }

    // The ViewHolder that implement the layout of rows
    public class vocVH extends RecyclerView.ViewHolder {

        TextView topicTxt, cat1Txt, wordTxt, transTxt, cat2Txt, word2Txt, trans2Txt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;


        public vocVH(@NonNull View itemView) {
            super(itemView);

            topicTxt = itemView.findViewById(R.id.topic);
            cat1Txt = itemView.findViewById(R.id.category1);
            wordTxt = itemView.findViewById(R.id.word1);
            transTxt = itemView.findViewById(R.id.translation1);
            cat2Txt = itemView.findViewById((R.id.category2));
            word2Txt = itemView.findViewById(R.id.word2);
            trans2Txt = itemView.findViewById(R.id.translation2);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HomeContent content = contentList.get(getAdapterPosition());
                    content.setExpandable(!content.isExpandable());
                    notifyItemChanged(getAdapterPosition());

                }
            });

        }
    }

    public class marketVH extends RecyclerView.ViewHolder {

        TextView topicTxt, itemTxt, priceTxt, itemListTxt, priceListTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;


        public marketVH(@NonNull View itemView) {
            super(itemView);

            topicTxt = itemView.findViewById(R.id.topic);
            itemTxt = itemView.findViewById(R.id.item);
            priceTxt = itemView.findViewById(R.id.price);
            itemListTxt = itemView.findViewById(R.id.itemList);
            priceListTxt = itemView.findViewById(R.id.priceList);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HomeContent content = contentList.get(getAdapterPosition());
                    content.setExpandable(!content.isExpandable());
                    notifyItemChanged(getAdapterPosition());

                }
            });

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_voc, parent, false);
                    return new vocVH(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_market, parent, false);
                    return new marketVH(view2);
        }

        //Default state
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_voc, parent, false);
        return new vocVH(view1);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HomeContent content = contentList.get(position);
        boolean isExpandable = contentList.get(position).isExpandable();

        switch (holder.getItemViewType()) {
            case 0:
                vocVH viewHolder0 = (vocVH) holder;
                viewHolder0.topicTxt.setText(content.getTopic());
                viewHolder0.cat1Txt.setText(content.getContent1());
                viewHolder0.wordTxt.setText(content.getContent2());
                viewHolder0.transTxt.setText(content.getContent3());
                viewHolder0.cat2Txt.setText(content.getContent4());
                viewHolder0.word2Txt.setText(content.getContent5());
                viewHolder0.trans2Txt.setText(content.getContent6());

                viewHolder0.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
                break;

            case 2:
                marketVH viewHolder2 = (marketVH) holder;
                viewHolder2.topicTxt.setText(content.getTopic());
                viewHolder2.itemTxt.setText(content.getContent1());
                viewHolder2.priceTxt.setText(content.getContent2());
                viewHolder2.itemListTxt.setText(content.getContent3());
                viewHolder2.priceListTxt.setText(content.getContent4());

                viewHolder2.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
                break;
        }


    }

    @Override
    public int getItemViewType(int position){
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        //System.out.println(position);
        return position % 2 * 2;
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

}
