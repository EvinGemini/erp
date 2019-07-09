package cn.itcast.erp.realm;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

public class ErpRealm extends AuthorizingRealm {
    private IEmpBiz empBiz;
    private IMenuBiz menuBiz;

    public void setEmpBiz(IEmpBiz empBiz) {
        this.empBiz = empBiz;
    }

    public void setMenuBiz(IMenuBiz menuBiz) {
        this.menuBiz = menuBiz;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("认证。。。。。。。。。。。");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String pwd = new String(token.getPassword());
        Emp emp = empBiz.findByUsernameAndPwd(token.getUsername(), pwd);
        if (null != emp) {  //认证成功
            return new SimpleAuthenticationInfo(emp,pwd,getName());
        }
        return null;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("授权。。。。。。。。。。。");
        //获取登陆用户
        Emp emp = (Emp) principalCollection.getPrimaryPrincipal();
        List<Menu> menus = menuBiz.getMenusByEmpuuid(emp.getUuid());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //遍历用户下的菜单权限
        for (Menu m : menus) {
            //添加权限
            authorizationInfo.addStringPermission(m.getMenuname());
        }
        return authorizationInfo;
    }
}
