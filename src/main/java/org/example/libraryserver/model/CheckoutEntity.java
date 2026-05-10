package org.example.libraryserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "checkouts")
public class CheckoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "book_id", nullable = false)
    private int bookId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "checked_out_at", nullable = false)
    private String checkedOutAt;

    @Column(name = "due_date", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String dueDate = "";

    @Column(name = "returned_at")
    private String returnedAt;

    public CheckoutEntity() {}

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }
    public int getBookId()                { return bookId; }
    public void setBookId(int bookId)     { this.bookId = bookId; }
    public int getUserId()                { return userId; }
    public void setUserId(int userId)     { this.userId = userId; }
    public String getCheckedOutAt()       { return checkedOutAt; }
    public void setCheckedOutAt(String t) { this.checkedOutAt = t; }
    public String getDueDate()            { return dueDate; }
    public void setDueDate(String d)      { this.dueDate = d; }
    public String getReturnedAt()         { return returnedAt; }
    public void setReturnedAt(String r)   { this.returnedAt = r; }
}
