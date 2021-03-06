package pl.karollisiewicz.cinema.app.ui.movie;


import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import pl.karollisiewicz.cinema.R;
import pl.karollisiewicz.cinema.domain.exception.AuthorizationException;
import pl.karollisiewicz.cinema.domain.exception.CommunicationException;
import pl.karollisiewicz.cinema.domain.movie.Movie;
import pl.karollisiewicz.cinema.domain.movie.MovieRepository;
import pl.karollisiewicz.common.ui.Resource;
import pl.karollisiewicz.common.ui.SnackbarPresenter;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.make;
import static pl.karollisiewicz.common.ui.Resource.Status.ERROR;
import static pl.karollisiewicz.common.ui.Resource.Status.LOADING;

public final class MoviesFragment extends Fragment {

    private static final String CRITERION_KEY = "MoviesFragment.Type";

    @BindView(R.id.container_layout)
    ViewGroup container;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    SnackbarPresenter snackbarPresenter;

    @BindView(R.id.movies_list)
    RecyclerView recyclerView;

    private MoviesAdapter adapter;
    private MoviesViewModel moviesViewModel;

    private MovieRepository.Criterion criterion;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);

        final Bundle args = getArguments();
        criterion = (MovieRepository.Criterion) args.getSerializable(CRITERION_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecyclerView();
        setupViewModel();
        setupRefreshLayout();
    }

    private void setupRecyclerView() {
        adapter = new MoviesAdapter();
        adapter.setMovieClickListener((movie, image) -> {
            final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    image, ViewCompat.getTransitionName(image));
            MovieDetailsActivity.start(getActivity(), options, movie);
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColumnNumbers()));
    }

    private int getColumnNumbers() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
    }

    private void setupViewModel() {
        moviesViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MoviesViewModel.class);

        moviesViewModel.getMovies(criterion).observe(this, this::show);
    }

    private void setupRefreshLayout() {
        refreshLayout.setOnRefreshListener(() ->
                moviesViewModel.getMovies(criterion));
    }

    @Override
    public void onResume() {
        super.onResume();
        moviesViewModel.getMovies(criterion);
    }

    private void show(@Nullable final Resource<List<Movie>> resource) {
        if (resource == null) return;

        if (resource.getStatus() == ERROR) showError(resource.getError());
        if (resource.getStatus() != LOADING) {
            hideProgress();
            populateView(resource.getData());
        }
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
    }

    private void populateView(@Nullable List<Movie> movies) {
        adapter.setItems(movies);
        recyclerView.setAdapter(adapter);
    }

    private void showError(Throwable throwable) {
        if (throwable instanceof CommunicationException) showMessage(R.string.error_communication);
        else if (throwable instanceof AuthorizationException) showMessage(R.string.error_authorization);
        else showMessage(R.string.error_unknown);
    }

    private void showMessage(@StringRes int messageId) {
        snackbarPresenter.show(make(container, getString(messageId), LENGTH_LONG)
                .setAction(R.string.action_retry, v -> moviesViewModel.getMovies(criterion))
        );
    }

    public static MoviesFragment newPopularInstance() {
        return newInstance(MovieRepository.Criterion.POPULARITY);
    }

    public static MoviesFragment newTopRatedInstance() {
        return newInstance(MovieRepository.Criterion.RATING);
    }

    public static Fragment newFavouriteInstance() {
        return newInstance(MovieRepository.Criterion.FAVOURITE);
    }

    private static MoviesFragment newInstance(@NonNull final MovieRepository.Criterion criterion) {
        final MoviesFragment fragment = new MoviesFragment();
        fragment.setArguments(createBundle(criterion));
        return fragment;
    }

    @NonNull
    private static Bundle createBundle(@NonNull MovieRepository.Criterion criterion) {
        Bundle args = new Bundle();
        args.putSerializable(CRITERION_KEY, criterion);
        return args;
    }
}
