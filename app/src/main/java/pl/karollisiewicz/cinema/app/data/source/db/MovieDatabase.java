package pl.karollisiewicz.cinema.app.data.source.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public final class MovieDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int VERSION = 1;

    MovieDatabase(@NonNull final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        final String createTableQuery = new StringBuilder()
                .append("CREATE TABLE ")
                .append(MovieContract.MovieEntry.TABLE_NAME)
                .append("(")
                .append(TextUtils.join(", ", MovieContract.MovieEntry.Column.values()))
                .append(")")
                .toString();

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
