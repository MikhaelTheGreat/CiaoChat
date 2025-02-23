package org.ciaochat.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.ChatDto;
import org.ciaochat.dto.MessageDto;
import org.ciaochat.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/home")
    public String home(Model model, HttpSession httpSession) {
        Long userId = (Long) httpSession.getAttribute("userId");
        List<ChatDto> chats = chatService.getUserAllChatsNames(userId);

        model.addAttribute("chats", chats);
        model.addAttribute("userId", userId);
        model.addAttribute("username", httpSession.getAttribute("username"));

        return "home";
    }

    @GetMapping("/{id}")
    public String chat(@PathVariable Long id, Model model, HttpSession httpSession) {
        ChatDto chatDto = chatService.getChat(id);
        model.addAttribute("chatName", chatDto.getName());

        List<MessageDto> messageDtos = chatService.getChatMessages(id);
        model.addAttribute("messages", messageDtos);
        model.addAttribute("userId", httpSession.getAttribute("userId"));
        model.addAttribute("username", httpSession.getAttribute("username"));
        model.addAttribute("chatId", id);

        return "chat";
    }
}
