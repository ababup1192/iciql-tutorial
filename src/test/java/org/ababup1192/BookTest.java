package org.ababup1192;

import com.iciql.Db;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Book book = new Book();

        // Bookテーブルからイニシャルが「a」であるリストを選択
        List<Book> selectedBooks = db.from(book).where(book.title).like("a%").select();

        // イニシャルが「a」である本のリストと選択した本のリストが一致するかどうか。
        assertThat(selectedBooks, is(initialABooks));
    }

    @Test
    public void testSelectWithDao() throws Exception{
        BookDao bookDao = db.open(BookDao.class);

        Book selectedBook = bookDao.getBook(1);
        Book nullBook = bookDao.getBook(-1);

        assertThat(selectedBook, is(insertedBooks.get(0)));
        assertThat(nullBook, new IsNull<>());
    }

    @Test
    public void testInsert() throws Exception {
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
        // Bookテーブルの最後の本は挿入した本と一致するか。
        assertThat(selectedBooks.get(selectedBooks.size() - 1), is(newBook));
    }

    @Test
    public void testUpdate() throws Exception {
        Book book = new Book();

        // イニシャルが「a」のもののタイトルを「abc」へ変更。
        db.from(book).set(book.title).to("abc")
                .where(book.title).like("a%").update();

        // タイトルが「abc」の本のリストを取得。
        List<Book> selectedBooks = db.from(book).where(book.title).is("abc").orderBy(book.id).select();

        // イニシャルが「a」のタイトルの本のリストのタイトルを「abc」」へ変えたリストを作る。
        List<Book> abcBooks = initialABooks.stream().map(aBook ->
        {
            aBook.title = "abc";
            return aBook;
        }).collect(Collectors.toList());

        // Bookテーブルの「abc」本のリストとabcBooksが一致するかどうか。
        assertThat(selectedBooks, is(abcBooks));
    }

    @Test
    public void testUpdateByEntity() throws Exception {
        Book book = new Book();

        // イニシャルが「a」のタイトルの本のリストのタイトルを「abc」」へ変えたリストを作る。
        List<Book> abcBooks = initialABooks.stream().map(aBook ->
        {
            aBook.title = "abc";
            return aBook;
        }).collect(Collectors.toList());

        // abcBooksのエレメントを利用してアップデートをする。
        abcBooks.forEach(abcBook -> db.update(abcBook));

        // タイトルが「abc」の本のリストを取得。
        List<Book> selectedBooks = db.from(book).where(book.title).is("abc").orderBy(book.id).select();

        // Bookテーブルの「abc」本のリストとabcBooksが一致するかどうか。
        assertThat(selectedBooks, is(abcBooks));
    }

    @Test
    public void testUpdateByEntityList() throws Exception {
        Book book = new Book();

        // イニシャルが「a」のタイトルの本のリストのタイトルを「abc」」へ変えたリストを作る。
        List<Book> abcBooks = initialABooks.stream().map(aBook ->
        {
            aBook.title = "abc";
            return aBook;
        }).collect(Collectors.toList());

        // abcBooksを利用してアップデートをする。
        db.updateAll(abcBooks);

        // タイトルが「abc」の本のリストを取得。
        List<Book> selectedBooks = db.from(book).where(book.title).is("abc").orderBy(book.id).select();

        // Bookテーブルの「abc」本のリストとabcBooksが一致するかどうか。
        assertThat(selectedBooks, is(abcBooks));
    }

    @Test
    public void testDelete() throws Exception {
        Book book = new Book();

        // イニシャルが「a」の本を削除
        db.from(book).where(book.title).like("a%").delete();

        // Bookテーブルから選択
        List<Book> selectedBooks = db.from(book).select();

        // 挿入した本のリストからイニシャル
        insertedBooks.removeAll(initialABooks);

        // Bookテーブルの「abc」本のリストとabcBooksが一致するかどうか。
        assertThat(selectedBooks, is(insertedBooks));
    }


    @Test
    public void testDeleteByEntity() throws Exception {
        Book book = new Book();

        // イニシャルが「a」の本をinitialBooksのエレメントを利用して削除
        initialABooks.forEach(initialABook -> {
            db.delete(initialABook);
        });

        // Bookテーブルから選択
        List<Book> selectedBooks = db.from(book).select();

        // 挿入した本のリストからイニシャル
        insertedBooks.removeAll(initialABooks);

        // Bookテーブルの「abc」本のリストとabcBooksが一致するかどうか。
        assertThat(selectedBooks, is(insertedBooks));
    }

    @Test
    public void testDeleteByEntityList() throws Exception {
        Book book = new Book();

        // イニシャルが「a」の本をinitialBooksのエレメントを利用して削除
        db.deleteAll(initialABooks);

        // Bookテーブルから選択
        List<Book> selectedBooks = db.from(book).select();

        // 挿入した本のリストからイニシャル
        insertedBooks.removeAll(initialABooks);

        // Bookテーブルの「abc」本のリストとabcBooksが一致するかどうか。
        assertThat(selectedBooks, is(insertedBooks));
    }

}
