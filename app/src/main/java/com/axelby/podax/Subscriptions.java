package com.axelby.podax;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class Subscriptions {

	private static PublishSubject<SubscriptionData> _changeSubject = PublishSubject.create();
	public static void notifyChange(SubscriptionCursor c) {
		SubscriptionData data = SubscriptionData.cacheSwap(c);
		_changeSubject.onNext(data);
	}

	public static Observable<SubscriptionData> watchAll() {
		return _changeSubject
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable<SubscriptionData> watch(Context context, long id) {
		if (id < 0)
			return Observable.empty();

		return _changeSubject
			.filter(d -> d.getId() == id)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.startWith(SubscriptionData.create(context, id));
	}

	public static Observable<SubscriptionData> getAll(Context context) {
		return Observable.create(subscriber -> {
			Cursor c = context.getContentResolver().query(SubscriptionProvider.URI, null, null, null, null);
			if (c != null) {
				while (c.moveToNext())
					subscriber.onNext(SubscriptionData.from(new SubscriptionCursor(c)));
				c.close();
			}
			subscriber.onCompleted();
		});
	}

	/* -------
	   helpers
	   ------- */

	private static Observable<SubscriptionData> queryToObservable(Context context,
			  Uri uri, String selection, String[] selectionArgs, String sortOrder) {
		return Observable.create(subscriber -> {
			Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, sortOrder);
			if (cursor != null) {
				while (cursor.moveToNext())
					subscriber.onNext(SubscriptionData.from(new SubscriptionCursor(cursor)));
				cursor.close();
			}
			subscriber.onCompleted();
		});
	}

	public static Observable<SubscriptionData> getFor(Context context, String field, int value) {
		String fieldName = SubscriptionProvider.getColumnMap().get(field);
		String selection = fieldName + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(value) };
		return queryToObservable(context, SubscriptionProvider.URI, selection, selectionArgs, null);
	}

	public static Observable<SubscriptionData> getFor(Context context, String field, String value) {
		String fieldName = SubscriptionProvider.getColumnMap().get(field);
		String selection = fieldName + " = ?";
		String[] selectionArgs = new String[] { value };
		return queryToObservable(context, SubscriptionProvider.URI, selection, selectionArgs, null);
	}

	public static Observable<SubscriptionData> getForRSSUrl(Context context, String rssUrl) {
		String selection = SubscriptionProvider.COLUMN_URL + "=?";
		String[] selectionArgs = new String[] { rssUrl };
		return Subscriptions.queryToObservable(context, SubscriptionProvider.URI, selection, selectionArgs, null);
	}

	public static void evictCache() {
		SubscriptionData.evictCache();
		_changeSubject = PublishSubject.create();
	}
}
