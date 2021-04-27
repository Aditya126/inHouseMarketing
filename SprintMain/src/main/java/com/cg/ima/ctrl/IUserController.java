package com.cg.ima.ctrl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cg.ima.entity.User;
import com.cg.ima.service.IUserService;

@RestController
@RequestMapping("/user")
@Validated
public class IUserController {
	private Logger logger = LoggerFactory.getLogger(IUserController.class);

	@Autowired
	private IUserService userService;

	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping("/login")
	public ResponseEntity<User> createSession(@RequestBody @Valid User user, @Param("note") @Valid String note,
			HttpServletRequest request) {
		User loginUser = userService.login(user);
		if (loginUser == null) {
			logger.info("Sorry! userId or password is incorrect");
			return new ResponseEntity<User>(loginUser, HttpStatus.NOT_FOUND);
		}
		else
		{
		List<String> notes = (List<String>) request.getSession().getAttribute("NOTES_SESSION");
		if (notes == null) {
			notes = new ArrayList<>();
		}
		notes.add(note);
		logger.debug("notes= " + notes);
		request.getSession().setAttribute("NOTES_SESSION", notes);
		}
		return new ResponseEntity<User>(loginUser, HttpStatus.OK);
	}

	@PostMapping("/invalidate/session")
	public String destroySession(HttpServletRequest request) {
		User logoutUser = userService.logout();
		if (logoutUser == null) {
			return "Not Loggedin";
		}
		logger.info("Invalidating session");
		request.getSession().invalidate();
		return "Session invalidated";
	}

	@PostMapping("/userAdd")
	public ResponseEntity<User> addsUser(@RequestBody @Valid User user) {
		User addedUser = userService.addUser(user);
		if (addedUser == null) {
			logger.info("User cannot be added");
			return new ResponseEntity<User>(addedUser, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(addedUser, HttpStatus.OK);
	}

	@PutMapping("/userUpdate")
	public ResponseEntity<User> updateUser(@RequestBody @Valid User user) {
		User updatedUser = userService.editUser(user);
		if (updatedUser == null) {
			logger.info("User cannot be updated");
			return new ResponseEntity<User>(updatedUser, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
	}

	@DeleteMapping("/userDelete/{userId}")
	public ResponseEntity<User> deleteUser(@PathVariable("userId") @Valid String userId) {
		User deletedUser = null;
		deletedUser = userService.removeUser(userId);
		if (deletedUser == null) {
			logger.info("User cannot be deleted");
			return new ResponseEntity<User>(deletedUser, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(deletedUser, HttpStatus.OK);
	}

	@GetMapping("/getsessions")
	public List<String> getSessions(HttpServletRequest request) {
		List<String> sessions = (List<String>) request.getSession().getAttribute("NOTES_SESSION");
		logger.info("Session ID: " + request.getSession().getId());
		logger.info("getting sessions: " + sessions);
		return sessions;
	}

}
