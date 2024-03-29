package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;
import play.libs.*;
import play.cache.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	@Before
	static void addDefault(){
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}

    public static void index() {
        Post frontPost = Post.find("order by postedAt desc").first();
		List<Post> olderPosts = Post.find(
			"order by postedAt desc"
		).from(1).fetch(10);
		render(frontPost, olderPosts);
    }

	public static void show(Long id) {
		Post post = Post.findById(id);
		String randomId = Codec.UUID();
		render(post, randomId);
	}
	
	public static void postComment(
			Long postId, 
			@Required(message="Author is required") String author, 
			@Required(message="A message is required") String content,
			@Required(message="Please type the code") String code,
			String randomId) 
	{
		Post post = Post.findById(postId);
		if(!Play.id.equals("test")) {
		    validation.equals(code, Cache.get(randomId)).message("Invalid code. Please type it again");
		}
		if(validation.hasErrors()) {
			render("Application/show.html", post, randomId);
		}
		post.addComment(author, content);
		flash.success("Thanks for posting %s", author);
		Cache.delete(randomId);
		show(postId);
	}
	
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
	    String code = captcha.getText("#E4EAFD");
	    Cache.set(id, code, "10mn");
		renderBinary(captcha);
	}
	
	public static void listTagged(String tag) {
		List<Post> posts = Post.findTaggedWith(tag);
		render(tag, posts);
	}

}