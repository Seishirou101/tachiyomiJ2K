package eu.kanade.tachiyomi.data.database.mappers

import android.database.Cursor
import androidx.core.content.contentValuesOf
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery
import eu.kanade.tachiyomi.data.database.models.Track
import eu.kanade.tachiyomi.data.database.models.TrackImpl
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_FINISH_DATE
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_ID
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_LAST_CHAPTER_READ
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_LIBRARY_ID
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_MANGA_ID
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_MEDIA_ID
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_SCORE
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_START_DATE
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_STATUS
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_SYNC_ID
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_TITLE
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_TOTAL_CHAPTERS
import eu.kanade.tachiyomi.data.database.tables.TrackTable.COL_TRACKING_URL
import eu.kanade.tachiyomi.data.database.tables.TrackTable.TABLE

class TrackTypeMapping :
    SQLiteTypeMapping<Track>(
        TrackPutResolver(),
        TrackGetResolver(),
        TrackDeleteResolver(),
    )

class TrackPutResolver : DefaultPutResolver<Track>() {
    override fun mapToInsertQuery(obj: Track) =
        InsertQuery
            .builder()
            .table(TABLE)
            .build()

    override fun mapToUpdateQuery(obj: Track) =
        UpdateQuery
            .builder()
            .table(TABLE)
            .where("$COL_ID = ?")
            .whereArgs(obj.id)
            .build()

    override fun mapToContentValues(obj: Track) =
        contentValuesOf(
            COL_ID to obj.id,
            COL_MANGA_ID to obj.manga_id,
            COL_SYNC_ID to obj.sync_id,
            COL_MEDIA_ID to obj.media_id,
            COL_LIBRARY_ID to obj.library_id,
            COL_TITLE to obj.title,
            COL_LAST_CHAPTER_READ to obj.last_chapter_read,
            COL_TOTAL_CHAPTERS to obj.total_chapters,
            COL_STATUS to obj.status,
            COL_TRACKING_URL to obj.tracking_url,
            COL_SCORE to obj.score,
            COL_START_DATE to obj.started_reading_date,
            COL_FINISH_DATE to obj.finished_reading_date,
        )
}

class TrackGetResolver : DefaultGetResolver<Track>() {
    override fun mapFromCursor(cursor: Cursor): Track =
        TrackImpl().apply {
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID))
            manga_id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MANGA_ID))
            sync_id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SYNC_ID))
            media_id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MEDIA_ID))
            library_id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LIBRARY_ID))
            title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE))
            last_chapter_read = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_LAST_CHAPTER_READ))
            total_chapters = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_CHAPTERS))
            status = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STATUS))
            score = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SCORE))
            tracking_url = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRACKING_URL))
            started_reading_date = cursor.getLong(cursor.getColumnIndexOrThrow(COL_START_DATE))
            finished_reading_date = cursor.getLong(cursor.getColumnIndexOrThrow(COL_FINISH_DATE))
        }
}

class TrackDeleteResolver : DefaultDeleteResolver<Track>() {
    override fun mapToDeleteQuery(obj: Track) =
        DeleteQuery
            .builder()
            .table(TABLE)
            .where("$COL_ID = ?")
            .whereArgs(obj.id)
            .build()
}
