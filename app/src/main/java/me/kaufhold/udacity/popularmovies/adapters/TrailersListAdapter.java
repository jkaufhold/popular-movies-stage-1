package me.kaufhold.udacity.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.kaufhold.udacity.popularmovies.R;
import me.kaufhold.udacity.popularmovies.model.Trailer;

public class TrailersListAdapter extends RecyclerView.Adapter<TrailersListAdapter.TrailersListHolder>  {

    private final Context context;
    private List<Trailer> trailers;

    public TrailersListAdapter(Context applicationContext) { this.context = applicationContext; }

    public void init(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public TrailersListAdapter.TrailersListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View trailer_item_view = inflater.inflate(R.layout.trailers_item, parent, false);
        return new TrailersListHolder(trailer_item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersListAdapter.TrailersListHolder holder, int position) {
        if(trailers == null){
            return;
        }
        Trailer trailer = trailers.get(position);
        if(trailer == null){
            return;
        }
        holder.setTrailer(trailer);
        if(trailer.getName() != null) {
            holder.trailer_name_tv.setText(trailer.getName());
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(trailers != null) {
            count = this.trailers.size();
        }
        return count;
    }

    public ArrayList<Trailer> createTrailersArray() { return new ArrayList<>(trailers); }

    public class TrailersListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView trailer_name_tv;
        private Trailer trailer;

        public TrailersListHolder(@NonNull View itemView) {
            super(itemView);
            trailer_name_tv = itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View movie_image_view) {
            String video_id = trailer.getKey();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + video_id));
            context.startActivity(i);
        }

        public void setTrailer(Trailer trailer) {
            this.trailer = trailer;
        }
    }

    public void addTrailers(List<Trailer> trailers) {
        this.trailers.addAll(trailers);
        notifyDataSetChanged();
    }
}
