package com.axelby.podax;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import org.joda.time.LocalTime;

import java.util.Date;

class PlaylistManager {

	public static void completeActiveEpisode(Context context) {
		if (isInSleepytime(context))
			PlayerService.stop(context);

		markEpisodeComplete(context, EpisodeProvider.ACTIVE_EPISODE_URI);

		Stats.addCompletion(context);
	}

	public static void markEpisodeComplete(Context context, Uri uri) {
		ContentValues values = new ContentValues(3);
		values.put(EpisodeProvider.COLUMN_LAST_POSITION, 0);
		values.put(EpisodeProvider.COLUMN_PLAYLIST_POSITION, (Integer) null);
		values.put(EpisodeProvider.COLUMN_FINISHED_TIME, new Date().getTime() / 1000);
		context.getContentResolver().update(uri, values, null, null);
	}

	private static boolean isInSleepytime(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean sleepytimeMode = preferences.getBoolean("sleepytimeEnabled", false);
		if (sleepytimeMode) {
			int startHour = preferences.getInt("sleepytimeStart", 20);
			int endHour = preferences.getInt("sleepytimeEnd", 4) + 24;
			int currentHour = LocalTime.now().getHourOfDay();
			if (currentHour < 12)
				currentHour += 24;
			if (startHour > currentHour || currentHour > endHour)
				sleepytimeMode = false;
		}
		return sleepytimeMode;
	}

	public static void changeActiveEpisode(Context context, long activeEpisodeId) {
		ContentValues values = new ContentValues();
		values.put(EpisodeProvider.COLUMN_ID, activeEpisodeId);
		context.getContentResolver().update(EpisodeProvider.ACTIVE_EPISODE_URI, values, null, null);
	}

}
