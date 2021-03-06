package com.tracking.controller;

import com.tracking.dto.UserDto;
import com.tracking.mapper.UserMapper;
import com.tracking.model.registration.AppUser;
import com.tracking.model.registration.Role;
import com.tracking.service.file.FileStorageService;
import com.tracking.service.user.RoleService;
import com.tracking.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private RoleService roleService;
    private UserMapper userMapper;
    private FileStorageService fileStorageService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, UserMapper userMapper, FileStorageService fileStorageService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/add_role/")
    public String addRole(@RequestParam("user_id") Long user_id,
                          @RequestParam("role_id") Long role_id) {
        AppUser userFromDb = userService.findById(user_id);
        Role roleFromDb = roleService.findById(role_id);
        userService.addRoleToUser(userFromDb, roleFromDb);
        return "redirect:/admin/list";
    }

    @GetMapping("/update/{id}")
    public String findUserById(@PathVariable("id") Long id, Model model) {
        AppUser userFromDb = userService.findById(id);
        UserDto userDto = userMapper.toDto(userFromDb);
        model.addAttribute("userDto", userDto);
        return "update_user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, UserDto userDto) {

        AppUser appUser = userMapper.toEntity(userDto);
        appUser.setId(id);
        userService.update(appUser);
        return "redirect:/admin/list";
    }

    @GetMapping("/list")
    public String getEmployeeList() {
        return "user_list";
    }

    @GetMapping("/remove_role")
    public String removeRole(@RequestParam("user_id") Long user_id,
                             @RequestParam("role_id") Long role_id) {
        AppUser userFromDb = userService.findById(user_id);
        Role roleFromDb = roleService.findById(role_id);
        userService.removeRoleFromUser(userFromDb, roleFromDb);
        return "redirect:/admin/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin/list";
    }

    @GetMapping("/config")
    public String getConfigPage() {
        return "config";
    }

    @PostMapping("/save/default/image")
    public String saveImage(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            fileStorageService.saveDefaultImage(file);
        }
        return "redirect:/admin/config";
    }

    @ModelAttribute("users")
    public List<AppUser> getAppUsers() {
        return userService.findAll();
    }

    @ModelAttribute("roles")
    public List<Role> getAppRoles() {
        return roleService.findAll();
    }


}
