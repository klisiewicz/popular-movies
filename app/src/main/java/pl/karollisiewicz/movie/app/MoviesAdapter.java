package pl.karollisiewicz.movie.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.karollisiewicz.movie.R;
import pl.karollisiewicz.movie.domain.Movie;

import static java.util.Collections.emptyList;

/**
 * An {@link android.support.v7.widget.RecyclerView.Adapter} for {@link pl.karollisiewicz.movie.domain.Movie}
 */
public final class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> movies = new ArrayList<>();

    @Nullable
    private MovieItemClickListener movieItemClickListener;

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void setItems(@Nullable final List<Movie> movies) {
        this.movies = (movies != null) ? new ArrayList<>(movies) : emptyList();
        notifyDataSetChanged();
    }

    void setOnItemClickListener(@Nullable final MovieItemClickListener movieItemClickListener) {
        this.movieItemClickListener = movieItemClickListener;
    }

    @FunctionalInterface
    public interface MovieItemClickListener {
        void onMovieClick(Movie movie);
    }

    final class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title_text)
        TextView title;

        @BindView(R.id.poster_image)
        ImageView posterImage;

        MovieViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(@NonNull final Movie movie) {
            title.setText(movie.getTitle());
            Picasso.with(posterImage.getContext())
                    .load(movie.getPosterUrl())
                    .into(posterImage);
        }

        @Override
        public void onClick(View v) {
            if (movieItemClickListener != null) {
                final int position = getAdapterPosition();
                movieItemClickListener.onMovieClick(movies.get(position));
            }
        }
    }
}
