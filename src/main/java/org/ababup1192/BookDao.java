package org.ababup1192;

import com.iciql.Dao;

public interface BookDao extends Dao {
    @Dao.SqlQuery("SELECT * FROM book WHERE id = :id")
    Book getBook(@Bind("id") long id);
}
