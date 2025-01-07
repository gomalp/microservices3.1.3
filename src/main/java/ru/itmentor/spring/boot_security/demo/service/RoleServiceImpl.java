package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    //конвертирует список строковых ролей в множество объектов Role
    public Set<Role> getRolesByName(List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Optional<Role> roleOpt = roleRepository.findByRole(roleName);
            roleOpt.ifPresent(roles::add);
        }
        return roles;
    }

    // В RoleServiceImpl
    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRole(roleName).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    @Override
    public boolean existsByRole(String role) {
        return roleRepository.existsByRole(role);
    }

    @Override
    public Set<Role> getRolesByIds(List<Long> roleIds) {
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }
}