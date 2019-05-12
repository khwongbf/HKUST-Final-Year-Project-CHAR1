package hk.ust.char1.server.service;

import hk.ust.char1.server.dto.TenantGroupDTO;
import hk.ust.char1.server.model.Role;
import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.TenantGroup;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.RoleRepository;
import hk.ust.char1.server.repository.TenantGroupRepository;
import hk.ust.char1.server.repository.TenantRepository;
import hk.ust.char1.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class TenantGroupingService {
    private static final String OWNER_PREV_STRING = "GROUP_OWNER";

    private final TenantGroupRepository tenantGroupRepository;

    private final TenantRepository tenantRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public TenantGroupingService(TenantGroupRepository tenantGroupRepository, TenantRepository tenantRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.tenantGroupRepository = tenantGroupRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public boolean initiateGroup(String username, TenantGroupDTO tenantGroupDTO){
        if (!(tenantRepository.existsTenantByUsername(username) && userRepository.existsUserByUsername(username)) || tenantGroupRepository.existsTenantGroupByGroupName(tenantGroupDTO.getGroupName())){
            return false;
        } else{
	        Tenant tenant = tenantRepository.findTenantByUsername(username);
	        User user = userRepository.findUserByUsername(username);

            // Tenant already has a group
            if (!tenantGroupRepository.findAllByTenantsContaining(tenant).isEmpty()){
                return false;
            }
	        TenantGroup tenantGroup = new TenantGroup();
            tenantGroup.setGroupName(tenantGroupDTO.getGroupName());
            tenantGroup.setTenants(new ArrayList<>());
            tenantGroup.getTenants().add(tenant);
            tenantGroup.setGroupOwner(tenant);

            tenantGroupRepository.saveAndFlush(tenantGroup);

            if (!roleRepository.existsRoleByRole(OWNER_PREV_STRING)){
	            Role role = new Role();
                role.setRole(OWNER_PREV_STRING);
                roleRepository.saveAndFlush(role);
            }

	        Role role = roleRepository.findRoleByRole(OWNER_PREV_STRING);
            user.getRole().add(role);
            tenant.getRole().add(role);
            userRepository.saveAndFlush(user);
            tenantRepository.saveAndFlush(tenant);

            return true;
        }
    }

    @Transactional
    public boolean addToGroup(String ownerName, String username, String groupName){
        if (!tenantGroupRepository.existsTenantGroupByGroupName(groupName) || !tenantRepository.existsTenantByUsername(ownerName) || !tenantRepository.existsTenantByUsername(username)){
            return false;
        }else{
	        TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
            if (!tenantGroup.getGroupOwner().getUsername().equals(ownerName)){
                return false;
            }
	        Tenant tenantToAdd = tenantRepository.findTenantByUsername(username);
            tenantGroup.getTenants().add(tenantToAdd);
            tenantToAdd.setInTenantGroup(tenantGroup);
            tenantGroupRepository.saveAndFlush(tenantGroup);
            tenantRepository.saveAndFlush(tenantToAdd);
            return true;
        }
    }

    @Transactional
    public boolean changeGroupOwner(String oldOwnerName, String newOwnerName, String groupName){
        if (!(tenantGroupRepository.existsTenantGroupByGroupName(groupName) && tenantRepository.existsTenantByUsername(oldOwnerName) && tenantRepository.existsTenantByUsername(newOwnerName))){
            return false;
        }else{
	        TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
	        Tenant newOwner = tenantRepository.findTenantByUsername(newOwnerName);
	        Tenant oldOwner = tenantRepository.findTenantByUsername(oldOwnerName);
	        User newUser = userRepository.findUserByUsername(newOwnerName);
	        User oldUser = userRepository.findUserByUsername(oldOwnerName);

            if (!(tenantGroup.getTenants().contains(newOwner) && tenantGroup.getGroupOwner().getUsername().equals(oldOwnerName))){
                return false;
            }

            tenantGroup.setGroupOwner(newOwner);
            newOwner.setOwnerGroup(tenantGroup);
            oldOwner.setOwnerGroup(null);
            newOwner.getRole().add(roleRepository.findRoleByRole(OWNER_PREV_STRING));
            oldOwner.getRole().remove(roleRepository.findRoleByRole(OWNER_PREV_STRING));

            oldUser.getRole().remove(roleRepository.findRoleByRole(OWNER_PREV_STRING));
            newUser.getRole().add(roleRepository.findRoleByRole(OWNER_PREV_STRING));

            tenantGroupRepository.saveAndFlush(tenantGroup);
            tenantRepository.saveAndFlush(newOwner);
            tenantRepository.saveAndFlush(oldOwner);

            userRepository.saveAndFlush(oldUser);
            userRepository.saveAndFlush(newUser);

            return true;
        }
    }

    @Transactional
    public boolean removeFromGroup(String groupOwnerName, String memberNameToRemove, String groupName){
	    if (!(tenantGroupRepository.existsTenantGroupByGroupName(groupName) && tenantRepository.existsTenantByUsername(groupOwnerName) && tenantRepository.existsTenantByUsername(memberNameToRemove))){
		    return false;
	    }else{
		    TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
		    Tenant tenantToRemove = tenantRepository.findTenantByUsername(memberNameToRemove);
	    	if (!tenantGroup.getGroupOwner().getUsername().equals(groupName) || !tenantGroup.getTenants().contains(tenantToRemove)){
	    		return false;
		    }

		    tenantGroup.getTenants().remove(tenantToRemove);
	    	// Set owner if the removed member is owner
	    	if (groupOwnerName.equals(memberNameToRemove)){
	    		if (tenantGroup.getTenants().isEmpty()){
	    			return removeGroup(groupOwnerName, groupName);
			    }else{
	    			return changeGroupOwner(groupOwnerName, tenantGroup.getTenants().get(0).getUsername(), groupName);
			    }
		    }
	    	return true;
	    }
    }

    @Transactional
    public boolean removeGroup(String groupOwnerName, String groupName){
    	if (!(tenantGroupRepository.existsTenantGroupByGroupName(groupName) && tenantRepository.existsTenantByUsername(groupOwnerName))){
    		return false;
	    }else{
		    TenantGroup tenantGroup = tenantGroupRepository.findTenantGroupByGroupName(groupName);
    		if (!tenantGroup.getGroupOwner().getUsername().equals(groupOwnerName)){
    			return false;
		    }
			Tenant owner = tenantGroup.getGroupOwner();
    		owner.getRole().remove(roleRepository.findRoleByRole(OWNER_PREV_STRING));

    		tenantRepository.saveAndFlush(owner);
    		userRepository.saveAndFlush(owner);


    		tenantGroupRepository.deleteTenantGroupByGroupName(groupName);
    		tenantGroupRepository.flush();
    		return true;
	    }
    }
}
