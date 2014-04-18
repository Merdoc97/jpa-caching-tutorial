package it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hascode.tutorial.jpa_caching.entity.Book;

public class JpaCachingTest {
	EntityManagerFactory emf;
	EntityManager em;
	EntityTransaction tx;

	@Before
	public void setup() {
		Map<String, Object> properties = new HashMap<>();
		properties
				.put("javax.persistence.sharedCache.mode", CacheStoreMode.USE);
		emf = Persistence.createEntityManagerFactory("default", properties);
		em = emf.createEntityManager();
		tx = em.getTransaction();
		tx.begin();
	}

	@After
	public void teardown() {
		tx.rollback();
		em.close();
		emf.close();
	}

	@Test
	public void shouldCacheACachableEntity() throws Exception {
		em.setProperty("javax.persistence.cache.storeMode", CacheStoreMode.USE);
		Book book = new Book(1L, "Some book");
		em.persist(book);
		em.flush();

		Cache cache = emf.getCache();
		assertThat(cache.contains(Book.class, 1L), is(false));

		Book bookStored = em
				.createQuery("SELECT b FROM Book b WHERE b.id=1", Book.class)
				.setHint("javax.persistence.cache.storeMode",
						CacheStoreMode.USE).getSingleResult();
		assertThat(bookStored, notNullValue());

		assertThat(cache.contains(Book.class, 1L), is(true));

	}
}
