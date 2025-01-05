package org.ciaochat.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.MessageDto;
import org.ciaochat.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageDto messageDto) {
        chatService.sendMessage(messageDto);
    }

//    @GetMapping("/all_chats")
//    public String getAllChats(Model model, HttpSession session) {
//
//        return "home";
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

    @PostMapping("/create_chat")
    public String createChat(@RequestBody ChatDto chatDto) {
        chatService.createChat(chatDto);
        return "redirect:/chat/home";
    }
}
