package org.ciaochat.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.MessageDto;
import org.ciaochat.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

//    @PostMapping("/send")
//    public void sendMessage(@RequestBody MessageDto messageDto) {
//        chatService.sendMessage(messageDto);
//    }

    @GetMapping("/home")
    public String home(Model model, HttpSession httpSession) {
        Long userId = (Long) httpSession.getAttribute("userId");
        List<ChatDto> chats = chatService.getUserAllChatsNames(userId);

        model.addAttribute("chats", chats);
        model.addAttribute("userId", userId);
        model.addAttribute("username", httpSession.getAttribute("username"));

        return "home";
    }

//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    @PostMapping("/create_chat")
//    public ChatDto createChat(@RequestBody ChatDto chatDto) {
//        return chatService.createChat(chatDto);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteChat(@PathVariable Long id) {
//        chatService.deleteChat(id);
//
//        return ResponseEntity.ok().build();
//    }
}
