package pl.karollisiewicz.cinema.app.data.source.web.video;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

import io.reactivex.Observable;

public class VideoMapper {
    private VideoMapper() {
    }

    @NonNull
    public static pl.karollisiewicz.cinema.domain.movie.video.Video toDomain(@NonNull final Video video) {
        return new pl.karollisiewicz.cinema.domain.movie.video.Video.Builder()
                .setId(video.getId())
                .setName(video.getName())
                .setUrl(video.getUrl())
                .setThumbnailUrl(video.getThumbnailUrl())
                .setType(byTypeName(video.getType()))
                .build();
    }

    @NonNull
    public static Video toDto(@NonNull pl.karollisiewicz.cinema.domain.movie.video.Video video) {
        final Video vid = new Video();
        vid.setName(video.getName());
        vid.setUrl(video.getUrl());
        vid.setThumbnailUrl(video.getThumbnailUrl());
        vid.setType(video.getType() != null ? video.getType().name() : "");
        return vid;
    }

    @Nullable
    private static pl.karollisiewicz.cinema.domain.movie.video.Video.Type byTypeName(final String name) {
        return Observable.fromIterable(Arrays.asList(pl.karollisiewicz.cinema.domain.movie.video.Video.Type.values()))
                .filter(it -> it.name().equalsIgnoreCase(name)).blockingFirst();
    }
}
