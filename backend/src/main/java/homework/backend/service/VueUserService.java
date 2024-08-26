package homework.backend.service;

import homework.backend.model.VueUser;

import java.util.List;

public interface VueUserService {

    void createVueUser(VueUser vueUser);

    void updateVueUser(VueUser vueUser);

    List<VueUser> findAllVueUsers();

    List<VueUser> findUsersByPageAndFilters(int pageNum, int pageSize, String username, String startDate, String endDate);
    int countUsersByFilters(String username, String startDate, String endDate);

    VueUser findVueUserById(Long id);

    void deleteVueUser(Long id);
}
