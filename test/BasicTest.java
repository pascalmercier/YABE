import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "secret", "Bob").save();

		User bob = User.find("byEmail", "bob@gmail.com").first();
		
		assertNotNull(bob);
		assertEquals("Bob", bob.fullname);
    }

	@Test
	public void tryConnectAsUser(){
		new User("bob@gmail.com", "secret", "Bob").save();
		
		assertNotNull(User.connect("bob@gmail.com", "secret"));
		assertNull(User.connect("bob@gmail.com", "badPassword"));
		assertNull(User.connect("tom@gmail.com", "secret"));
	}
	
	@Test
	public void createPost() {
		// Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();

	    // Create a new post
	    new Post(bob, "My first post", "Hello world").save();

	    // Test that the post has been created
	    assertEquals(1, Post.count());

	    // Retrieve all posts created by Bob
	    List<Post> bobPosts = Post.find("byAuthor", bob).fetch();

	    // Tests
	    assertEquals(1, bobPosts.size());
	    Post firstPost = bobPosts.get(0);
	    assertNotNull(firstPost);
	    assertEquals(bob, firstPost.author);
	    assertEquals("My first post", firstPost.title);
	    assertEquals("Hello world", firstPost.content);
	    assertNotNull(firstPost.postedAt);
	}
	
	@Test
	public void postComments() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();

	    // Create a new post
	    Post bobPost = new Post(bob, "My first post", "Hello world").save();

	    // Post a first comment
	    new Comment(bobPost, "Jeff", "Nice post").save();
	    new Comment(bobPost, "Tom", "I knew that !").save();

	    // Retrieve all comments
	    List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();

	    // Tests
	    assertEquals(2, bobPostComments.size());

	    Comment firstComment = bobPostComments.get(0);
	    assertNotNull(firstComment);
	    assertEquals("Jeff", firstComment.author);
	    assertEquals("Nice post", firstComment.content);
	    assertNotNull(firstComment.postedAt);

	    Comment secondComment = bobPostComments.get(1);
	    assertNotNull(secondComment);
	    assertEquals("Tom", secondComment.author);
	    assertEquals("I knew that !", secondComment.content);
	    assertNotNull(secondComment.postedAt);
	}

	@Test
	public void useTheCommentsRelation() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();

	    // Create a new post
	    Post bobPost = new Post(bob, "My first post", "Hello world").save();

	    // Post a first comment
	    bobPost.addComment("Jeff", "Nice post");
	    bobPost.addComment("Tom", "I knew that !");

	    // Count things
	    assertEquals(1, User.count());
	    assertEquals(1, Post.count());
	    assertEquals(2, Comment.count());

	    // Retrieve Bob's post
	    bobPost = Post.find("byAuthor", bob).first();
	    assertNotNull(bobPost);

	    // Navigate to comments
	    assertEquals(2, bobPost.comments.size());
	    assertEquals("Jeff", bobPost.comments.get(0).author);

	    // Delete the post
	    bobPost.delete();

	    // Check that all comments have been deleted
	    assertEquals(1, User.count());
	    assertEquals(0, Post.count());
	    assertEquals(0, Comment.count());
	}
	
	@Test
	public void testTags() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();

	    // Create a new post
	    Post bobPost = new Post(bob, "My first post", "Hello world").save();
	    Post anotherBobPost = new Post(bob, "Hop", "Hello world").save();

	    // Well
	    assertEquals(0, Post.findTaggedWith("Red").size());

	    // Tag it now
	    bobPost.tagItWith("Red").tagItWith("Blue").save();
	    anotherBobPost.tagItWith("Red").tagItWith("Green").save();

	    // Check
	    assertEquals(2, Post.findTaggedWith("Red").size());        
	    assertEquals(1, Post.findTaggedWith("Blue").size());
	    assertEquals(1, Post.findTaggedWith("Green").size());
		assertEquals(1, Post.findTaggedWith("Red", "Blue").size());   
		assertEquals(1, Post.findTaggedWith("Red", "Green").size());   
		assertEquals(0, Post.findTaggedWith("Red", "Green", "Blue").size());  
		assertEquals(0, Post.findTaggedWith("Green", "Blue").size());
		
		List<Map> cloud = Tag.getCloud();
		assertEquals(
		    "[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]", 
		    cloud.toString()
		);

	}
}
