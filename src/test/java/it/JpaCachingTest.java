package it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
import com.hascode.tutorial.jpa_caching.entity.Person;

public class JpaCachingTest {
	EntityManagerFactory emf;
	EntityManager em;
	EntityTransaction tx;

	@Before
	public void setup() {
		emf = Persistence.createEntityManagerFactory("default");
		em = emf.createEntityManager();
		tx = em.getTransaction();
	}

	@After
	public void teardown() {
		em.close();
		emf.close();
	}

	/**
	 * Selective caching is enabled by
	 * <shared-cache-element>ENABLE_SELECTIVE</shared-cache-element> and the
	 * book entity is marked @Cachable(true)
	 */
	@Test
	public void shouldCacheACachableEntity() throws Exception {
		tx.begin();
		Book book1 = new Book(1L, "Some book");
		Book book2 = new Book(2L, "Another book");
		em.persist(book1);
		em.persist(book2);
		tx.commit();

		Cache cache = emf.getCache();
		assertThat(cache.contains(Book.class, 1L), is(true));
		assertThat(cache.contains(Book.class, 2L), is(true));

		Book cachedBook = em.find(Book.class, 1L);
		assertThat(cachedBook, notNullValue());

		cache.evict(Book.class, 1L); // clear one designated book from cache
		assertThat(cache.contains(Book.class, 1L), is(false));

		cache.evict(Book.class); // clear all books from cache
		assertThat(cache.contains(Book.class, 2L), is(false));
	}

	/**
	 * Selective caching is enabled by
	 * <shared-cache-element>ENABLE_SELECTIVE</shared-cache-element> and the
	 * person entity is marked @Cachable(false)
	 */
	@Test
	public void shouldNotCacheAnUncachableEntity() throws Exception {
		tx.begin();
		Person person1 = new Person(1L, "Lisa");
		Person person2 = new Person(2L, "Tim");
		em.persist(person1);
		em.persist(person2);
		tx.commit();

		Cache cache = emf.getCache();
		assertThat(cache.contains(Person.class, 1L), is(false));
		assertThat(cache.contains(Person.class, 2L), is(false));

		Person personFound = em.find(Person.class, 1L);
		assertThat(personFound, notNullValue());
	}

	@Test
	public void shouldCacheQuery() throws Exception {
		tx.begin();
		Book book1 = new Book(3L, "Book 1");
		Book book2 = new Book(4L, "Book 2");
		em.persist(book1);
		em.persist(book2);
		tx.commit();

		Cache cache = emf.getCache();
		cache.evictAll();
		Book book3 = em
				.createQuery("SELECT p FROM Book p WHERE p.title='Book 1'",
						Book.class)
				.setHint("javax.persistence.cache.storeMode",
						CacheStoreMode.BYPASS).getSingleResult();
		assertThat(cache.contains(Book.class, book3.getId()), is(false));
	}
}
