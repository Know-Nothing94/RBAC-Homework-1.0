package homework.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import homework.backend.mapper.VueUserMapper;
import homework.backend.model.VueUser;
import homework.backend.service.VueUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class VueUserServiceImpl implements VueUserService {

    @Autowired
    private VueUserMapper vueUserMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();  // 用于JSON转换

    @Override
    public void createVueUser(VueUser vueUser) {
        vueUser.setRoleList(convertRoleListToJson(vueUser.getRoleList()));
        vueUserMapper.insertVueUser(vueUser);
    }

    @Override
    public void updateVueUser(VueUser vueUser) {
        vueUser.setRoleList(convertRoleListToJson(vueUser.getRoleList()));
        vueUserMapper.updateVueUser(vueUser);
    }

    private String convertRoleListToJson(String roleList) {
        try {
            // 将单个字符串角色包装为 JSON 数组格式
            return objectMapper.writeValueAsString(Collections.singletonList(roleList));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert roleList to JSON", e);
        }
    }


    @Override
    public List<VueUser> findAllVueUsers() {
        return vueUserMapper.findAllVueUsers();
    }

//    @Override
//    public List<VueUser> findUsersByPageAndFilters(int pageNum, int pageSize, String username) {
//        int offset = (pageNum - 1) * pageSize;
//        return vueUserMapper.findUsersByPageAndFilters(pageNum, pageSize, username);
//    }
    @Override
    public List<VueUser> findUsersByPageAndFilters(int pageNum, int pageSize, String username, String startDate, String endDate) {
        int offset = (pageNum - 1) * pageSize; // Calculate offset
        return vueUserMapper.findUsersByPageAndFilters(offset, pageSize, username, startDate, endDate); // Pass offset and filters
    }


    @Override
    public int countUsersByFilters(String username, String startDate, String endDate) {
        return vueUserMapper.countUsersByFilters(username, startDate, endDate);
    }
    @Override
    public VueUser findVueUserById(Long id) {
        return vueUserMapper.findVueUserById(id);
    }

    @Override
    public void deleteVueUser(Long id) {
        vueUserMapper.deleteVueUser(id);
    }
}
