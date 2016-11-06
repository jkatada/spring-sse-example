package com.example.chat;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/chat")
public class ChatController {

	private List<SseEmitter> sseEmitters = new ArrayList<>();

	@GetMapping("/connect")
	public SseEmitter connect() {
		SseEmitter sseEmitter = new SseEmitter();
		sseEmitters.add(sseEmitter);
		sseEmitter.onTimeout(() -> sseEmitters.remove(sseEmitter));
		return sseEmitter;
	}

	@PostMapping("/send")
	public void send(@RequestBody Message message) {
        for (SseEmitter sseEmitter : this.sseEmitters) {
			String dateTime = message.getDateTime()
					.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
			String messageStr = dateTime + " [" + message.getName() + "]: " + message.getMessage();
            try {
				sseEmitter.send(messageStr, MediaType.APPLICATION_JSON);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}


}