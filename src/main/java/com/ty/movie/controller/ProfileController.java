package com.ty.movie.controller;

import com.ty.movie.model.User;
import com.ty.movie.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Method;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Display current user's profile.
     * Builds simple attributes (username, email, roles, createdAt) to avoid template errors if User fields differ.
     */
    @GetMapping("/profile")
    public String profile(Principal principal, Model model, RedirectAttributes ra) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "User not found");
            return "redirect:/login";
        }

        User user = opt.get();

        // Always add username
        model.addAttribute("username", safeInvokeString(user, "getUsername", "Unknown"));

        // Try to read email if the getter exists, otherwise null
        model.addAttribute("email", safeInvokeString(user, "getEmail", null));

        // Try to read createdAt (and format) if exists
        String createdAtFormatted = safeFormatDate(user, "getCreatedAt");
        model.addAttribute("createdAt", createdAtFormatted);

        // Roles: attempt to read getRoles() and extract a human string list (role.name or role.getName())
        List<String> roles = extractRoles(user);
        model.addAttribute("roles", roles);

        // Always provide the raw user id if available (for admin edit link)
        model.addAttribute("userId", safeInvokeString(user, "getId", null));

        return "profile/view";
    }

    // Helper: attempt to call a no-arg getter and return string via toString()
    private String safeInvokeString(Object obj, String methodName, String defaultValue) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            return val != null ? val.toString() : defaultValue;
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // Helper: format createdAt if present (accepts java.time types or java.util.Date)
    private String safeFormatDate(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            if (val == null) return null;

            // If it's a java.time.LocalDateTime / LocalDate / Instant, handle those
            String pattern = "dd MMM yyyy";
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);

            if (val instanceof java.time.LocalDateTime) {
                return dtf.format((java.time.LocalDateTime) val);
            }
            if (val instanceof java.time.LocalDate) {
                return dtf.format((java.time.LocalDate) val);
            }
            if (val instanceof java.time.Instant) {
                return dtf.format(((java.time.Instant) val).atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            }
            // fallback to toString for other date types (e.g., java.util.Date)
            return val.toString();
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // Helper: try to read getRoles() -> Collection and extract a meaningful name for each role object
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Object user) {
        List<String> result = new ArrayList<>();
        try {
            Method getRoles = user.getClass().getMethod("getRoles");
            Object rolesObj = getRoles.invoke(user);
            if (rolesObj instanceof Collection<?>) {
                Collection<?> coll = (Collection<?>) rolesObj;
                for (Object r : coll) {
                    if (r == null) continue;
                    // try common getters on role object
                    String roleName = null;
                    try {
                        Method m1 = r.getClass().getMethod("getName");
                        Object v = m1.invoke(r);
                        if (v != null) roleName = v.toString();
                    } catch (NoSuchMethodException ignored) {}
                    if (roleName == null) {
                        try {
                            Method m2 = r.getClass().getMethod("getRole");
                            Object v = m2.invoke(r);
                            if (v != null) roleName = v.toString();
                        } catch (NoSuchMethodException ignored) {}
                    }
                    if (roleName == null) {
                        // fallback to toString()
                        roleName = r.toString();
                    }
                    result.add(roleName);
                }
            }
        } catch (NoSuchMethodException e) {
            // no roles getter — ignore
        } catch (Exception e) {
            // ignore other reflection errors
        }
        return result;
    }
}
