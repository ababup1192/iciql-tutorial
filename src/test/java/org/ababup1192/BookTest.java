package org.ababup1192;

import com.iciql.Db;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BookTest {
    Db db;

    /*
        CREATE TABLE book (
            id       int8    primary key,
            title    varchar(254)
        );
     */

    @Before
    public void setDb() {
        db = Db.open("jdbc:postgresql://localhost:5432/vagrant", "vagrant", "vagrant");
    }

    @Test
    public void testCRUD() throws Exception {
        Book book = new Book();
        // テーブルを削除
        db.dropTable(Book.class);

        // テーブルが空であることを確認。
        List<Book> emptyBooks = db.from(book).select();
        assertThat(emptyBooks.size(), is(0));

        Book newBook = new Book();
        newBook.title = "title";

        // 新しい本を挿入し、タイトルと一致するかを確認
        db.insert(newBook);
        Book findBook = db.from(book).where(book.title).is("title").selectFirst();
        assertThat(newBook.title, is(findBook.title));

        // レコードを更新して、新しいタイトルと一致するか確認。
        findBook.title = "updateTitle";
        db.update(findBook);
        Book updatedBook = db.from(book).where(book.title).is("updateTitle").selectFirst();

        assertThat(findBook, is(updatedBook));

        db.delete(updatedBook);

        // 再びテーブルが空であることを確認。
        emptyBooks = db.from(book).select();
        assertThat(emptyBooks.size(), is(0));
    }
}
