package com.data.ets.controller;

import com.data.ets.dto.LeaveDTO;
import com.data.ets.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/leaves")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    @GetMapping
    public String listLeaves(Model model) {
        List<LeaveDTO> leaves = leaveService.getAllLeaves();
        model.addAttribute("leaves", leaves);
        return "admin/leaves/list";
    }

    @GetMapping("/pending")
    public String pendingLeaves(Model model) {
        List<LeaveDTO> pendingLeaves = leaveService.getPendingLeaves();
        model.addAttribute("leaves", pendingLeaves);
        return "admin/leaves/pending";
    }

    @PostMapping("/approve/{id}")
    public String approveLeave(@PathVariable Long id, @RequestParam String approvedBy) {
        leaveService.approveLeave(id, approvedBy);
        return "redirect:/admin/leaves/pending";
    }

    @PostMapping("/reject/{id}")
    public String rejectLeave(@PathVariable Long id, @RequestParam String approvedBy) {
        leaveService.rejectLeave(id, approvedBy);
        return "redirect:/admin/leaves/pending";
    }

    @GetMapping("/delete/{id}")
    public String deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return "redirect:/admin/leaves";
    }
}

