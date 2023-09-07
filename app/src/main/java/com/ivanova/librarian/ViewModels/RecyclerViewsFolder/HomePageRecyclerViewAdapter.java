package com.ivanova.librarian.ViewModels.RecyclerViewsFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.BookViewModelFolder.BookViewModel;

import java.util.ArrayList;

public class HomePageRecyclerViewAdapter extends RecyclerView.Adapter<HomePageRecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    ArrayList<BookViewModel> bookVMs;
    Context context;

    public HomePageRecyclerViewAdapter(ArrayList<BookViewModel> bookVMs, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.bookVMs = bookVMs;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_book_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view, recyclerViewInterface, bookVMs);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.iv_bookImage.setImageBitmap(bookVMs.get(position).getBook().getImage());
        holder.tv_author.setText(bookVMs.get(position).getBook().getAuthor());
        holder.tv_bookName.setText(bookVMs.get(position).getBook().getBookName());
        holder.tv_rating.setText(String.format("%.1f", bookVMs.get(position).getBook().getRating()));
    }

    @Override
    public int getItemCount() {
        return bookVMs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bookImage;
        TextView tv_author;
        TextView tv_bookName;
        TextView tv_rating;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface, ArrayList<BookViewModel> bookVMs) {
            super(itemView);
            iv_bookImage = itemView.findViewById(R.id.bookImage);
            tv_author = itemView.findViewById(R.id.tv_author);
            tv_bookName = itemView.findViewById(R.id.tv_bookName);
            tv_rating = itemView.findViewById(R.id.tv_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onBookItemClick(bookVMs, pos);
                        }
                    }
                }
            });
        }
    }
}
