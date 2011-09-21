package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
@Table(schema="public", name = "comment")
public class Comment extends Model {
 	
	@Required
    public String author;
    
	@Required
	public Date postedAt;
     
    @Lob
	@Required
	@MaxSize(10000)
    public String content;
    
    @ManyToOne
	@Required
    public Post post;
    
    public Comment(Post post, String author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }

	public String toString() {
		return this.author + " - " + this.post.title + " (" + this.postedAt + ")"; 
	}
 
}
