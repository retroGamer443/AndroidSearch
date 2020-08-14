package com.kyrodevs.bottomnav;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchResult> mResults;

    public SearchAdapter(List<SearchResult> res) {
        mResults = res;
    }

    public void addSearchResult(SearchResult item) {
        // This is a custom method that adds the item to list and notify RecyclerView to Update
        mResults.add(item);
        notifyDataSetChanged();
    }

    public void clearSearch() {
        // This is also a custom method which removes all the items from list.
        mResults.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View searchRes = inflater.inflate(R.layout.search_res_layout, parent, false);

        return new ViewHolder(searchRes);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // This method invokes when ViewHolder is finished binding
        SearchResult searchItem = mResults.get(position);

        TextView userName = holder.userName;
        TextView userId = holder.userId;

        userName.setText(searchItem.getUsername());
        userId.setText(searchItem.getUserid());
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            userId = itemView.findViewById(R.id.userid);
        }
    }
}
