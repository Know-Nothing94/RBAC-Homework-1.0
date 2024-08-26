package homework.backend.mapper;

import homework.backend.model.VueUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VueUserMapper {

    // 查询所有用户
    List<VueUser> findAllVueUsers();

    // 通过ID查询用户
    VueUser findVueUserById(Long id);

//    // 通过分页和筛选条件查询用户
//    List<VueUser> findUsersByPageAndFilters(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("username") String username);
//
//    // 计算符合筛选条件的用户数量
//    int countUsersByFilters(@Param("username") String username);

    // 修改分页和筛选条件的查询方法，加入日期筛选
    List<VueUser> findUsersByPageAndFilters(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("username") String username, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // 修改用户数量统计方法，加入日期筛选
    int countUsersByFilters(@Param("username") String username, @Param("startDate") String startDate, @Param("endDate") String endDate);


    // 插入新用户
    void insertVueUser(VueUser vueUser);

    // 更新用户信息
    void updateVueUser(VueUser vueUser);

    // 删除用户
    void deleteVueUser(Long id);
}
