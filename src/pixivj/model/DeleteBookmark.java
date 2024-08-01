package pixivj.model;

public class DeleteBookmark extends AddBookmark {
    public DeleteBookmark(long id) {
        setIllustId(id);
        setRestrict(Restrict.PUBLIC);
    }
}
