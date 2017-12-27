package org.jsets.shiro.realm;

import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.jsets.shiro.config.ShiroProperties;
import org.jsets.shiro.model.Account;
import org.jsets.shiro.model.StatelessAccount;
import org.jsets.shiro.service.ShiroAccountProvider;

/**
 * 用户、名密码的控制域
 * 
 * @author wangjie (https://github.com/wj596) 
 * @date 2016年6月24日 下午2:55:15
 */
public class PasswdRealm extends AuthorizingRealm {
	
	private final ShiroAccountProvider accountProvider;
	
	public PasswdRealm(ShiroAccountProvider accountProvider){
		this.accountProvider = accountProvider;
	}

	public Class<?> getAuthenticationTokenClass() {
		return UsernamePasswordToken.class;
	}
	
	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		if(null==token.getPrincipal()||null==token.getCredentials()){
			throw new AuthenticationException(ShiroProperties.MSG_AUTHC_ERROR);
		}
		String account = (String) token.getPrincipal();
		Account accountEntity = this.accountProvider.loadAccount(account);
		if (null == accountEntity) {
			throw new AuthenticationException(ShiroProperties.MSG_AUTHC_ERROR);
		}
		return new SimpleAuthenticationInfo(account,accountEntity.getPassword(), getName());
	}

	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Object principal = principals.getPrimaryPrincipal();
		if(principal instanceof StatelessAccount) return null;
		SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
		String account = (String) principal;
		Set<String> roles = this.accountProvider.loadRoles(account);
		Set<String> permissions = this.accountProvider.loadPermissions(account);
		if(null!=roles&&!roles.isEmpty())
			info.setRoles(roles);
		if(null!=permissions&&!permissions.isEmpty())
			info.setStringPermissions(permissions);
        return info;  
	}
}