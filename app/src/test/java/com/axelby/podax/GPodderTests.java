package com.axelby.podax;

import com.axelby.podax.model.PodaxDB;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GPodderTests {
	@Rule
	public RxSchedulerSwitcher _rxSchedulerSwitcher = new RxSchedulerSwitcher();

	@Rule
	public DataCacheClearer _dataCacheClearer = new DataCacheClearer();

	@Test
	public void testDB() {
		PodaxDB.gPodder.add("http://www.axelby.com/podcast1.xml");
		List<String> toAdd = PodaxDB.gPodder.getToAdd();
		Assert.assertEquals(1, toAdd.size());
		Assert.assertEquals("http://www.axelby.com/podcast1.xml", toAdd.get(0));

		PodaxDB.gPodder.remove("http://www.axelby.com/podcast2.xml");
		List<String> toRemove = PodaxDB.gPodder.getToAdd();
		Assert.assertEquals(1, toRemove.size());
		Assert.assertEquals("http://www.axelby.com/podcast1.xml", toRemove.get(0));

		PodaxDB.gPodder.clear();
		Assert.assertEquals(0, PodaxDB.gPodder.getToAdd().size());
		Assert.assertEquals(0, PodaxDB.gPodder.getToRemove().size());
	}
}
