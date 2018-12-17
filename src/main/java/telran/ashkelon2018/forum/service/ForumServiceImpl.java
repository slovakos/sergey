package telran.ashkelon2018.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.exceptions.UserForbiddenException;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	ForumRepository repository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Autowired
	UserAccountRepository userRepository;

	@Override
	public Post addNewPost(NewPostDto newPost) {
		Post post = new Post(newPost.getTitle(), newPost.getContent(), newPost.getAuthor(), newPost.getTags());
		return repository.save(post);
	}

	@Override
	public Post getPost(String id) {

		return repository.findById(id).orElse(null);
	}

	@Override
	public Post removePost(String id, String token) {

		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
			UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
			String authorByPost = post.getAuthor();
			if (authorByPost.equals(credentials.getLogin()) || userAccount.getRoles().contains("admin") || userAccount.getRoles().contains("moderator")) {
				repository.delete(post);
			}else {
				throw new UserForbiddenException();
			}
			
		}
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdateDto, String token) {

		Post post = repository.findById(postUpdateDto.getId()).orElse(null);
		if (post != null) {
			AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
			String authorByPost = post.getAuthor();
			if (authorByPost.equals(credentials.getLogin())) {
				post.setContent(postUpdateDto.getContent());
				repository.save(post);
			} else {
				throw new UserForbiddenException();
			}

		}
		return post;

	}

	@Override
	public boolean addLike(String id) {
		Post post = repository.findById(id).orElse(null);
		if (post == null) {
			return false;
		}
		post.addLike();
		repository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment) {
		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			Comment comment = new Comment(id, newComment.getMessage());
			post.addComment(comment);
			repository.save(post);

		}
		return post;
	}

	@Override
	public Iterable<Post> findByTagsIn(List<String> tags) {

		return repository.findByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findByAuthor(String author) {

		return repository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto datesDto) {

		return repository.findByDateCreatedBetween(datesDto.getFromDate(), datesDto.getToDate());

	}

}
