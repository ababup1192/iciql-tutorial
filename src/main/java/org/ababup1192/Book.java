package org.ababup1192;

import com.iciql.Iciql;

import java.io.Serializable;

@Iciql.IQTable(name="book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Iciql.IQColumn(primaryKey=true, nullable=false)
    public Integer id;

    @Iciql.IQColumn(length=254, nullable=false)
    public String title;

    public Book() {}
}
