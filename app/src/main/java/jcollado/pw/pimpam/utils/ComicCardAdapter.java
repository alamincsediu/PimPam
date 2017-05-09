package jcollado.pw.pimpam.utils;

/**
 * Created by colla on 28/03/2017.
 */

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import jcollado.pw.pimpam.R;
import jcollado.pw.pimpam.controller.MainActivity;
import jcollado.pw.pimpam.controller.ViewComicFragment;
import jcollado.pw.pimpam.model.Comic;


public class ComicCardAdapter extends RecyclerView.Adapter<ComicCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Comic> comicList;

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            view.setOnClickListener(this);


        }

        @Override
        //onClick method when click a card
        public void onClick(View view) {
            openViewComicFragment(comicList.get(getAdapterPosition()));
        }
    }


    public ComicCardAdapter(Context mContext, List<Comic> albumList) {
        this.mContext = mContext;
        this.comicList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comic_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Comic comic = comicList.get(position);
        holder.title.setText(comic.getDisplayName());
        holder.count.setText(comic.getEditorial());

        // loading album cover using Glide library
        Glide.with(mContext).load(comic.getImageURL()).placeholder(R.drawable.placeholder).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openViewComicFragment(comicList.get(position));

            }
        });
    }
    private void openViewComicFragment(Comic comic){
        ViewComicFragment fragment = ViewComicFragment.newInstance();
        fragment.setComic(comic);
        MainActivity myActivity = (MainActivity) mContext;
         myActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment,"")
                .commit();
    }

    /*
        Añade una lista completa de items
         */
    public void addAll(List<Comic> lista){
        comicList.addAll(lista);
        notifyDataSetChanged();
    }

    /*
    Permite limpiar todos los elementos del recycler
     */
    public void clear(){
        comicList.clear();
        notifyDataSetChanged();
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }
}