package de.dema.pd3.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import de.dema.pd3.WithMockCurrentUser;
import de.dema.pd3.services.UserService;
import de.dema.pd3.services.VoteService;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@WithMockCurrentUser
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;
	
	@MockBean
	private VoteService voteService;
	
	@Test
    public void testSendMessage() throws Exception {
        String text = "testtext";
		this.mvc.perform(post("/user/send-message/user")
        		.with(csrf())
        		.param("targetId", "2")
        		.param("text", text)
        		.accept(MediaType.TEXT_HTML))
            	.andExpect(status().is3xxRedirection())
            	.andExpect(model().attribute("id", equalTo("2")))
            	.andExpect(redirectedUrlPattern("/user/profile?*"));
        
		verify(userService).sendMessage(text, 1L, 2L);

		this.mvc.perform(post("/user/send-message/room")
        		.with(csrf())
        		.param("targetId", "17")
        		.param("text", text)
        		.accept(MediaType.TEXT_HTML))
            	.andExpect(status().is3xxRedirection())
            	.andExpect(model().attribute("room", equalTo("17")))
            	.andExpect(redirectedUrlPattern("/user/inbox?*"));

		verify(userService).sendMessageToChatroom(text, 1L, 17L);
	}

}
