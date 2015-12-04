package org.ababup1192;

import com.iciql.Iciql;

import java.io.Serializable;
import java.util.Objects;

@Iciql.IQTable(name="book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Iciql.IQColumn(primaryKey=true, autoIncrement = true, nullable = false)
    public Integer id;

    @Iciql.IQColumn(length=254, nullable=false)
    public String title;

    public Book() {}

    @Override
    public String toString(){
        return "Book(" + id + ", " + title + ")";
    }

    public boolean equals(Object target){
        if(this == target) return true;
        else if(!(target instanceof Book)){
            return false;
        }else{
            Book targetBook = (Book) target;
            return Objects.equals(this.id, targetBook.id) &&
                    Objects.equals(this.title, targetBook.title);
        }
    }
}
