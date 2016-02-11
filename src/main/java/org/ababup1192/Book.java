package org.ababup1192;

import com.iciql.Iciql;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

@Accessors(fluent = true)
@Iciql.IQTable(name = "book", primaryKey = "id")
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    @Iciql.IQColumn(primaryKey = true, autoIncrement = true)
    public Long id;

    @Iciql.IQColumn(length = 254, nullable = false)
    public String title;

    public Book() {
    }

    public Book(String title) {
        this.id = null;
        this.title = title;
    }


    public Book(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Book(" + id + ", " + title + ")";
    }

    public boolean equals(Object target) {
        if (this == target) return true;
        else if (!(target instanceof Book)) {
            return false;
        } else {
            Book targetBook = (Book) target;
            return Objects.equals(this.id, targetBook.id) &&
                    Objects.equals(this.title, targetBook.title);
        }
    }
}
