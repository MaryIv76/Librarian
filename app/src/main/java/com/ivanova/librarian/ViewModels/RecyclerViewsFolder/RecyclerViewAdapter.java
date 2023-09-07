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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    ArrayList<BookViewModel> bookVMs;
    Context context;

    public RecyclerViewAdapter(ArrayList<BookViewModel> bookVMs, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.bookVMs = bookVMs;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_book_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view, recyclerViewInterface, bookVMs);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_bookAuthor.setText(bookVMs.get(position).getBook().getAuthor());
        holder.tv_bookName.setText(bookVMs.get(position).getBook().getBookName());
        holder.tv_bookYear.setText(bookVMs.get(position).getBook().getYear());
        holder.tv_bookGenre.setText(bookVMs.get(position).getBook().getGenre());
        holder.tv_bookAnnotation.setText(bookVMs.get(position).getBook().getAnnotation());
        holder.iv_bookImg.setImageBitmap(bookVMs.get(position).getBook().getImage());
    }

    @Override
    public int getItemCount() {
        return bookVMs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bookImg;
        TextView tv_bookAuthor;
        TextView tv_bookName;
        TextView tv_bookYear;
        TextView tv_bookGenre;
        TextView tv_bookAnnotation;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface, ArrayList<BookViewModel> bookVMs) {
            super(itemView);
            iv_bookImg = itemView.findViewById(R.id.bookImage);
            tv_bookAuthor = itemView.findViewById(R.id.authorInfo);
            tv_bookName = itemView.findViewById(R.id.bookNameInfo);
            tv_bookYear = itemView.findViewById(R.id.yearInfo);
            tv_bookGenre = itemView.findViewById(R.id.genreInfo);
            tv_bookAnnotation = itemView.findViewById(R.id.annotationInfo);

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
