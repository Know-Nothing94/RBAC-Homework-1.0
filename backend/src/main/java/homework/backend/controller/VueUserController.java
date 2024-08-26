package homework.backend.controller;

import homework.backend.model.VueUser;
import homework.backend.service.VueUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vue_users")
public class VueUserController {

    @Autowired
    private VueUserService vueUserService;

    @GetMapping
    public List<VueUser> getVueUsers() {
        return vueUserService.findAllVueUsers();
    }

    @PostMapping
    public VueUser createVueUser(@RequestBody VueUser user) {
        vueUserService.createVueUser(user);
        return user;
    }

    @PostMapping("/save")
    public VueUser saveOrUpdateVueUser(@RequestBody VueUser user) {
        if (user.getId() == null) {
            vueUserService.createVueUser(user);  // 新增用户
        } else {
            vueUserService.updateVueUser(user);  // 更新用户
        }
        return user;
    }
//时区转换之前
//    @PostMapping("/fetch")
//    public Map<String, Object> fetchUsers(@RequestBody Map<String, Object> filters) {
//        int pageNum = (int) filters.get("pageNum");
//        int pageSize = (int) filters.get("pageSize");
//        String username = (String) filters.get("username");
//
//        // 处理日期范围
//        List<String> createTime = (List<String>) filters.get("createTime");
//        String startDate = null;
//        String endDate = null;
//        if (createTime != null && createTime.size() == 2) {
//            startDate = createTime.get(0);
//            endDate = createTime.get(1);
//        }
//
//        // 打印出获取到的createTime的值
//        System.out.println("Received createTime: " + createTime);
//        System.out.println("Parsed startDate: " + startDate);
//        System.out.println("Parsed endDate: " + endDate);
//
//        List<VueUser> users = vueUserService.findUsersByPageAndFilters(pageNum, pageSize, username, startDate, endDate);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("list", users);
//        response.put("total", vueUserService.countUsersByFilters(username, startDate, endDate));
//
//        return response;
//    }
    @PostMapping("/fetch")
    public Map<String, Object> fetchUsers(@RequestBody Map<String, Object> filters) {
        int pageNum = (int) filters.get("pageNum");
        int pageSize = (int) filters.get("pageSize");
        String username = (String) filters.get("username");

        // 处理日期范围
        List<String> createTime = (List<String>) filters.get("createTime");
        String startDate = null;
        String endDate = null;
        if (createTime != null && createTime.size() == 2) {
            startDate = convertToLocalDateTime(createTime.get(0));
            endDate = convertToLocalDateTime(createTime.get(1));
            endDate = adjustEndDate(endDate); // 调整结束日期
        }

        List<VueUser> users = vueUserService.findUsersByPageAndFilters(pageNum, pageSize, username, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("list", users);
        response.put("total", vueUserService.countUsersByFilters(username, startDate, endDate));

        return response;
    }

//    时区转换之后

    // 将ISO时间字符串转换为本地时间字符串
    private String convertToLocalDateTime(String isoDateTime) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(isoDateTime);
        LocalDateTime localDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String adjustEndDate(String endDate) {
        LocalDateTime dateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dateTime = dateTime.with(LocalTime.MAX); // 设置为当天的最后一秒 23:59:59
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    @PutMapping("/{id}")
    public VueUser updateVueUser(@PathVariable Long id, @RequestBody VueUser user) {
        user.setId(id);
        vueUserService.updateVueUser(user);
        return user;
    }

    @DeleteMapping
    public void deleteVueUsers(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        for (Long id : ids) {
            vueUserService.deleteVueUser(id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteVueUser(@PathVariable Long id) {
        vueUserService.deleteVueUser(id);
    }
}
