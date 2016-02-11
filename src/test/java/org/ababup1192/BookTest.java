package org.ababup1192;

import com.iciql.Db;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BookTest {
    private Db db;
    private List<Book> insertedBooks;
    private List<Book> initialABooks;
    private static final int NUM_OF_BOOKS = 1000;

    /*
        CREATE TABLE book (
            id       SERIAL PRIMARY KEY,
            title    VARCHAR(254)
        );
     */

    @Before
    public void setDb() {
        // 初期化
        db = Db.open("jdbc:postgresql://localhost:5432/vagrant", "vagrant", "vagrant");
        db.dropTable(Book.class);

        long bookCount = 0;
        insertedBooks = new ArrayList<>();
        initialABooks = new ArrayList<>();

        for (int i = 0; i < NUM_OF_BOOKS; i++) {
            String title = RandomStringUtils.randomAlphabetic(100).toLowerCase();
            Book book = new Book(title);

            // 生成した本をDBに格納。
            db.insert(book);
            book.id = ++bookCount;

            // イニシャルが「a」だったら
            if (title.matches("a\\w.+")) {
                initialABooks.add(book);
            }
            insertedBooks.add(book);
        }
    }

    @Test
    public void testSelect() throws Exception {
        // Bookテーブルから選択
        List<Book> selectedBooks = db.from(new Book()).select();

        // 挿入した本と選択した本のリストが一致するかどうか。
        assertThat(selectedBooks, is(insertedBooks));
    }

    @Test
    public void testSelectWithFilter() throws Exception {
        // Bookテーブルからイニシャルが「a」であるリストを選択
        Book book = new Book();

        List<Book> selectedBooks = db.from(book).where(book.title).like("a%").select();

        // イニシャルが「a」である本のリストと選択した本のリストが一致するかどうか。
        assertThat(selectedBooks, is(initialABooks));
    }

    @Test
    public void testInsert() throws Exception {
        // Bookテーブルからイニシャルが「a」であるリストを選択
        Book book = new Book();

        // 新しく本をDBに格納
        Book newBook = new Book("abc");
        db.insert(newBook);

        // Bookテーブルから選択
        List<Book> selectedBooks = db.from(book).select();

        // 挿入本リストの最後にも新しい本を挿入
        newBook.id = new Integer(insertedBooks.size() + 1).longValue();
        insertedBooks.add(newBook);

        // 挿入したである本のリストと選択した本のリストが一致するかどうか。
        assertThat(selectedBooks, is(insertedBooks));
        // Bookテーブルの最後の本は挿入した本と一緒するか。
        assertThat(selectedBooks.get(selectedBooks.size() - 1), is(newBook));
    }


}
